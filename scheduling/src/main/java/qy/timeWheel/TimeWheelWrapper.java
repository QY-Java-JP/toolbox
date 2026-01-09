package qy.timeWheel;

public class TimeWheelWrapper implements Runnable {

    // 时间间隔
    private final long slotMs;
    // 时间轮
    private final TimeWheel timeWheel;
    // 运行线程
    private final Thread wheelThread = new Thread(this);

    // 是否停止了
    private boolean stoped = false;

    TimeWheelWrapper(long slotMs, TimeWheel timeWheel){
        this.slotMs = slotMs;
        this.timeWheel = timeWheel;
    }

    @Override
    public void run() {
        while (!stoped) {
            timeWheel.move(0);

            try {
                Thread.sleep(slotMs);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 添加任务
    public void addTask(Runnable task, long ms){
        timeWheel.addTask(new TimerTask(task, ms));
    }

    // 开启
    public void start(){
        wheelThread.setDaemon(true);
        wheelThread.start();
    }

    public void setStoped(boolean stoped) {
        this.stoped = stoped;
    }
}
