package com.kyyee.sps.common.component.task.manager;

import com.kyyee.sps.common.enums.TaskFinishEnum;
import com.kyyee.sps.model.BaseTaskEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayTask<T extends BaseTaskEntity> implements Delayed {
    @Getter
    private Long delayTime;

    private int retry;

    @Setter
    private int delayMaxTime;

    @Getter
    private final T taskData;

    public DelayTask(T taskData) {
        this.delayTime = Instant.now().toEpochMilli();
        this.taskData = taskData;
    }

    public void resetDelay() {
        long delay = (long) Math.pow(2, retry);
        if (delay > delayMaxTime) {
            delay = delayMaxTime;
        }
        this.delayTime = delay * 1000L + Instant.now().toEpochMilli();
    }

    public boolean finish() {
        return TaskFinishEnum.YES.equals(taskData.getFinish());
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(delayTime - Instant.now().toEpochMilli(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return Long.compare(delayTime, ((DelayTask<?>) o).delayTime);
    }
}
