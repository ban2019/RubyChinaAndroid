package org.rubychinaandroid.api;

import com.loopj.android.http.RequestParams;

import org.rubychinaandroid.utils.oauth.OAuthManager;

import java.io.File;

public class ApiParams extends RequestParams {
    public ApiParams with(String key, String value) {
        put(key, value);
        return this;
    }

    public ApiParams withToken() {
        put(OAuthManager.ACCESS_TOKEN, OAuthManager.getAccessTokenString());
        return this;
    }

    public ApiParams withFile(String key, File file) {
        /*
        try {
            put(key, file);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        return this;
    }
}
