package qy.timeWheel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Executor;

public class TieredTimeWheel implements TimeWheel {

    // 槽数量
    public final int slotSize;
    // 时间粒度 (槽之间间隔多少ms)
    private final long slotMs;
    // 当前指针位置
    private int point = -1;
    // 槽数组
    private final Slot[] slots;
    // 下一层时间轮
    private final TieredTimeWheel nextWheel;
    // 执行任务的线程池
    private final Executor taskExecutor;

    TieredTimeWheel(long slotMs, int slotSize, Executor taskExecutor, TieredTimeWheel nextWheel){
        this.slotMs = slotMs;
        this.nextWheel = nextWheel;
        this.taskExecutor = taskExecutor;
        this.slotSize = slotSize;
        this.slots = new Slot[slotSize];

        init();
    }

    // 转动
    @Override
    public void move(long lowerTimeSum){
        // 首先移动 然后检查任务
        if (point >= slotSize) {
            // 回归 并且下一个时间轮+1
            point = -1;
            if (nextWheel != null) {
                nextWheel.move(lowerTimeSum);
            }

        } else point++;

        final long now = slotMs * point + lowerTimeSum;
        runNowTasks(now);
    }

    // 添加任务
    @Override
    public void addTask(TimerTask task){
        // 排除最小的
        if (task.getTime() < slotMs) {return;}

        // 检查是否需要交给上级
        if (task.getTime() > slotMs * slotSize) {
            if (nextWheel != null) {
                nextWheel.addTask(task);
            }

            return;
        }

        // 除法放入
        final int slotIndex = Math.toIntExact(task.getTime() / slotMs);
        if (slotIndex >= slotSize) {return;}

        slots[slotIndex].getTasks().add(task);
    }

    // 执行当前时间的任务
    private void runNowTasks(long now){
        // 首先拿到槽位 然后对别下时间 如果到了则扔线程池里执行
        final LinkedList<TimerTask> tasks = slots[point].getTasks();
        final Iterator<TimerTask> iterator = tasks.iterator();

        TimerTask task;
        while (iterator.hasNext()) {
            task = iterator.next();
            if (task.getTime() == now) {
                taskExecutor.execute(task.getTask());
                tasks.remove(task);
            }
        }
    }

    // 初始化
    private void init(){
        for (int i = 0; i < slots.length; i++) {
            slots[i] = new Slot();
        }
    }
}
