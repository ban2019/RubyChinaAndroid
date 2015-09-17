package org.rubychinaandroid.utils.oauth;

import android.content.Context;
import android.content.SharedPreferences;

import org.rubychinaandroid.MyApplication;

public class OAuthManager {

    // For convenience, the key name 'access_token' is kept identical to the parameter of HTTP request
    // used when accessing protected resource.
    public static String ACCESS_TOKEN = "access_token";
    public static final String EMPTY_TOKEN = "";

    private static String SHARED_PREFERENCE_FILE_NAME = "oauth";
    private static String STATE_LOGGED_IN = "logged_in";

    private OAuthManager() {}

    public static void saveLoggedInState(boolean loggedIn) {
        SharedPreferences.Editor editor = MyApplication.gAppContext
                .getSharedPreferences(SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(STATE_LOGGED_IN, loggedIn);
        editor.commit();
    }

    public static boolean getLoggedInState() {
        SharedPreferences pref = MyApplication.gAppContext
                .getSharedPreferences(SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(STATE_LOGGED_IN, false);
    }

    public static void saveAccessTokenString(String accessTokenString) {
        SharedPreferences.Editor editor = MyApplication.gAppContext
                .getSharedPreferences(SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(ACCESS_TOKEN, accessTokenString);
        editor.commit();
    }

    public static String getAccessTokenString() {
        SharedPreferences pref = MyApplication.gAppContext
                .getSharedPreferences(SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return pref.getString(ACCESS_TOKEN, "");
    }

    public static void revokeAccessToken() {
        SharedPreferences.Editor editor = MyApplication.gAppContext
                .getSharedPreferences(SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(ACCESS_TOKEN, EMPTY_TOKEN);
    }
}
