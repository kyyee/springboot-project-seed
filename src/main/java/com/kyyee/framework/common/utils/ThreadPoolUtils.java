package com.kyyee.framework.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程池工具类
 * 借用newCachedThreadPool特性实现线程自动回收
 * 借用synchronized特性实现限流控制
 * 建议项目中所有多线程编程，均使用此类的execute方法
 */
@Slf4j
public class ThreadPoolUtils {
    private static final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private static final ThreadPoolExecutor cachedThreadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    private static int count = 0;

    /**
     * 单任务耗时        NORMAL(cc/ts)      FAST(cc/ts)
     * 1s                    717/897         1857/2334
     * 500ms                1005/628         2701/1617
     * 200ms                1373/397         4515/984
     * 100ms                2586/315         6485/716
     */
    public static void execute(Runnable runnable) {
        execute(runnable, ConcurrentSpeed.NORMAL);
    }

    /**
     * 异步列队执行，超高吞吐，不阻塞，容易耗尽资源
     *
     * @param runnable
     */
    public static void executeAsyn(Runnable runnable) {
        executeAsyn(runnable, ConcurrentSpeed.NORMAL);
    }

    public static void executeAsyn(Runnable runnable, ConcurrentSpeed speed) {
        singleThreadExecutor.execute(() -> execute(runnable, speed));
    }

    /**
     * 线程池活跃数向右偏移10位，每1024活跃=1ms休眠
     * 同步休眠是为了压制并发量，避免资源耗尽
     *
     * @param runnable
     * @param speed
     */
    public static synchronized void execute(Runnable runnable, ConcurrentSpeed speed) {
        cachedThreadPool.execute(runnable);

        int mod = 2;
        int move = 8;
        if (ConcurrentSpeed.FAST == speed) {
            mod = 16;
//        } else if (ConcurrentSpeed.SLOW == speed) {
//            move = 4;
        }

        if (count++ % mod == 0) {
            int activeCount = cachedThreadPool.getActiveCount() >> move;
            if (activeCount > 0) {
                log.debug("activeCount:{}", activeCount);
                ThreadUtils.sleep(activeCount);
            }

        }
    }

    public static void main(String[] args) {
        AtomicLong ct = new AtomicLong();
        long start = System.currentTimeMillis();
        final Set<String> set = new CopyOnWriteArraySet<>();
        for (int i = 0; i < 10000; i++) {
//            execute
            executeAsyn(() -> {
                int r = 4500 + new Random().nextInt(2000);
                ThreadUtils.sleep(r);
                set.add(Thread.currentThread().getName());
                long time = (System.currentTimeMillis() - start);
                if (ct.incrementAndGet() == 10000) {
                    log.info("{}\t{}\t{}", set.size(), time, ct.get() * 1000 / time);
                }

            }, ConcurrentSpeed.FAST);
        }
        log.info("use time:{}", System.currentTimeMillis() - start);
    }

    public enum ConcurrentSpeed {
        //        SLOW,
        NORMAL, FAST
    }
}
