package org.rubychinaandroid.utils;

import android.util.Log;
import android.widget.Toast;

import org.rubychinaandroid.MyApplication;
import org.rubychinaandroid.MyConfig;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {
    private static final String LOG_TAG = "Utility";

    public static String getTimeSpanSinceCreated(String date) {
        if (date == null || "".equals(date)) {
            return "";
        }
        String rawPublishTime = date;
        String timeInString = "";

        if (rawPublishTime.contains(".")) { // This time format parsing is for the one got from API
            timeInString = rawPublishTime.substring(0, rawPublishTime.indexOf('T')) + ' ' +
                    rawPublishTime.substring(rawPublishTime.indexOf('T') + 1, rawPublishTime.indexOf('.'));
        } else { // This parsing is for the one got from JSoup
            if (rawPublishTime.contains("前")) {
                return rawPublishTime;
            }
            timeInString = rawPublishTime.substring(0, rawPublishTime.indexOf('T')) + ' ' +
                    rawPublishTime.substring(rawPublishTime.indexOf('T') + 1, rawPublishTime.indexOf('+'));
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date created;
        Date current = new Date();

        try {
            created = format.parse(timeInString);
            long diff = created.getTime() - current.getTime();
            long milliSeconds = diff > 0 ? diff : -diff;
            long seconds = milliSeconds / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (days > 0) {
                return (days + " 天前");
            } else if (hours > 0) {
                return (hours + " 小时前");
            } else if (minutes > 0) {
                return (minutes + " 分钟前");
            } else if (seconds > 0 && seconds < 10) {
                return ("几秒前");
            } else {
                return ("刚刚");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private static Toast toast = null;

    public static void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(MyApplication.getInstance(), msg, Toast.LENGTH_SHORT);
            Log.d(LOG_TAG, "toast is null");
        } else {
            toast.setText(msg);
            Log.d(LOG_TAG, "toast is not null");
        }
        toast.show();
    }

    public static boolean isDisplayImageNow() {
        /*
        boolean isDisplayIfMobile = MyConfig.getInstance()
                .getBooleanPreference(RubyChinaArgKeys.DISPLAY_IMAGE_IF_MOBILE);
        boolean isMobile = NetWorkHelper.isConnected();
        if (!isDisplayIfMobile && isMobile) {
            return false;
        }*/
        return true;
    }
}
