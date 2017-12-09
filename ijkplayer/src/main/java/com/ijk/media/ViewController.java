package com.ijk.media;

import java.util.Map;
import android.os.Handler;
/**
 * @author majes
 * @date 12/8/17.
 */

public interface ViewController {
    /**
     * Sets video URI using specific headers.
     *
     * @param path     the path of the video.
     * @param headers the headers for the URI request.
     *                Note that the cross domain redirection is allowed by default, but that can be
     *                changed with key/value pairs through the headers parameter with
     *                "android-allow-cross-domain-redirect" as the key and "0" or "1" as the value
     *                to disallow or allow cross domain redirection.
     */
     void setPath(String path, Map<String, String> headers);

    void toggleAspectRatio();

    void onHandler(Handler handler);

    void setPlayerController(PlayerController playerController);

    void setWindowManager();

    void windowIn();

}
