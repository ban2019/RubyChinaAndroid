package org.rubychinaandroid.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.rubychinaandroid.MyApplication;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Utility {

    private static final String LOG_TAG = "Utility";

    // the number of returned posts after one request
    public static int LIST_LIMIT = 20;

    public static String getTimeSpanSinceCreated(String date) {

        String rawPublishTime = date;

        String timeInString = rawPublishTime.substring(0, rawPublishTime.indexOf('T')) + ' ' +
                rawPublishTime.substring(rawPublishTime.indexOf('T') + 1, rawPublishTime.indexOf('.'));
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
            toast = Toast.makeText(MyApplication.gAppContext, msg, Toast.LENGTH_SHORT);
            Log.d(LOG_TAG, "toast is null");
        } else {
            toast.setText(msg);
            Log.d(LOG_TAG, "toast is not null");
        }

        toast.show();
    }

    public static void storeTopicsToFile(String fileName, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    MyApplication.getInstance().openFileOutput(fileName, Context.MODE_PRIVATE | Context.MODE_APPEND));
            outputStreamWriter.write(data + "\n");
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static ArrayList<String> readTopicsFromFile(String fileName) {

        ArrayList<String> ret = new ArrayList<>();

        try {
            InputStream inputStream = MyApplication.getInstance().openFileInput(fileName);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                //StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    //stringBuilder.append(receiveString);
                    ret.add(receiveString);
                }

                inputStream.close();
                //ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.d("PostActivity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.d("PostActivity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static boolean deleteFile(String fileName) {
        return MyApplication.getInstance().deleteFile(fileName);
    }
}
