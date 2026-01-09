# 时间轮

此工具类共提供两种形式的时间轮  
`TIERED` 多层时间轮  
`SINGLE_CIRCLE` 单层计次时间轮

## 使用方式:
```
TimeWheelWrapper timeWheel = new TimeWheelWrapperBuilder()
        .setSlotSize(3)
        .setSlotMs(1000)
        .build(TimeWheelType.TIERED);


timeWheel.addTask(() -> System.out.println(1), 1000);
timeWheel.start();

// 设置停止
timeWheel.setStoped(true);
```