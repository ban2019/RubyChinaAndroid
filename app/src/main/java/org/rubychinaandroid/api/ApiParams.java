package org.rubychinaandroid.api;

import com.loopj.android.http.RequestParams;

import org.rubychinaandroid.utils.oauth.OAuthManager;

public class ApiParams extends RequestParams {
    public ApiParams with(String key, String value) {
        put(key, value);
        return this;
    }

    public ApiParams withToken() {
        put(OAuthManager.Keys.ACCESS_TOKEN, OAuthManager.getInstance().getAccessTokenString());
        return this;
    }
}
