'use strict';

class Snowflake {
    static dataCenterId = 0n;
    static workerId = 0n;
    static twepoch = 1661906860000n;
    static dataCenterIdBits = 5n;
    static workerIdBits = 5n;
    static maxDataCenterId = -1n ^ ( - 1n << Snowflake.dataCenterIdBits); // 值为：31
    static maxWorkerId = -1n ^ ( - 1n << Snowflake.workerIdBits); // 值为：31
    static sequenceBits = 12n;
    static workerIdShift = Snowflake.sequenceBits; // 值为：12
    static dataCenterIdShift = Snowflake.sequenceBits + Snowflake.workerIdBits; // 值为：17
    static timestampLeftShift = Snowflake.sequenceBits + Snowflake.workerIdBits + Snowflake.dataCenterIdBits; // 值为：22
    static sequenceMask = -1n ^ ( - 1n << Snowflake.sequenceBits); // 值为：4095

    static singleton;

    constructor(workerId, dataCenterId) {
        //设置默认值,从环境变量取
        this.dataCenterId = 1n;
        this.workerId = 1n;
        this.sequence = 0n;
        this.lastTimestamp = -1n;

        if (workerId > Snowflake.maxWorkerId || workerId < 0) {
            throw new Error('_worker Id can\'t be greater than maxWorkerId-[' + Snowflake.maxWorkerId + '] or less than 0');
        }
        if (dataCenterId > Snowflake.maxDataCenterId || dataCenterId < 0) {
            throw new Error('dataCenter Id  can\'t be greater than maxDataCenterId-[' + Snowflake.maxDataCenterId + '] or less than 0');
        }

        console.debug('worker starting. timestamp left shift ' + Snowflake.timestampLeftShift + ', datacenter id bits ' + Snowflake.dataCenterIdBits + ', worker id bits '
        + Snowflake.workerIdBits + ', sequence bits ' + Snowflake.sequenceBits + ', worker id ' + Snowflake.workerId);
        this.workerId = BigInt(workerId);
        this.dataCenterId = BigInt(dataCenterId);
    }

    static getId() {
        if (!Snowflake.singleton) {
            Snowflake.singleton = new Snowflake(Snowflake.dataCenterId, Snowflake.workerId);
        }
        return Snowflake.singleton._nextId();
    }

    _nextId = function() {
        let timestamp = this._timeGen();
        if (timestamp < this.lastTimestamp) {
            throw new Error('Clock moved backwards.  Refusing to generate id for ' + (this.lastTimestamp - timestamp) + 'milliseconds');
        }

        if (this.lastTimestamp === timestamp) {
            this.sequence = (this.sequence + 1n) & Snowflake.sequenceMask;
            if (this.sequence === 0n) {
                timestamp = this._tilNextMillis(this.lastTimestamp);
            }
        } else {
            this.sequence = 0n;
        }
        this.lastTimestamp = timestamp;
        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - Snowflake.twepoch) << Snowflake.timestampLeftShift)
         | (this.dataCenterId << Snowflake.dataCenterIdShift)
         | (this.workerId << Snowflake.workerIdShift)
         | this.sequence;
    };

    _tilNextMillis(lastTimestamp) {
        let timestamp = this._timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this._timeGen();
        }
        return BigInt(timestamp);
    };

    _timeGen() {
        return BigInt(Date.now());
    };
}

export { Snowflake };
