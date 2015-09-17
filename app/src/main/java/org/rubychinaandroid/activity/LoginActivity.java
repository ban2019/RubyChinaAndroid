package org.rubychinaandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.rubychinaandroid.R;
import org.rubychinaandroid.utils.oauth.OAuthManager;
import org.rubychinaandroid.utils.oauth.RubyChinaOAuthService;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class LoginActivity extends SwipeBackActivity {
    private String LOG_TAG = "LoginActivity";
    private static final Token EMPTY_TOKEN = null;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("登录");
        //setSupportActionBar(mToolbar);
        //setActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final OAuthService service = RubyChinaOAuthService.getInstance().getOAuthService();

        WebView webView = (WebView) findViewById(R.id.login_web_oauth);

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
                            Log.d(OAuthManager.ACCESS_TOKEN, accessToken.toString());

                            // Return access_token to MainActivity
                            Intent intent = new Intent();
                            intent.putExtra(OAuthManager.ACCESS_TOKEN, accessToken);
                            setResult(RESULT_OK, intent);

                            finish();
                        }
                    }).start();
                }
                return true;
            }
        });
        webView.loadUrl(service.getAuthorizationUrl(EMPTY_TOKEN));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}