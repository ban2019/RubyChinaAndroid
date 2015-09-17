package org.rubychinaandroid.utils.oauth;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Verb;
import org.scribe.utils.Preconditions;


public class RubyChinaOAuthApi20 extends DefaultApi20 {

    private final String AUTHORIZATION_URL = "https://ruby-china.org/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code";
    private final String ACCESS_TOKEN_ENDPOINT = "https://ruby-china.org/oauth/token?grant_type=authorization_code";

    public RubyChinaOAuthApi20() {}

    @Override
    public Verb getAccessTokenVerb()
    {
        return Verb.POST;
    }

    public String getAccessTokenEndpoint() {
        return ACCESS_TOKEN_ENDPOINT;
    }

    public String getAuthorizationUrl(OAuthConfig config) {
        Preconditions.checkValidUrl(config.getCallback(), "Must provide a valid url as callback.");
        return String.format(AUTHORIZATION_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
    }

    public AccessTokenExtractor getAccessTokenExtractor() {
        return new JsonTokenExtractor();
    }
}
