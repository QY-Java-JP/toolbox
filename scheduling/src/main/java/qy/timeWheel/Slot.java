package qy.timeWheel;

import java.util.LinkedList;

public class Slot {
    // 任务
    private final LinkedList<TimerTask> tasks = new LinkedList<>();

    public LinkedList<TimerTask> getTasks() {
        return tasks;
    }
}
