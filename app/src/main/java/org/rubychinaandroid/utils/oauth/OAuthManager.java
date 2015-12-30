package org.rubychinaandroid.utils.oauth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.rubychinaandroid.MyApplication;
import org.rubychinaandroid.R;
import org.rubychinaandroid.api.RubyChinaApiListener;
import org.rubychinaandroid.api.RubyChinaApiWrapper;
import org.rubychinaandroid.utils.Utility;

public class OAuthManager {
    private String TAG = "OAuthManager";
    // For convenience, the key name 'access_token' is kept identical to the parameter of HTTP request
    // used when accessing protected resource.
    public interface Keys {
        String ACCESS_TOKEN = "access_token";
        String SP_FILE_NAME = "oauth";
        String STATE_LOGGED_IN = "logged_in";
        String LOGIN = "user_login";
        String AVATAR_URL = "avatar_url";
    }

    public final String EMPTY_TOKEN = "";

    private static SharedPreferences.Editor mEditor;
    private static SharedPreferences mPref;
    private static OAuthManager mInstance;

    private OAuthManager() {
        Log.d(TAG, "construct");
        mEditor = MyApplication.getInstance()
                .getSharedPreferences(Keys.SP_FILE_NAME, Context.MODE_PRIVATE).edit();
        mPref = MyApplication.getInstance()
                .getSharedPreferences(Keys.SP_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static OAuthManager getInstance() {
        if (mInstance == null) {
            synchronized (OAuthManager.class) {
                if (mInstance == null) {
                    mInstance = new OAuthManager();
                }
            }
        }
        return mInstance;
    }

    private synchronized SharedPreferences.Editor getEditor() {
        Log.d(TAG, "getEditor()");
        if (mEditor == null) {
            mEditor = MyApplication.getInstance()
                    .getSharedPreferences(Keys.SP_FILE_NAME, Context.MODE_PRIVATE).edit();
        }
        return mEditor;
    }

    public void saveLoggedInState(boolean loggedIn) {
        getEditor().putBoolean(Keys.STATE_LOGGED_IN, loggedIn);
        getEditor().commit();
    }

    public boolean isLoggedIn() {
        return mPref.getBoolean(Keys.STATE_LOGGED_IN, false);
    }

    public void saveAccessTokenString(String accessTokenString) {
        getEditor().putString(Keys.ACCESS_TOKEN, accessTokenString);
        getEditor().commit();
    }

    public String getAccessTokenString() {
        return mPref.getString(Keys.ACCESS_TOKEN, "");
    }

    public void revokeAccessToken() {
        getEditor().putString(Keys.ACCESS_TOKEN, EMPTY_TOKEN);
        getEditor().commit();
    }

    public void saveUserLogin(String login) {
        getEditor().putString(Keys.LOGIN, login);
        getEditor().commit();
    }

    public String getUserLogin() {
        return mPref.getString(Keys.LOGIN, "");
    }

    public void logOut() {
        RubyChinaApiWrapper.revoke(new RubyChinaApiListener() {
            @Override
            public void onSuccess(Object data) {
                Utility.showToast("注销成功");
                revokeAccessToken();
                saveUserLogin("drawable://" + R.string.default_username);
                saveLoggedInState(false);
                saveAvatarUrl("drawable://" + R.drawable.avatar_default);
            }

            @Override
            public void onFailure(String data) {
                Utility.showToast("注销失败");
            }
        });
    }

    public void saveAvatarUrl(String url) {
        mEditor.putString(Keys.AVATAR_URL, url);
        mEditor.commit();
    }
    public String getAvatarUrl() {
        return mPref.getString(Keys.AVATAR_URL, "");
    }
}
