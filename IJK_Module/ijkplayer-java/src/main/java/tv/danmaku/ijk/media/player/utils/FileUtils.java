package tv.danmaku.ijk.media.player.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author majes
 * @date 12/9/17.
 */

public class FileUtils {
    public static final String videoPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() +
                    File.separator + "test.mp4";

    public static void copyFilesFassets(Context context) {
        try {
            InputStream is = context.getAssets().open("test.mp4");
            FileOutputStream fos = new FileOutputStream(new File(videoPath));
            byte[] buffer = new byte[2048];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
