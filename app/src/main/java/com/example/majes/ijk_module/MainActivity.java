package com.example.majes.ijk_module;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

import tv.danmaku.ijk.media.player.utils.FileUtils;
import static tv.danmaku.ijk.media.player.utils.FileUtils.videoPath;


/**
 * @author majes
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setvideopath);
        findViewById(R.id.gobtn).setOnClickListener(this);
        findViewById(R.id.netvideo).setOnClickListener(this);
        file = new File(videoPath);
        if (!file.exists()){
            new RxPermissions(this)
                    .requestEach(Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(permission -> {
                        if (permission.granted) {
                            FileUtils.copyFilesFassets(this);
                        } else {
                            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    //    url =  "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear1/prog_index.m3u8"  name = "bipbop basic 400x300 @ 232 kbps"
//    url = "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear2/prog_index.m3u8" name ="bipbop basic 640x480 @ 650 kbps"
//    url =  "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear3/prog_index.m3u8" name ="bipbop basic 640x480 @ 1 Mbps"
//    url =   "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear4/prog_index.m3u8" name ="bipbop basic 960x720 @ 2 Mbps"
//    url =    "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear0/prog_index.m3u8" name ="bipbop basic 22.050Hz stereo @ 40 kbps"
//    url =   "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_16x9/bipbop_16x9_variant.m3u8"name = "bipbop advanced master playlist"
//    url =   "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_16x9/gear1/prog_index.m3u8" name ="bipbop advanced 416x234 @ 265 kbps"
//    url =  "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_16x9/gear2/prog_index.m3u8" name ="bipbop advanced 640x360 @ 580 kbps"
//    url =   "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_16x9/gear3/prog_index.m3u8" name ="bipbop advanced 960x540 @ 910 kbps"
//    url =   "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_16x9/gear4/prog_index.m3u8" name ="bipbop advanced 1289x720 @ 1 Mbps"
//    url =  "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_16x9/gear5/prog_index.m3u8" name ="bipbop advanced 1920x1080 @ 2 Mbps"
//    url =  "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_16x9/gear0/prog_index.m3u8" "name =bipbop advanced 22.050Hz stereo @ 40 kbps"
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.gobtn: {
                if (file.exists()) {
                    ARouter.getInstance().build("/activity/视频").withString("videoPath", file.getPath()).
                            withString("videoTitle", file.getName()).navigation(this);
                }
                break;
            }
            case R.id.netvideo: {
                ARouter.getInstance().build("/activity/视频").withString("videoPath",
                        "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear2/prog_index.m3u8").
                        withString("videoTitle", "bipbop basic 640x480 @ 650 kbps").navigation(this);
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        ARouter.getInstance().destroy();
        super.onDestroy();
    }
}
