package tv.danmaku.ijk.media.player.threadutils;

import android.os.Handler;
import android.os.Looper;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tv.danmaku.ijk.media.player.threadutils.task.AbsTask;

/**
 * 线程工具，用于切换线程
 */
public class ThreadUtils {
    private static Handler handler = null;

    static {
        handler = new Handler(Looper.getMainLooper());//初始化handle，用于切换到主线程
    }

    private static ExecutorService mCacheExcutor = Executors.newCachedThreadPool();//初始化线程池，用于切换到线程

    /**
     * 执行在ui线程
     *
     * @param r
     */
    public static void runInUiThread(Runnable r) {
        if (handler != null) {
            handler.post(r);
        }
    }

    /**
     * 执行在线程
     *
     * @param r
     */
    public static void runInBackGroundThread(Runnable r) {
        if (mCacheExcutor != null) {
            mCacheExcutor.execute(r);
        }
    }


    public static void ondestroy() {
        if (mCacheExcutor != null) {
            mCacheExcutor.shutdown();
        }
    }

    public static void runInUi(AbsTask task, int delay) {
        if (handler != null) {
            handler.postDelayed(task, delay);
        }
    }

    public static void runInBack(final AbsTask task, final int delay) {
        if (mCacheExcutor != null) {
            mCacheExcutor.execute(new Runnable() {
                @Override
                public void run() {
                    //延迟
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    task.run();
                }
            });
        }
    }


}