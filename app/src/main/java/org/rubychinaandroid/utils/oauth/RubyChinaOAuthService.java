package org.rubychinaandroid.utils.oauth;

import org.scribe.builder.ServiceBuilder;
import org.scribe.oauth.OAuthService;


public class RubyChinaOAuthService {
    private static volatile RubyChinaOAuthService instance;
    private static OAuthService mOAuthService;

    private static final String API_KEY = "a3cc6eb4";
    private static final String API_SECRET = "8860f5cc08d32b30f24932e8121657794a3e516c152d6ee81eab95897cf0c74e";
    private static final String CALLBACK = "https://ruby-china.org";

    private RubyChinaOAuthService() {
        mOAuthService = new ServiceBuilder()
                .provider(RubyChinaOAuthApi20.class)
                .apiKey(API_KEY)
                .apiSecret(API_SECRET)
                .callback(CALLBACK)
                .build();
    }

    public static RubyChinaOAuthService getInstance() {
        if (instance == null) {
            synchronized (RubyChinaOAuthService.class) {
                if (instance == null) {
                    instance = new RubyChinaOAuthService();
                }
            }
        }
        return instance;
    }

    public OAuthService getOAuthService() {
        return mOAuthService;
    }
}
