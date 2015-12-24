package org.rubychinaandroid.utils;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.rubychinaandroid.MyApplication;
import org.rubychinaandroid.api.RubyChinaApiListener;
import org.rubychinaandroid.api.RubyChinaApiWrapper;
import org.rubychinaandroid.model.TopicModel;
import org.rubychinaandroid.utils.RubyChinaArgKeys;
import org.rubychinaandroid.utils.oauth.OAuthManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class FavouriteUtils {
    private static final String LOG_TAG = "Favourite";
    private final static String FILENAME;
    static {
        FILENAME = new File(MyApplication.getInstance().getFilesDir(), "fav_record").getName();
    }

    public static void recordFavourite(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    MyApplication.getInstance().openFileOutput(FILENAME, Context.MODE_PRIVATE | Context.MODE_APPEND));
            outputStreamWriter.write(data + "\n");
            outputStreamWriter.close();
            //Log.d(LOG_TAG, "store " + data + FILENAME);
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static ArrayList<String> loadFavourites() {
        ArrayList<String> ret = new ArrayList<>();
        try {
            InputStream inputStream = MyApplication.getInstance().openFileInput(FILENAME);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                while ((receiveString = bufferedReader.readLine()) != null) {
                    Log.d(LOG_TAG, receiveString);
                    ret.add(receiveString);
                }
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            Log.d("PostActivity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.d("PostActivity", "Can not read file: " + e.toString());
        }
        return ret;
    }

    private static boolean deleteFile(String fileName) {
        return MyApplication.getInstance().deleteFile(fileName);
    }

    private static final int PAGE = -1;

    private static class FavouriteHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PAGE:
                    if (!OAuthManager.getInstance().getLoggedInState()) {
                        Log.d(LOG_TAG, "have not logged in");
                        return;
                    }
                    Log.d("login=", OAuthManager.getInstance().getUserLogin());
                    RubyChinaApiWrapper.getFavouriteTopics(OAuthManager.getInstance().getUserLogin(),
                            msg.arg1, new RubyChinaApiListener<ArrayList<TopicModel>>() {
                                @Override
                                public void onSuccess(ArrayList<TopicModel> data) {
                                    Log.d(LOG_TAG, Integer.toString(data.size()));
                                    for (int i = 0; i < data.size(); i++) {
                                        recordFavourite(data.get(i).getTopicId());
                                    }
                                    favouriteHelper();
                                }

                                @Override
                                public void onFailure(String data) {
                                    Log.d(LOG_TAG, data);
                                }
                            });
                    break;
                default:
                    break;
            }
        }
    }

    private static Handler mHandler = new FavouriteHandler();

    public static void updateFavouriteRecord() {
        deleteFile(FILENAME);
        Log.d(LOG_TAG, "updatefavouriterecord");
        if (OAuthManager.getInstance().getLoggedInState()) {
            favouriteHelper();
        }
    }

    private static int page = 0;

    private static void favouriteHelper() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = PAGE;
                message.arg1 = page;
                ++page;
                mHandler.sendMessage(message);
            }
        }).start();
    }
}