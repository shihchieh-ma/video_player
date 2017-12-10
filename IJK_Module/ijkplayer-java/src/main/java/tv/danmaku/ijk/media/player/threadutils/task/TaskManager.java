package tv.danmaku.ijk.media.player.threadutils.task;

/**
 * Created by guohaiyang on 2017/4/10.
 */

public class TaskManager {

    //第一个任务
    private AbsTask firstTask = null;
    //上一个任务
    private AbsTask lastTask = null;

    private TaskManager() {

    }

    public static TaskManager getNewInstance() {
        return new TaskManager();
    }

    public void clearTask() {
        firstTask = null;
        lastTask = null;
    }

    public TaskManager onNext(AbsTask task) {
        if (firstTask == null) {
            lastTask = firstTask = task;
        } else {
            lastTask.onNext(task);
            lastTask = task;
        }

        return this;
    }

    public void start() {
        if (firstTask != null) {
            firstTask.run();
        } else {
            throw new NullPointerException("任务不能为空！");
        }

    }


}
