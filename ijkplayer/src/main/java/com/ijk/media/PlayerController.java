package com.ijk.media;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Map;

import com.ijk.R;
import com.ijk.services.MediaPlayerService;
import com.ijk.ui.WindowManagerCtroller;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.utils.ReBuildTimeUtils;
import tv.danmaku.ijk.media.player.utils.ThreadPoolUtils;
import tv.danmaku.ijk.media.player.utils.VideoUtils;
import tv.danmaku.ijk.media.player.utils.WindowUtils;

/**
 * @author majes
 * @date 12/8/17.
 */

public class PlayerController extends FrameLayout implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, View.OnTouchListener, IMediaController.MediaController {

    public static final int KEEPGOING = 0x001;
    public static final int START = 0x003;
    public static final int STOP = 0x002;
    public static final int VIEWINVISIBLE = 0x003;
    private final String TAG = getClass().getName();
    private static final int VID = 0x00F;
    private Context context;
    private CheckBox lockScreen;
    private TextView vName;
    private CheckBox pauseStart;
    private TextView currentTv;
    private SeekBar seekBar;
    private TextView totalTv;
    private IMediaPlayer iMediaPlayer;
    private final int THRESHOLD = 80;
    private long tempDown = 0;
    private long tempUp = 0;
    private float mDownX;
    private float mDownY;
    private boolean mChangeVolume;
    private boolean mChangePosition;
    private boolean mChangeBrightness;
    private int mGestureDownPosition;
    private float mGestureDownBrightness;
    private int mGestureDownVolume;
    private long mSeekTimePosition;
    private int mScreenWidth;
    private int mScreenHeight;
    private AudioManager mAudioManager;
    private Dialog mProgressDialog;
    private SeekBar mDialogProgressBar;
    private TextView mDialogSeekTime;
    private TextView mDialogTotalTime;
    private ImageView mDialogIcon;
    private Dialog mVolumeDialog;
    private ImageView mDialogVolumeImageView;
    private TextView mDialogVolumeTextView;
    private ProgressBar mDialogVolumeProgressBar;
    private Dialog mBrightnessDialog;
    private TextView mDialogBrightnessTextView;
    private ProgressBar mDialogBrightnessProgressBar;
    private ProgressBar progressBar;
    private int progress;
    private long temp;
    private boolean isSeekBarChange;
    private boolean isPlayFinish;
    private ImageView changescreen;
    private ImageView smallscreen;
    private View v;
    private ViewController viewController;
    private Activity activity;
    private Handler mainHandler;
    private HandlerThread ht;
    private Handler handler;
    private IjkVideoView ijkVideoView;
    private FrameLayout baseFramelayout;
    private String tempCurrent = "00:00";

    public PlayerController(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        v = LayoutInflater.from(context).inflate(R.layout.controllerview, this, true);
        v.setId(VID);
        v.setOnTouchListener(this);
        v.setOnClickListener(this);
        lockScreen = findViewById(R.id.lockScreen);
        progressBar = findViewById(R.id.temp_progressbar);
        vName = findViewById(R.id.video_name);
        vName.setSelected(true);
        lockScreen.setOnClickListener(this);
        pauseStart = findViewById(R.id.startorpause);
        pauseStart.setOnClickListener(this);
        currentTv = findViewById(R.id.current);
        currentTv.setOnClickListener(this);
        seekBar = findViewById(R.id.bottom_seek_progress);
        seekBar.setOnTouchListener(this);
        //seekBar.setOnSeekBarChangeListener(this);
        totalTv = findViewById(R.id.total);
        totalTv.setOnClickListener(this);
        changescreen = findViewById(R.id.changescreen);
        changescreen.setOnClickListener(this);
        smallscreen = findViewById(R.id.smallscreen);
        smallscreen.setOnClickListener(this);
    }

    @Override
    public void setlooping(boolean aboolean) {
        iMediaPlayer.setLooping(aboolean);
    }

    @Override
    public void setVideoPlayer(final IMediaPlayer iMediaPlayer) {
        this.iMediaPlayer = iMediaPlayer;
        mainHandler = new Handler(Looper.getMainLooper());
        ht = new HandlerThread("refreshCurrent");
        ht.start();
        handler = new Handler(ht.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (iMediaPlayer.getDuration() == 0 || currentTv == null) {
                    return;
                }

                if (msg.what == START) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            seekBar.setProgress(0);
                            pauseStart.setChecked(false);
                        }
                    });
                }
                if (msg.what == KEEPGOING) {
                    handler.sendEmptyMessageDelayed(KEEPGOING, 1000);
                }

                if (msg.what == STOP) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isViewVisible) {
                                invisibleView();
                                isViewVisible = !isViewVisible;
                            }
                            isPlayFinish = true;
                            pauseStart.setChecked(true);

                        }
                    });
                    handler.removeMessages(KEEPGOING);
                }
//                if (msg.what == VIEWINVISIBLE) {
//                   invisibleView();
//                }
                //我用oppo r9s测试这里总是有问题的，ijkplayer issues里也有这个问题，暂未解决，我这里
                //老是差1s左右，先手动加上了。就看ijk怎么解决了。不同机型或者视频可能会有差异。
                tempCurrent = ReBuildTimeUtils.buildTime(
                        iMediaPlayer.getCurrentPosition() + 1000);
                progress = Integer.parseInt(String.valueOf(((iMediaPlayer.getCurrentPosition() + 1000) * 100 / iMediaPlayer.getDuration())));
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        currentTv.setText(tempCurrent);
                        if (progress > 100) {
                            seekBar.setProgress(100);
                        } else {
                            seekBar.setProgress(progress);
                        }
                    }
                });

            }
        };

        viewController.onHandler(handler);
    }

    @Override
    public void start() {
        iMediaPlayer.start();
        totalTv.setText(ReBuildTimeUtils.buildTime(iMediaPlayer.getDuration()));
        progressBar.setVisibility(GONE);
    }

    @Override
    public void uiInisibleButVideoPlaying() {
        MediaPlayerService.setMediaPlayer(iMediaPlayer);
        IjkMediaPlayer.native_profileEnd();
    }

    @Override
    public void release() {
        if (iMediaPlayer != null) {
            iMediaPlayer.reset();
            iMediaPlayer.release();
            iMediaPlayer = null;
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
        ht.getLooper().quit();
        if (mainHandler != null) {
            mainHandler.removeMessages(STOP);
            mainHandler.removeMessages(START);
            mainHandler = null;
        }
       ThreadPoolUtils.execute(new Runnable() {
           @Override
           public void run() {
               if (null != v) {
                   lockScreen = null;
                   vName = null;
                   pauseStart = null;
                   currentTv = null;
                   seekBar = null;
                   totalTv = null;
                   mProgressDialog = null;
                   mDialogProgressBar = null;
                   mDialogSeekTime = null;
                   mDialogTotalTime = null;
                   mDialogIcon = null;
                   mVolumeDialog = null;
                   mDialogVolumeImageView = null;
                   mDialogVolumeTextView = null;
                   mDialogVolumeProgressBar = null;
                   mBrightnessDialog = null;
                   mDialogBrightnessTextView = null;
                   mDialogBrightnessProgressBar = null;
                   progressBar = null;
                   changescreen = null;
                   smallscreen = null;
                   viewController = null;
                   activity = null;
               }
               if (null != ijkVideoView){
                   ijkVideoView = null;
               }
               if (null != baseFramelayout){
                   baseFramelayout = null;
               }
           }
       });
    }


    @Override
    public void setControllerView(ViewController viewController, Activity activity) {
        this.activity = activity;
        WindowManager.LayoutParams lp = activity.getWindow()
                .getAttributes();
        lp.alpha = 1f;
        activity.getWindow().setAttributes(lp);
        mScreenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = activity.getResources().getDisplayMetrics().heightPixels;
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        this.viewController = viewController;
        viewController.setPlayerController(this);
    }

    @Override
    public void setVideoPathOrUri(String path, Map<String, String> headers) {
        if (null == viewController) {
            return;
        }
        viewController.setPath(path, headers);
    }

    @Override
    public void setVideoName(String name) {
            vName.setText(name);
    }


    private boolean isViewVisible = true;

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == VID) {
            invisibleView();
            isViewVisible = !isViewVisible;
        }

        if (viewId == R.id.startorpause) {
            if (iMediaPlayer.isPlaying()) {
                iMediaPlayer.pause();
            } else {
                totalTv.setText(ReBuildTimeUtils.buildTime(iMediaPlayer.getDuration()));
                if (isPlayFinish) {
                    handler.sendEmptyMessage(KEEPGOING);
                    currentTv.setText("00:00");
                    isPlayFinish = !isPlayFinish;
                }
                iMediaPlayer.start();
            }

        }

        if (viewId == R.id.lockScreen) {
            if (null == screenListener) {
                return;
            }
            if (lockScreen.isChecked()) {
                screenListener.changeScreen(true);
            } else {
                screenListener.changeScreen(false);
            }
        }


        if (viewId == R.id.smallscreen) {
            if (lockScreen.isChecked()) {
                return;
            }
            if (WindowUtils.checkFloatWindowPermission(context)) {
                baseFramelayout.removeView(ijkVideoView);
                viewController.setWindowManager();
                if (null == screenListener) {
                    return;
                }
                if (null != activity) {
                    activity = null;
                }
                screenListener.finishDontClean(false);
                WindowManagerCtroller.getWindowManagerCtroller(context).createWindowView(viewController, ijkVideoView);
//                if (isCreate){
//                    screenListener.finishDontClean(false);
//                }else {
//                    screenListener.finishDontClean(true);
//                }
            } else {
                WindowUtils.showDialogTipUserRequestPermission(activity);
            }
        }
        if (viewId == R.id.changescreen) {
            if (lockScreen.isChecked()) {
                return;
            }
            viewController.toggleAspectRatio();
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        if (isSeekBarChange){
//            temp = progress * iMediaPlayer.getDuration() / 100;
//            currentTv.setText(ReBuildTimeUtils.buildTime(iMediaPlayer.getCurrentPosition()));
//            iMediaPlayer.seekTo(temp);
//        }else {
            temp = progress * iMediaPlayer.getDuration() / 100;
            currentTv.setText(ReBuildTimeUtils.buildTime(iMediaPlayer.getCurrentPosition()));
            iMediaPlayer.seekTo(temp);
//        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (v.getId() == R.id.bottom_seek_progress){
            if (lockScreen.isChecked()) {
                return false;
            }
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                isSeekBarChange = true;
            }
            if (event.getAction() == MotionEvent.ACTION_UP){
                isSeekBarChange = false;
            }
            return false;
        }

        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                tempDown = System.currentTimeMillis();
                mDownX = x;
                mDownY = y;
                mChangeVolume = false;
                mChangePosition = false;
                mChangeBrightness = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - mDownX;
                float deltaY = y - mDownY;
                float absDeltaX = Math.abs(deltaX);
                float absDeltaY = Math.abs(deltaY);
                if (!mChangePosition && !mChangeVolume && !mChangeBrightness) {
                    if (absDeltaX > THRESHOLD || absDeltaY > THRESHOLD) {
                        if (absDeltaX >= THRESHOLD) {
                            mChangePosition = true;
                            mGestureDownPosition = (int) (iMediaPlayer.getCurrentPosition() / iMediaPlayer.getDuration());
                        } else {
                            if (mDownX < mScreenWidth * 0.5f) {
                                mChangeBrightness = true;
                                WindowManager.LayoutParams lp = VideoUtils.getWindow(activity).getAttributes();
                                if (lp.screenBrightness < 0) {
                                    try {
                                        try {
                                            mGestureDownBrightness = android.provider.Settings.System.getInt(
                                                    context.getContentResolver(),
                                                    android.provider.Settings.System.SCREEN_BRIGHTNESS);
                                        } catch (android.provider.Settings.SettingNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                        Log.i(TAG, "current system brightness: " + mGestureDownBrightness);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    mGestureDownBrightness = lp.screenBrightness * 255;
                                    Log.i(TAG, "current activity brightness: " + mGestureDownBrightness);
                                }
                            } else {
                                mChangeVolume = true;
                                mGestureDownVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            }
                        }
                    }
                }

                if (mChangePosition) {
                    iMediaPlayer.pause();
                    long totalTimeDuration = iMediaPlayer.getDuration();
                    mSeekTimePosition = (long) (mGestureDownPosition + deltaX * totalTimeDuration / seekBar.getWidth());
                    if (mSeekTimePosition > totalTimeDuration) {
                        mSeekTimePosition = totalTimeDuration;
                    }
                    String seekTime = ReBuildTimeUtils.buildTime(mSeekTimePosition);
                    showProgressDialog(deltaX, seekTime, mSeekTimePosition, totalTimeDuration);
                }
                if (mChangeVolume) {
                    deltaY = -deltaY;
                    int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    int deltaV = (int) (max * deltaY * 3 / mScreenHeight);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mGestureDownVolume + deltaV, 0);
                    int volumePercent = (int) (mGestureDownVolume * 100 / max + deltaY * 3 * 100 / mScreenHeight);
                    showVolumeDialog(volumePercent);
                }

                if (mChangeBrightness) {
                    deltaY = -deltaY;
                    int deltaV = (int) (255 * deltaY * 3 / mScreenHeight);
                    WindowManager.LayoutParams params = VideoUtils.getWindow(activity).getAttributes();
                    if (((mGestureDownBrightness + deltaV) / 255) >= 1) {
                        params.screenBrightness = 1;
                    } else if (((mGestureDownBrightness + deltaV) / 255) <= 0) {
                        params.screenBrightness = 0.01f;
                    } else {
                        params.screenBrightness = (mGestureDownBrightness + deltaV) / 255;
                    }
                    VideoUtils.getWindow(activity).setAttributes(params);
                    int brightnessPercent = (int) (mGestureDownBrightness * 100 / 255 + deltaY * 3 * 100 / mScreenHeight);
                    showBrightnessDialog(brightnessPercent);
                }
                break;
            case MotionEvent.ACTION_UP:
                tempUp = System.currentTimeMillis();
                if (!iMediaPlayer.isPlaying()) {
                    totalTv.setText(ReBuildTimeUtils.buildTime(iMediaPlayer.getDuration()));
                    iMediaPlayer.start();
                }
                dismissDialog();
                break;
            default:
                break;
        }
        if (tempUp - tempDown < 500) {
            return false;
        } else {
            return true;
        }
    }

    private void showProgressDialog(float deltaX, String seekTime, float seekTimePosition, long totalTimeDuration) {
        if (mProgressDialog == null) {
            View localView = LayoutInflater.from(activity).inflate(R.layout.dialog_progress, null);
            mDialogProgressBar = localView.findViewById(R.id.duration_progressbar);
            mDialogProgressBar.setOnSeekBarChangeListener(this);
            mDialogSeekTime = localView.findViewById(R.id.tv_current);
            mDialogTotalTime = localView.findViewById(R.id.tv_duration);
            mDialogIcon = localView.findViewById(R.id.duration_image_tip);
            mProgressDialog = createDialogWithView(localView);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }

        mDialogSeekTime.setText(seekTime);
        mDialogTotalTime.setText(" / " + ReBuildTimeUtils.buildTime(iMediaPlayer.getDuration()));
        int temp = (int) (totalTimeDuration <= 0 ? 0 : (seekTimePosition * 100 / totalTimeDuration));
        mDialogProgressBar.setProgress(temp);
        seekBar.setProgress(temp);
        if (deltaX > 0) {
            mDialogIcon.setBackgroundResource(R.drawable.forward_icon);
        } else {
            mDialogIcon.setBackgroundResource(R.drawable.backward_icon);
        }
    }

    private Dialog createDialogWithView(View localView) {
        Dialog dialog = new Dialog(activity, R.style.style_dialog_progress);
        dialog.setContentView(localView);
        Window window = dialog.getWindow();
        window.addFlags(Window.FEATURE_ACTION_BAR);
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        window.setLayout(-2, -2);
        WindowManager.LayoutParams localLayoutParams = window.getAttributes();
        localLayoutParams.gravity = Gravity.CENTER;
        window.setAttributes(localLayoutParams);
        return dialog;
    }

    private void dismissDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (mBrightnessDialog != null) {
            mBrightnessDialog.dismiss();
        }
        if (mVolumeDialog != null) {
            mVolumeDialog.dismiss();
        }
    }

    private void showBrightnessDialog(int brightnessPercent) {
        if (mBrightnessDialog == null) {
            View localView = LayoutInflater.from(activity).inflate(R.layout.dialog_brightness, null);
            mDialogBrightnessTextView = localView.findViewById(R.id.tv_brightness);
            mDialogBrightnessProgressBar = localView.findViewById(R.id.brightness_progressbar);
            mBrightnessDialog = createDialogWithView(localView);
        }
        if (!mBrightnessDialog.isShowing()) {
            mBrightnessDialog.show();
        }
        if (brightnessPercent > 100) {
            brightnessPercent = 100;
        } else if (brightnessPercent < 0) {
            brightnessPercent = 0;
        }
        mDialogBrightnessTextView.setText(brightnessPercent + "%");
        mDialogBrightnessProgressBar.setProgress(brightnessPercent);
    }

    private void showVolumeDialog(int volumePercent) {
        if (mVolumeDialog == null) {
            View localView = LayoutInflater.from(activity).inflate(R.layout.dialog_volume, null);
            mDialogVolumeImageView = localView.findViewById(R.id.volume_image_tip);
            mDialogVolumeTextView = localView.findViewById(R.id.tv_volume);
            mDialogVolumeProgressBar = localView.findViewById(R.id.volume_progressbar);
            mVolumeDialog = createDialogWithView(localView);
        }
        if (!mVolumeDialog.isShowing()) {
            mVolumeDialog.show();
        }
        if (volumePercent <= 0) {
            mDialogVolumeImageView.setBackgroundResource(R.mipmap.close_volume);
        } else {
            mDialogVolumeImageView.setBackgroundResource(R.mipmap.add_volume);
        }
        if (volumePercent > 100) {
            volumePercent = 100;
        } else if (volumePercent < 0) {
            volumePercent = 0;
        }
        mDialogVolumeTextView.setText(volumePercent + "%");
        mDialogVolumeProgressBar.setProgress(volumePercent);
    }


    public interface ScreenListener {
        void changeScreen(Boolean stopIt);

        void finishDontClean(Boolean clean);
    }

    private ScreenListener screenListener;

    @Override
    public void setScreenListener(ScreenListener screenListener) {
        this.screenListener = screenListener;
    }


    @Override
    public void setWindowManageSupport(FrameLayout frameLayout, IjkVideoView ijkVideoView) {
        baseFramelayout = frameLayout;
        this.ijkVideoView = ijkVideoView;
    }

    @Override
    public boolean isPlaying() {
        return iMediaPlayer == null ? false : iMediaPlayer.isPlaying();
    }

    private void invisibleView() {
        if (isViewVisible) {
            lockScreen.setVisibility(INVISIBLE);
            vName.setVisibility(INVISIBLE);
            lockScreen.setVisibility(INVISIBLE);
            pauseStart.setVisibility(INVISIBLE);
            currentTv.setVisibility(INVISIBLE);
            seekBar.setVisibility(INVISIBLE);
            totalTv.setVisibility(INVISIBLE);
            changescreen.setVisibility(INVISIBLE);
            smallscreen.setVisibility(INVISIBLE);
        } else {
            lockScreen.setVisibility(VISIBLE);
            vName.setVisibility(VISIBLE);
            lockScreen.setVisibility(VISIBLE);
            pauseStart.setVisibility(VISIBLE);
            currentTv.setVisibility(VISIBLE);
            seekBar.setVisibility(VISIBLE);
            totalTv.setVisibility(VISIBLE);
            changescreen.setVisibility(VISIBLE);
            smallscreen.setVisibility(VISIBLE);
        }
    }
}
