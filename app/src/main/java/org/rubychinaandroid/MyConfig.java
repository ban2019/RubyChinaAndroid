package org.rubychinaandroid;

import android.content.Context;
import android.content.SharedPreferences;

public class MyConfig {
    private static MyConfig mConfig = null;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private final String SPNAME = "MyConfig";

    private MyConfig() {
        mPref = MyApplication.getInstance().getSharedPreferences(SPNAME, Context.MODE_PRIVATE);
        mEditor = MyApplication.getInstance().
                getSharedPreferences(SPNAME, Context.MODE_PRIVATE).edit();
    }

    public static MyConfig getInstance() {
        if (mConfig == null) {
            mConfig = new MyConfig();
        }
        return mConfig;
    }

    public void setBooleanPreference(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    public boolean getBooleanPreference(String key) {
        return mPref.getBoolean(key, false);
    }
}