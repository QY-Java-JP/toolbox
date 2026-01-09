package qy.timeWheel;

public class TimerTask {
    // 任务
    private final Runnable task;
    // 执行的时间
    private final long time;

    public TimerTask(Runnable task, long time) {
        this.task = task;
        this.time = time;
    }

    public Runnable getTask() {
        return task;
    }

    public long getTime() {
        return time;
    }
}
