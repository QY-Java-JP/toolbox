package qy.timeWheel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Executor;

public class SingleCircleTimeWheel implements TimeWheel{

    // 槽数量
    public final int slotSize;
    // 时间粒度 (槽之间间隔多少ms)
    private final long slotMs;
    // 当前指针位置
    private int point = -1;
    // 当前圈数
    private int roundCount = 0;
    // 槽数组
    private final Slot[] slots;
    // 执行任务的线程池
    private final Executor taskExecutor;

    SingleCircleTimeWheel(long slotMs, int slotSize, Executor taskExecutor){
        this.slotMs = slotMs;
        this.slotSize = slotSize;
        this.taskExecutor = taskExecutor;
        this.slots = new Slot[slotSize];

        init();
    }

    @Override
    public void move(long lowerTimeSum) {
        // 移动指针 圈数 检查任务
        if (point >= slotSize) {
            point = -1;
            roundCount++;

        } else point++;

        runNowTasks();
    }

    @Override
    public void addTask(TimerTask task) {
        // 除法放入
        final int slotIndex = Math.toIntExact(task.getTime() / slotMs);
        if (slotIndex >= slotSize) {return;}

        slots[slotIndex].getTasks().add(task);
    }

    // 执行当前时间的任务
    private void runNowTasks(){
        // 圈数 * 一轮的时间 + 当前指针时间
        final long now = roundCount * slotMs * slotSize + point * slotMs;
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
