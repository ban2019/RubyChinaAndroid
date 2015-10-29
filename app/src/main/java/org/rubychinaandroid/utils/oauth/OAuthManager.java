package org.rubychinaandroid.utils.oauth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.rubychinaandroid.MyApplication;
import org.rubychinaandroid.api.RubyChinaApiListener;
import org.rubychinaandroid.api.RubyChinaApiWrapper;
import org.rubychinaandroid.db.RubyChinaDBManager;
import org.rubychinaandroid.utils.Utility;

public class OAuthManager {

    private static String TAG = "OAuthManager";

    // For convenience, the key name 'access_token' is kept identical to the parameter of HTTP request
    // used when accessing protected resource.
    public static String ACCESS_TOKEN = "access_token";
    public static final String EMPTY_TOKEN = "";

    private static String SHARED_PREFERENCE_FILE_NAME = "oauth";
    private static String STATE_LOGGED_IN = "logged_in";

    private static String LOGIN = "user_login";

    private static SharedPreferences.Editor mEditor;
    private static SharedPreferences mPref;

    private static OAuthManager mOAuthManager;

    private OAuthManager() {
        Log.d(TAG, "construct");
        mEditor = MyApplication.getInstance()
                .getSharedPreferences(SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE).edit();
        mPref = MyApplication.getInstance()
                .getSharedPreferences(SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized OAuthManager getInstance() {
        if (mOAuthManager == null) {
            mOAuthManager = new OAuthManager();
        }
        return mOAuthManager;
    }

    private static synchronized SharedPreferences.Editor getEditor() {
        Log.d(TAG, "getEditor()");
        if (mEditor == null) {
            mEditor = MyApplication.getInstance()
                    .getSharedPreferences(SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE).edit();
        }
        return mEditor;
    }

    public static void saveLoggedInState(boolean loggedIn) {
        getEditor().putBoolean(STATE_LOGGED_IN, loggedIn);
        getEditor().commit();
    }

    public static boolean getLoggedInState() {
        return mPref.getBoolean(STATE_LOGGED_IN, false);
    }

    public static void saveAccessTokenString(String accessTokenString) {
        getEditor().putString(ACCESS_TOKEN, accessTokenString);
        getEditor().commit();
    }

    public static String getAccessTokenString() {
        return mPref.getString(ACCESS_TOKEN, "");
    }

    public static void revokeAccessToken() {
        getEditor().putString(ACCESS_TOKEN, EMPTY_TOKEN);
        getEditor().commit();
    }

    public static void saveUserLogin(String login) {
        getEditor().putString(LOGIN, login);
        getEditor().commit();
        Log.d(TAG + " save ", login);
    }

    public static String getUserLogin() {
        return mPref.getString(LOGIN, "");
    }

    public static void logOut() {
        RubyChinaApiWrapper.revoke(new RubyChinaApiListener() {
            @Override
            public void onSuccess(Object data) {
                Utility.showToast("注销成功");
            }

            @Override
            public void onFailure(String data) {
                Utility.showToast("注销失败");
                Log.d(TAG, data);
            }
        });
        revokeAccessToken();
        saveUserLogin("");
        saveLoggedInState(false);
    }
}
