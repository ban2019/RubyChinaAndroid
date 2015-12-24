package org.rubychinaandroid.api;

import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.rubychinaandroid.model.NodeModel;
import org.rubychinaandroid.model.PostModel;
import org.rubychinaandroid.model.ReplyModel;
import org.rubychinaandroid.model.TopicModel;
import org.rubychinaandroid.model.UserModel;
import org.rubychinaandroid.utils.RubyChinaCategory;
import org.rubychinaandroid.utils.Utility;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class RubyChinaApiWrapper {
    public static final String LOG_TAG = "RubyChinaApiWrapper";

    private static final String BASE_URL = "https://ruby-china.org";
    private static final String OAUTH_REVOKE_URL = BASE_URL + "/oauth/revoke";
    // API URL
    private static final String API_BASE_URL = "https://ruby-china.org/api/v3";
    private static final String API_TOPICS_URL = API_BASE_URL + "/topics.json";
    private static final String API_TOPICS_CONTENT_URL = API_BASE_URL + "/topics/%s.json";
    private static final String API_TOPICS_REPLY_URL = API_BASE_URL + "/topics/%s/replies.json";
    private static final String API_NODES_URL = API_BASE_URL + "/nodes.json";
    private static final String API_HELLO_URL = API_BASE_URL + "/hello.json";
    private static final String API_PROFILE_URL = API_BASE_URL + "/users/%s.json";
    private static final String API_USER_TOPICS_URL = API_BASE_URL + "/users/%s/topics.json";
    private static final String API_FAVOURITE_TOPIC_URL = API_BASE_URL + "/topics/%s/favorite.json";
    private static final String API_UNFAVOURITE_TOPIC_URL = API_BASE_URL + "/topics/%s/unfavorite.json";
    private static final String API_USER_FAVOURITE_TOPICS_URL = API_BASE_URL + "/users/%s/favorites.json";
    // HTML URL
    private static final String API_NODE_TOPICS_URL = BASE_URL + "/topics/node%s?page=%d";
    // The number of posts got once
    private static int LIST_LIMIT = 20;

    private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    // Page is zero-based, 20 topics in one page.
    public static void getTopics(String url, ApiParams params,
                                 final RubyChinaApiListener<ArrayList<TopicModel>> listener) {
        final String jsonObjName = "topics";
        asyncHttpClient.get(url, params,
                new WrappedTextHttpResponseHandler<>(TopicModel.class, jsonObjName, listener));
    }

    public static void getTopicsByCategory(RubyChinaCategory category, int page,
                                           final RubyChinaApiListener<ArrayList<TopicModel>> listener) {
        getTopics(API_TOPICS_URL,
                new ApiParams()
                        .with("type", category.getExpr())
                        .with("offset", Integer.toString(page * LIST_LIMIT)),
                listener);
    }

    // Page is zero-based, 20 topics in one page.
    public static void getUserTopics(String userLogin, int page,
                                     final RubyChinaApiListener<ArrayList<TopicModel>> listener) {
        getTopics(String.format(API_USER_TOPICS_URL, userLogin),
                new ApiParams()
                        .with("login", userLogin)
                        .with("offset", Integer.toString(page * LIST_LIMIT)),
                listener);
    }

    public static void getFavouriteTopics(String userLogin, int page,
                                          final RubyChinaApiListener<ArrayList<TopicModel>> listener) {
        getTopics(String.format(API_USER_FAVOURITE_TOPICS_URL, userLogin),
                new ApiParams()
                        .with("login", userLogin)
                        .with("offset", Integer.toString(page * LIST_LIMIT)),
                listener);
    }

    public static void getNodeTopicsFromBrowser(String nodeId, int page,
                                                final RubyChinaApiListener<ArrayList<TopicModel>> listener) {
        String urlString = String.format(API_NODE_TOPICS_URL, nodeId, page);
        asyncHttpClient.addHeader("Referer", getBaseUrl());
        asyncHttpClient.addHeader("Content-Type", "application/x-www-form-urlencoded");
        asyncHttpClient.get(urlString, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseBody) {
                new AsyncTask<Void, Void, ArrayList<TopicModel>>() {
                    @Override
                    protected ArrayList<TopicModel> doInBackground(Void... params) {
                        try {
                            return parseFromNodeEntry(responseBody, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(ArrayList<TopicModel> topics) {
                        if (topics != null) {
                            SafeHandler.onSuccess(listener, topics);
                        } else {
                            SafeHandler.onFailure(listener, "topics is null");
                        }
                    }
                }.execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String error, Throwable throwable) {
                SafeHandler.onFailure(listener, error);
            }
        });
    }

    private static ArrayList<TopicModel> parseFromNodeEntry(String responseBody, String nodeName)
            throws Exception {
        Document doc = Jsoup.parse(responseBody);
        ArrayList<TopicModel> topics = new ArrayList<>();
        Element body = doc.body();
        Elements elements = body.getElementsByAttributeValueMatching("class", Pattern.compile("topic media topic-(.*)"));

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
        TopicModel topic = new TopicModel();
        topic.setTopicId(el.attr("class").substring("topic media topic-".length()));

        Elements divNodes = el.getElementsByTag("div");
        for (Element divNode : divNodes) {
            String content = divNode.toString();
            if (content.contains("class=\"avatar")) {
                Elements userLoginNode = divNode.getElementsByTag("a");
                if (userLoginNode != null) {
                    topic.setUserName(userLoginNode.attr("href").substring("/".length()));
                }

                Elements avatarNode = divNode.getElementsByTag("img");
                if (avatarNode != null) {
                    String avatarString = avatarNode.attr("src");
                    topic.setUserAvatarUrl(avatarString);
                }
            } else if (content.contains("class=\"infos")) {
                Element aaNode = divNode.getElementsByTag("a").first();
                topic.setTitle(aaNode.attr("title"));

                Elements abbrNodes = divNode.getElementsByTag("abbr");
                String time = abbrNodes.get(0).attr("title");
                topic.setCreatedAt(Utility.getTimeSpanSinceCreated(time));
            }
        }
        return topic;
    }

    private static String getBaseUrl() {
        return BASE_URL;
    }

    public static void getPostContent(final String topicId,
                                      final RubyChinaApiListener<PostModel> listener) {
        asyncHttpClient.get(String.format(API_TOPICS_CONTENT_URL, topicId),
                new ApiParams().with("id", topicId),
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String rawResponse) {
                        if (rawResponse != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(rawResponse);
                                PostModel postModel = new PostModel();
                                postModel.parse(jsonObject);
                                listener.onSuccess(postModel);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d(LOG_TAG, "rawResponse is null");
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, String response, Throwable throwable) {
                        listener.onFailure(null);
                    }
                });
    }

    public static void getPostReplies(final String topicId,
                                      final RubyChinaApiListener<ArrayList<ReplyModel>> listener) {
        final String jsonObjName = "replies";
        asyncHttpClient.get(String.format(API_TOPICS_REPLY_URL, topicId),
                new ApiParams().with("id", topicId),
                new WrappedTextHttpResponseHandler<ReplyModel>(ReplyModel.class, jsonObjName, listener));
    }

    public static void getAllNodes(final RubyChinaApiListener<ArrayList<NodeModel>> listener) {
        final String jsonObjName = "nodes";
        asyncHttpClient.get(API_NODES_URL,
                new ApiParams(),
                new WrappedTextHttpResponseHandler<NodeModel>(NodeModel.class, jsonObjName, listener));
    }

    public static void publishPost(String title, String content, String nodeId, final RubyChinaApiListener listener) {
        asyncHttpClient.post(API_TOPICS_URL, new ApiParams()
                        .with("node_id", nodeId)
                        .with("title", title)
                        .with("body", content)
                        .withToken(),
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String rawResponse) {
                        listener.onSuccess(null);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String rawResponse, Throwable throwable) {
                        listener.onFailure(null);
                    }
                });
    }

    public static void revoke(final RubyChinaApiListener listener) {
        asyncHttpClient.post(OAUTH_REVOKE_URL, new ApiParams().withToken(),
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String rawResponse) {
                        listener.onSuccess(null);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String rawResponse, Throwable throwable) {
                        listener.onFailure(null);
                    }
                });
    }

    public static void replyToPost(String topicId, String replyContent, final RubyChinaApiListener listener) {
        asyncHttpClient.post(String.format(API_TOPICS_REPLY_URL, topicId), new ApiParams()
                        .with("id", topicId)
                        .with("body", replyContent)
                        .withToken(),
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String rawResponse) {
                        listener.onSuccess(null);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String rawResponse, Throwable throwable) {
                        listener.onFailure(null);
                    }
                });
    }

    public static void hello(final RubyChinaApiListener<UserModel> listener) {
        asyncHttpClient.get(String.format(API_HELLO_URL),
                new ApiParams().withToken(),
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String rawResponse) {
                        try {
                            JSONObject jsonObject = new JSONObject(rawResponse);
                            UserModel userModel = new UserModel();
                            userModel.parse(jsonObject);
                            listener.onSuccess(userModel);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String rawResponse, Throwable throwable) {
                        listener.onFailure(null);
                        Log.d("hello", "onFailure");
                    }
                });
    }

    public static void getUserProfile(String userLogin, final RubyChinaApiListener<UserModel> listener) {
        asyncHttpClient.get(String.format(API_PROFILE_URL, userLogin),
                new ApiParams().with("login", userLogin),
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String rawResponse) {
                        try {
                            JSONObject jsonObject = new JSONObject(rawResponse);
                            UserModel userModel = new UserModel();
                            userModel.parse(jsonObject);
                            listener.onSuccess(userModel);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String rawResponse, Throwable throwable) {
                        Log.d("getUserProfile", "onFailure, code=" + statusCode);
                    }
                });
    }


    public static void favouriteTopic(String topicId, final RubyChinaApiListener listener) {
        asyncHttpClient.post(String.format(API_FAVOURITE_TOPIC_URL, topicId), new ApiParams()
                        .with("id", topicId)
                        .withToken(),
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String rawResponse) {
                        listener.onSuccess(null);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String rawResponse, Throwable throwable) {
                        Log.d("code:", Integer.toString(statusCode));
                        Log.d("info:", rawResponse);
                        listener.onFailure(rawResponse);
                    }
                });
    }

    public static void unFavouriteTopic(String topicId, final RubyChinaApiListener listener) {
        asyncHttpClient.post(String.format(API_UNFAVOURITE_TOPIC_URL, topicId), new ApiParams()
                        .with("id", topicId)
                        .withToken(),
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String rawResponse) {
                        listener.onSuccess(null);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String rawResponse, Throwable throwable) {
                        listener.onFailure(null);
                    }
                });
    }
}