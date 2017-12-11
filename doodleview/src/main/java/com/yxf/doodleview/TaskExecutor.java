package com.yxf.doodleview;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Created by quehuang.du on 2017/12/10.
 */

public class TaskExecutor {

    public static final int INIT_TASK_PRIORITY = 0;
    public static final int SYSTEM_TASK_PRIORITY = 10;
    public static final int USER_TASK_PRIORITY = 100;


    private static Comparator<TaskParams> comparator = new Comparator<TaskParams>() {
        @Override
        public int compare(TaskParams lhs, TaskParams rhs) {
            return lhs.getPriority() - rhs.getPriority();
        }
    };

    private PriorityQueue<TaskParams> queue = new PriorityQueue<TaskParams>(Byte.MAX_VALUE, comparator);

    public boolean addTask(Runnable task, int priority) {
        return queue.offer(new TaskParams(task, priority));
    }

    public void cancelTask(Runnable task) {
        Iterator<TaskParams> iterator = queue.iterator();
        while (iterator.hasNext()) {
            TaskParams params = iterator.next();
            if (params.getTask() == task) {
                iterator.remove();
            }
        }
    }

    public void execute() {
        TaskParams params;
        while (true) {
            params = queue.poll();
            if (params == null) {
                break;
            }
            params.getTask().run();
        }
    }


    public static class TaskParams{

        private Runnable task;
        private int priority;

        public TaskParams(Runnable task, int priority) {
            this.task = task;
            this.priority = priority;
        }

        public Runnable getTask() {
            return task;
        }

        public int getPriority() {
            return priority;
        }
    }
}
