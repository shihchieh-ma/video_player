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

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alibaba.android.arouter.facade.annotation.Route;

import com.ijk.R;
import com.ijk.media.MediaController;
import com.ijk.media.PlayerController;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import com.ijk.media.IjkVideoView;

import tv.danmaku.ijk.media.player.utils.AutoChangeScreenUtils;

@Route(path = "/activity/视频")
public class VideoActivity extends AppCompatActivity {
    private static final String TAG = "VideoActivity";

    private String mVideoPath;
    private String mVideoName;
    private  static IjkVideoView mVideoView;
    private boolean mBackPressed;
    private static MediaController mediaController;
    private FrameLayout frameLayout;
    private boolean clean;

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


        if (null == mVideoView) {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
            Log.e("null == mVideoView", "-----------");

            mVideoPath = getIntent().getStringExtra("videoPath");
            mVideoName = getIntent().getStringExtra("videoTitle");
            mVideoView = new IjkVideoView(this);

            mVideoView.setLayoutParams(fl);
            frameLayout.addView(mVideoView);
            mediaController = new PlayerController(this);
            mediaController.setControllerView(mVideoView,this);
            mediaController.setScreenListener(new PlayerController.ScreenListener() {
                @Override
                public void changeScreen(Boolean stopIt) {
                    if (stopIt) {
                        AutoChangeScreenUtils.init(VideoActivity.this).stop();
                    } else {
                        AutoChangeScreenUtils.init(VideoActivity.this).start(VideoActivity.this);
                    }
                }

                @Override
                public void finishDontClean(Boolean clean) {
                    VideoActivity.this.clean = clean;
                    finish();
                }
            });
            mediaController.setVideoPathOrUri(mVideoPath, null);
            mediaController.setVideoName(mVideoName);
            mediaController.setlooping(false);
            mediaController.setWindowManageSupport(frameLayout, mVideoView);
            mediaController.start();
        }else {
            Log.e(TAG, "mediaController:" + mediaController);
            mediaController.setWindowManageSupport(frameLayout, mVideoView);
            mVideoView.setLayoutParams(fl);
            frameLayout.addView(mVideoView);
        }

    }

    @Override
    public void onBackPressed() {
        mBackPressed = true;
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        //frameLayout.removeView(mVideoView);
        //mediaController.uiInisibleButVideoPlaying();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!clean){
            Log.e(TAG, "clean");

            mVideoView = null;
            mediaController.release();
        }
        AutoChangeScreenUtils.init(this).stop();
    }
}