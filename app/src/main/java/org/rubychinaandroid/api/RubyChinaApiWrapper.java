package org.rubychinaandroid.api;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;
import org.rubychinaandroid.model.NodeModel;
import org.rubychinaandroid.model.PostModel;
import org.rubychinaandroid.model.ReplyModel;
import org.rubychinaandroid.model.TopicModel;
import org.rubychinaandroid.model.UserModel;
import org.rubychinaandroid.utils.RubyChinaCategory;
import org.rubychinaandroid.utils.Utility;

import java.util.ArrayList;


public class RubyChinaApiWrapper {

    /* OAUTH Request URLs, app id & app secret */
    private static final String BASE_URL = "https://ruby-china.org";

    private static final String OAUTH_REVOKE_URL = BASE_URL + "/oauth/revoke";

    /* API URL */
    private static final String API_BASE_URL = "https://ruby-china.org/api/v3";

    private static final String API_TOPICS_URL = API_BASE_URL + "/topics.json";

    private static final String API_TOPICS_CONTENT_URL = API_BASE_URL + "/topics/%s.json";

    private static final String API_TOPICS_REPLY_URL = API_BASE_URL + "/topics/%s/replies.json";

    private static final String API_NODES_URL = API_BASE_URL + "/nodes.json";

    private static final String API_HELLO_URL = API_BASE_URL + "/hello.json";

    private static final String API_PROFILE_URL = API_BASE_URL + "/users/%s.json";

    private static final String API_USER_TOPICS_URL = API_BASE_URL + "/users/%s/topics.json";

    private static final String API_FAVOURITE_TOPIC_URL = API_BASE_URL + "/topics/%s/favorite.json";

    public static final String LOG_TAG = "RubyChinaApiWrapper";

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
                        .with("offset", Integer.toString(page * Utility.LIST_LIMIT)),
                listener);
    }

    // Page is zero-based, 20 topics in one page.
    public static void getUserTopics(int page, String userLogin,
                                     final RubyChinaApiListener<ArrayList<TopicModel>> listener) {

        getTopics(String.format(API_USER_TOPICS_URL, userLogin),
                new ApiParams()
                        .with("login", userLogin)
                        .with("offset", Integer.toString(page * Utility.LIST_LIMIT)),
                listener);
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
                        listener.onFailure(null);
                    }
                });
    }
}

/*
    *****可用的URL列表*******
        Method	URL
        GET	    http://ruby-china.org/api/topics.json
        GET	    http://ruby-china.org/api/topics/node/:id.json
        POST	http://ruby-china.org/api/topics.json
        GET	    http://ruby-china.org/api/topics/:id.json
        POST	http://ruby-china.org/api/topics/:id/replies.json
        POST	http://ruby-china.org/api/topics/:id/follow.json
        POST	http://ruby-china.org/api/topics/:id/unfollow.json
        POST	http://ruby-china.org/api/topics/:id/favorite.json
        GET	    http://ruby-china.org/api/nodes.json
        PUT	    http://ruby-china.org/api/user/favorite/:user/:topic.json
        GET	    http://ruby-china.org/api/users.json
        GET	    http://ruby-china.org/api/users/temp_access_token.json
        GET	    http://ruby-china.org/api/users/:user.json
        GET	    http://ruby-china.org/api/users/:user/topics.json
        GET	    http://ruby-china.org/api/users/:user/topics/favorite.json
        GET	    http://ruby-china.org/api/sites.json

        https://ruby-china.org/api/v3/hello.json?access_token=#{access_token}
    */
