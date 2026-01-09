package qy.timeWheel;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TimeWheelWrapperBuilder {

    // 槽数
    private int slotSize = 60;
    // 时间间隔
    private long slotMs = 1000;

    // 构建单层时间轮
    private TimeWheel createSingleWheel(){
        return new SingleCircleTimeWheel(slotMs, slotSize, Executors.newSingleThreadExecutor());
    }

    // 构建多层时间轮
    private TimeWheel createTieredWheel(int count){
        // 循环构建 然后返回最下层的
        final Executor executor = Executors.newSingleThreadExecutor();
        TieredTimeWheel superWheel = new TieredTimeWheel(slotMs, slotSize, executor, null);
        for (int i = 1; i < count; i++) {
            superWheel = new TieredTimeWheel(slotMs, slotSize, executor, superWheel);
        }

        return superWheel;
    }

    // build方法
    public TimeWheelWrapper build(TimeWheelType timeWheelType){
        // 策略
        TimeWheel selectedWheel = createSingleWheel();
        if (TimeWheelType.TIERED == timeWheelType) {
            selectedWheel = createTieredWheel(3);
        } else if (TimeWheelType.SINGLE_CIRCLE == timeWheelType) {
            selectedWheel = createSingleWheel();
        }

        return new TimeWheelWrapper(slotMs, selectedWheel);
    }

    public TimeWheelWrapperBuilder setSlotSize(int slotSize){
        this.slotSize = slotSize;
        return this;
    }

    public TimeWheelWrapperBuilder setSlotMs(long slotMs){
        this.slotMs = slotMs;
        return this;
    }
}
