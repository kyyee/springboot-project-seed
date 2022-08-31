package com.kyyee.framework.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

/**
 * 生成snowflakeID snowflake 64bit Long:
 * 0      00000000000000000000000000000000000000000   000000       000000      0000000000
 * 站位(1)            时间戳(41)                      机器号(6)     版本号(6)     序列号(10)
 * 机器号配置在配置文件application.properties中 如果启动或者生成snowflakeID时发生时间回调，版本号自增
 */
@Slf4j
public final class SnowFlake {

    // ==============================constants===========================================

    /**
     * 数据中心（机房）ID(0-31)
     */
    private static final long DATA_CENTER_ID = 0L;

    /**
     * 机器ID(0-31)
     */
    private static final long WORKER_ID = 0L;


    // ==============================Fields===========================================
    /**
     * 开始时间截 (2022/02/23 16:59:48)
     */
    private static final long TWEPOCH = 1645606788000L;

    /**
     * 数据中心在id所占的位数
     */
    private static final long DATA_CENTER_ID_BITS = 5L;

    /**
     * 机器在id所占的位数
     */
    private static final long WORKER_ID_BITS = 5L;

    /**
     * 支持的最大数据中心id，结果是31
     */
    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);

    /**
     * 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    /**
     * 序列在id中占的位数，同一毫秒最多生成4096个
     */
    private static final long SEQUENCE_BITS = 12L;

    /**
     * 机器id向左移12位
     */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 数据中心id向左移17位(12+5)
     */
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    /**
     * 时间截向左移22位(5+5+12)
     */
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private volatile static SnowFlake singleton = null;

    /**
     * 数据中心ID(0~31)
     */
    private final long dataCenterId;

    /**
     * 机器ID(0~31)
     */
    private final long workerId;
    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;

    //==============================Constructors=====================================
    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    // ==============================Methods==========================================

    /**
     * 构造函数
     *
     * @param workerId     工作ID (0~31)
     * @param dataCenterId 数据中心ID (0~31)
     */
    private SnowFlake(long workerId, long dataCenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0",
                MAX_WORKER_ID));
        }
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("dataCenter Id can't be greater than %d or less than 0",
                MAX_DATA_CENTER_ID));
        }
        log.debug("worker starting. timestamp left shift {}, datacenter id bits {}, worker id bits {}, sequence bits {}, workerid {}",
            TIMESTAMP_LEFT_SHIFT, DATA_CENTER_ID_BITS, WORKER_ID_BITS, SEQUENCE_BITS, workerId);

        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    public static long getId() {
        if (singleton == null) {
            synchronized (SnowFlake.class) {
                if (singleton == null) {
                    singleton = new SnowFlake(WORKER_ID, DATA_CENTER_ID);
                }
            }
        }
        return singleton.nextId();
    }

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    private synchronized long nextId() {
        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                lastTimestamp - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT)
            | (dataCenterId << DATA_CENTER_ID_SHIFT)
            | (workerId << WORKER_ID_SHIFT)
            | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        //如果发现最新的时间戳小于或者等于序列号已经超4095的那个时间戳
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    private long timeGen() {
        return Instant.now().toEpochMilli();
    }
}
