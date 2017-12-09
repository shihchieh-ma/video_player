package com.ijk.media;

import android.app.Activity;
import android.widget.FrameLayout;

import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * @author majes
 * @date 12/10/17.
 */

public interface MediaController{
    /**
     * 循环播放
     * @param aboolean
     */
    void setlooping(boolean aboolean);

    /**
     * 设置player
     * @param iMediaPlayer
     */
    void setVideoPlayer(final IMediaPlayer iMediaPlayer);

    /**
     * 开始播放
     */
    void start();

    /**
     * 切后台
     */
    void uiInisibleButVideoPlaying();

    /**
     * 释放资源
     */
    void release();

    /**
     * 设置ijkvideoview
     * @param viewController
     */
    void setControllerView(ViewController viewController,Activity activity);

    /**
     * 文件路劲
     * @param path
     * @param headers
     */
    void setVideoPathOrUri(String path, Map<String, String> headers);

    /**
     * 文件名
     * @param name
     */
    void setVideoName(String name);

    /**
     * 旋转回调
     * @param screenListener
     */
    void setScreenListener(PlayerController.ScreenListener screenListener);

    void setWindowManageSupport(FrameLayout frameLayout, IjkVideoView ijkVideoView);

    void setVisible();
}
