package tv.danmaku.ijk.media.player.threadutils.task;

import android.util.Log;

import tv.danmaku.ijk.media.player.threadutils.thread.AsynThread;
import tv.danmaku.ijk.media.player.threadutils.thread.IThread;
import tv.danmaku.ijk.media.player.threadutils.thread.UiThread;


/**
 * Created by guohaiyang on 2017/6/30.
 */

public abstract class AbsTask implements Runnable {
    protected int delayTime = 0;
    //下一个任务
    private AbsTask afterTask;
    public static IThread MAIN;
    public static IThread ASYN;

    static {
        MAIN = new UiThread();
        ASYN = new AsynThread();
    }


    @Override
    public void run() {
        IThread thread = getThread();
        thread.run(new AbsTask() {
            @Override
            public void runInTask() {
                Log.v("ghy", "doOnNext");
            }

            @Override
            public void run() {
                //此处需要重写run方法，否则会死循环
                onNextTask();
            }
        }, delayTime);
    }

    private void onNextTask() {
        //执行自己的方法
        runInTask();
        //通知下个方法
        if (afterTask != null) {
            afterTask.run();
        }
    }

    public abstract void runInTask();

    public IThread getThread() {
        return MAIN;
    }

    public void onNext(AbsTask afterTask) {
        this.afterTask = afterTask;
    }
}
