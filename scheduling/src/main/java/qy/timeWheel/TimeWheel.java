package qy.timeWheel;

public interface TimeWheel {
    // 转动
    void move(long lowerTimeSum);
    // 添加任务
    void addTask(TimerTask task);
}
