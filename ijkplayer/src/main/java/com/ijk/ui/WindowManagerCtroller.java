package com.ijk.ui;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.ijk.R;
import com.ijk.media.IjkVideoView;
import com.ijk.media.ViewController;

import tv.danmaku.ijk.media.player.utils.DPXUtils;


/**
 * @author majes
 * @date 11/23/17.
 */

public class WindowManagerCtroller implements View.OnClickListener {

    private Context context;
    private WindowManager windowManager;
    private volatile static WindowManagerCtroller windowManagerCtroller;
    private static android.view.WindowManager androidWindowManager;
    private static DisplayMetrics dm;
    private boolean wasCreated = false;
    private FrameLayout frameLayout;
    private IjkVideoView ijkVideoView;

    private WindowManagerCtroller(Context context) {
        this.context = context;
    }

    public static WindowManagerCtroller getWindowManagerCtroller(Context context) {
        if (null == windowManagerCtroller) {
            synchronized (WindowManagerCtroller.class) {
                if (null == windowManagerCtroller) {
                    windowManagerCtroller = new WindowManagerCtroller(context);
                    dm = new DisplayMetrics();
                    androidWindowManager = (android.view.WindowManager)
                            context.getSystemService(Context.WINDOW_SERVICE);
                    androidWindowManager.getDefaultDisplay().getMetrics(dm);
                }
            }
        }
        return windowManagerCtroller;
    }

    private ViewController viewController;
    public void createWindowView(ViewController viewController, IjkVideoView ijkVideoView) {
        if (wasCreated) {
            return;
        }
        this.viewController = viewController;
        this.ijkVideoView = ijkVideoView;
        View windowView = LayoutInflater.from(context).inflate(R.layout.window_view, null, false);
        frameLayout = windowView.findViewById(R.id.float_root_view);
        windowView.setOnClickListener(this);
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(
                DPXUtils.dip2px(context,320),
                DPXUtils.dip2px(context,240),
                Gravity.CENTER
        );
        ijkVideoView.setLayoutParams(fl);
        Log.e("getParent", "ijkVideoView.getParent():" + ijkVideoView.getParent());
        frameLayout.addView(ijkVideoView);
        this.windowManager = new WindowManager(context);
        WindowManager.Configs configs = new WindowManager.Configs();
        configs.floatingViewX = dm.widthPixels / 2;
        configs.floatingViewY = dm.heightPixels / 4;
        configs.overMargin = -(int) (8 * dm.density);
        this.windowManager.andWindowView(windowView, configs);
        wasCreated = !wasCreated;

    }

    @Override
    public void onClick(View v) {
        destoryWindowView();
        Intent i = new Intent(context,VideoActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        if (null != viewController){
            viewController.windowIn();
        }
    }

    private void destoryWindowView() {
        frameLayout.removeView(ijkVideoView);
        if (null != windowManager) {
            windowManager.removeAllWindowView();
            windowManager = null;
        }
        if (null != dm) {
            dm = null;
        }
        if (null != androidWindowManager) {
            androidWindowManager = null;
        }
        if (null != windowManagerCtroller) {
            windowManagerCtroller = null;
        }
        wasCreated = false;
    }

}
