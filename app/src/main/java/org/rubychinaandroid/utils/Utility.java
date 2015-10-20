package org.rubychinaandroid.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.rubychinaandroid.MyApplication;
import org.rubychinaandroid.api.RubyChinaApiListener;
import org.rubychinaandroid.api.RubyChinaApiWrapper;
import org.rubychinaandroid.model.NodeModel;
import org.rubychinaandroid.model.TopicModel;
import org.rubychinaandroid.model.UserModel;
import org.rubychinaandroid.utils.oauth.OAuthManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

public class Utility {

    private static final String LOG_TAG = "Utility";

    // the number of returned posts after one request
    public static int LIST_LIMIT = 20;

    public static String getTimeSpanSinceCreated(String date) {

        if (date == null || "".equals(date)) {
            return "";
        }

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
            toast = Toast.makeText(MyApplication.getInstance(), msg, Toast.LENGTH_SHORT);
            Log.d(LOG_TAG, "toast is null");
        } else {
            toast.setText(msg);
            Log.d(LOG_TAG, "toast is not null");
        }

        toast.show();
    }

    public static void storeTopicsToFile(String fileName, String data) {
        Log.d(LOG_TAG, "store " + data + fileName);
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    MyApplication.getInstance().openFileOutput(fileName, Context.MODE_PRIVATE | Context.MODE_APPEND));
            outputStreamWriter.write(data + "\n");
            outputStreamWriter.close();
            Log.d(LOG_TAG, "store " + data + fileName);
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

    public static boolean deleteFile(String fileName) {
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
                    RubyChinaApiWrapper.getFavouriteTopics(OAuthManager.getInstance().getUserLogin(),
                            msg.arg1, new RubyChinaApiListener<ArrayList<TopicModel>>() {
                                @Override
                                public void onSuccess(ArrayList<TopicModel> data) {
                                    Log.d(LOG_TAG, Integer.toString(data.size()));
                                    for (int i = 0; i < data.size(); i++) {
                                        storeTopicsToFile(RubyChinaArgKeys.MY_FAVOURITES, data.get(i).getTopicId());
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
        deleteFile(RubyChinaArgKeys.MY_FAVOURITES);
        favouriteHelper();
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

    public static ArrayList<TopicModel> parseFromNodeEntry(String responseBody, String nodeName)
            throws Exception {
        Document doc = Jsoup.parse(responseBody);
        ArrayList<TopicModel> topics = new ArrayList<>();
        Element body = doc.body();
        Elements elements = body.getElementsByAttributeValueMatching("class", Pattern.compile("topic media topic-(.*)"));

        int i = 0;
        for (Element el : elements) {
            try {
                topics.add(parseTopicModel(el));
            } catch (Exception e) {
                Log.e("err", e.toString());
            }
        }

        return topics;
    }

    private static TopicModel parseTopicModel(Element el) throws Exception {
        Elements divNodes = el.getElementsByTag("div");
        TopicModel topic = new TopicModel();
        topic.setTopicId(el.attr("class").substring("topic media topic-".length()));
        UserModel user = new UserModel();
        for (Element divNode : divNodes) {
            String content = divNode.toString();
            if (content.contains("class=\"avatar\"")) {
                Elements userIdNode = divNode.getElementsByTag("a");
                if (userIdNode != null) {
                    String idUrlString = userIdNode.attr("href");
                    user.setUserName(idUrlString.substring("/".length()));
                    topic.setUserName(user.getName());
                }

                Elements avatarNode = divNode.getElementsByTag("img");
                if (avatarNode != null) {
                    String avatarString = avatarNode.attr("src");
                    user.setAvatarUrl(avatarString);
                    topic.setUserAvatarUrl(avatarString);
                }
            } else if (content.contains("class=\"infos")) {
                Elements divNodesInside = divNode.getElementsByTag("div");
                for (Element divNodeInside : divNodesInside) {
                    String contentInside = divNodeInside.toString();
                    if (contentInside.contains("class=\"title")) {
                        Elements es = divNodeInside.getElementsByTag("a");
                        topic.setTitle(es.get(0).attr("title"));
                    }
                }
            }
        }
        return topic;
    }
}
