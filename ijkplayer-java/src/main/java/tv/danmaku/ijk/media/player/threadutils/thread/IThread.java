package tv.danmaku.ijk.media.player.threadutils.thread;


import tv.danmaku.ijk.media.player.threadutils.task.AbsTask;

/**
 * Created by guohaiyang on 2017/6/30.
 */

public interface IThread {
    public void run(AbsTask task, int delay);
}
