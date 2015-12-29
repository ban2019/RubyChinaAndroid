package org.rubychinaandroid.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.rubychinaandroid.R;
import org.rubychinaandroid.utils.Utility;
import org.rubychinaandroid.utils.oauth.OAuthManager;
import org.rubychinaandroid.utils.oauth.RubyChinaOAuthService;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class LoginActivity extends BaseActivity {
    private String LOG_TAG = "LoginActivity";
    private static final Token EMPTY_TOKEN = null;
    private ProgressBar mProgressBar;

    @Override
    public void configToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("登录");
        setToolbarBackButton();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        configToolbar();

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        int color = 0xFFEB5424; // red
        mProgressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        mProgressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        final OAuthService service = RubyChinaOAuthService.getInstance().getOAuthService();
        final WebView webView = (WebView) findViewById(R.id.web_view);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if ("https://ruby-china.org/account/sign_in".equals(url)) {
                    // 1. Send Request for Authorization Grant Providing client id and redirect_url(callback_url)
                    view.loadUrl(url);
                } else if ("https://ruby-china.org/".equals(url)) {
                    view.loadUrl(service.getAuthorizationUrl(EMPTY_TOKEN));
                } else if (url.contains("https://ruby-china.org/?code=")) {
                    // 2. Receive Authorization Grant(code)
                    String code = url.replace("https://ruby-china.org/?code=", "");
                    final Verifier verifier = new Verifier(code);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 3. Send Request for Access Token Providing Authorization Grant
                            // 4. Receive the Access Token
                            Token accessToken = service.getAccessToken(null, verifier);
                            Log.d(OAuthManager.Keys.ACCESS_TOKEN, accessToken.toString());
                            // Return access_token to MainActivity
                            Intent intent = new Intent();
                            intent.putExtra(OAuthManager.Keys.ACCESS_TOKEN, accessToken);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }).start();
                }
                return true;
            }
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Utility.showToast("webView errorCode" + Integer.toString(errorCode));
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                mProgressBar.setProgress(progress);
                if (progress == 100) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }, 5000);
                }
            }
        });
        webView.loadUrl(service.getAuthorizationUrl(EMPTY_TOKEN));
    }
}