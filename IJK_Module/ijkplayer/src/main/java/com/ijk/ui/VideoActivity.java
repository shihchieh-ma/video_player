/*
 * Copyright (C) 2015 Bilibili
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ijk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alibaba.android.arouter.facade.annotation.Route;

import com.ijk.R;
import com.ijk.media.IMediaController;
import com.ijk.media.PlayerController;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import com.ijk.media.IjkVideoView;

import tv.danmaku.ijk.media.player.utils.AutoChangeScreenUtils;

@Route(path = "/activity/视频")
public class VideoActivity extends AppCompatActivity {
    private static final String TAG = "VideoActivity";

    private String mVideoPath;
    private String mVideoName;
    private static IjkVideoView mVideoView;
    private IMediaController.MediaController mediaController;
    private FrameLayout frameLayout;
    private boolean clean = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        frameLayout = findViewById(R.id.activity_framelayout);
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER
        );
        AutoChangeScreenUtils.init(this).start(this);

        // init player

        mediaController = IMediaController.getMediaController(getApplication());
        mediaController.setScreenListener(new PlayerController.ScreenListener() {
            //                锁定屏幕
            @Override
            public void changeScreen(Boolean stopIt) {
                if (stopIt) {
                    AutoChangeScreenUtils.init(VideoActivity.this).stop();
                } else {
                    AutoChangeScreenUtils.init(VideoActivity.this).start(VideoActivity.this);
                }
            }

            //              切换至悬浮窗了
            @Override
            public void finishDontClean(Boolean clean) {
                VideoActivity.this.clean = clean;
                finish();
            }
        });
        if (null == mVideoView) {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
            mVideoPath = getIntent().getStringExtra("videoPath");
            mVideoName = getIntent().getStringExtra("videoTitle");
            mVideoView = new IjkVideoView(this);
            mVideoView.setLayoutParams(fl);
            mediaController.setControllerView(mVideoView, this);
            mediaController.setVideoPathOrUri(mVideoPath, null);
            mediaController.setVideoName(mVideoName);
            mediaController.setlooping(false);
            mediaController.setWindowManageSupport(frameLayout, mVideoView);
            frameLayout.addView(mVideoView);
            mediaController.start();
        } else {
            mediaController.setControllerView(mVideoView, this);
            mediaController.setWindowManageSupport(frameLayout, mVideoView);
            mVideoView.setLayoutParams(fl);
            frameLayout.addView(mVideoView);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        quitPlayAndFinish();
    }

    private void quitPlayAndFinish() {
        if (null != mediaController) {
            mediaController.release();
            mediaController = null;
            mVideoView = null;
        }
        if (null != mVideoName) {
            mVideoName = null;
        }
        if (null != mVideoPath) {
            mVideoPath = null;
        }
        IMediaController.cleanIMediaController();
        finish();
    }

    @Override
    protected void onStop() {
        if (null != mediaController) {
            mediaController.uiInisibleButVideoPlaying();
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (clean) {
            quitPlayAndFinish();
        }
        AutoChangeScreenUtils.init(this).stop();
    }
}