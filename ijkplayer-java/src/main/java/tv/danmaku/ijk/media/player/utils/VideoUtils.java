package tv.danmaku.ijk.media.player.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.Window;

import java.util.Iterator;
import java.util.LinkedHashMap;

public class VideoUtils {

    /**
     * Get activity from context object
     *
     * @param context context
     * @return object of Activity or null if it is not Activity
     */
    public static Activity scanForActivity(Context context) {
        if (context == null) return null;

        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }

        return null;
    }

    /**
     * Get AppCompatActivity from context
     *
     * @param context context
     * @return AppCompatActivity if it's not null
     */
    public static AppCompatActivity getAppCompActivity(Context context) {
        if (context == null) return null;
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getAppCompActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    public static Window getWindow(Context context) {
        if (VideoUtils.getAppCompActivity(context) != null) {
            return VideoUtils.getAppCompActivity(context).getWindow();
        } else {
            return VideoUtils.scanForActivity(context).getWindow();
        }
    }


    public static Object getCurrentFromDataSource(Object[] dataSourceObjects, int index) {
        LinkedHashMap<String, Object> map = (LinkedHashMap) dataSourceObjects[0];
        if (map != null && map.size() > 0) {
            return getValueFromLinkedMap(map, index);
        }
        return null;
    }

    public static Object getValueFromLinkedMap(LinkedHashMap<String, Object> map, int index) {
        int currentIndex = 0;
        for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
            Object key = it.next();
            if (currentIndex == index) {
                return map.get(key);
            }
            currentIndex++;
        }
        return null;
    }

    public static boolean dataSourceObjectsContainsUri(Object[] dataSourceObjects, Object object) {
        LinkedHashMap<String, Object> map = (LinkedHashMap) dataSourceObjects[0];
        if (map != null) {
            return map.containsValue(object);
        }
        return false;
    }

    public static String getKeyFromDataSource(Object[] dataSourceObjects, int index) {
        LinkedHashMap<String, Object> map = (LinkedHashMap) dataSourceObjects[0];
        int currentIndex = 0;
        for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
            Object key = it.next();
            if (currentIndex == index) {
                return key.toString();
            }
            currentIndex++;
        }
        return null;
    }
}
