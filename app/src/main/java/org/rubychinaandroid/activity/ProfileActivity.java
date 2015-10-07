package org.rubychinaandroid.activity;


import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.rubychinaandroid.MyApplication;
import org.rubychinaandroid.R;
import org.rubychinaandroid.api.RubyChinaApiListener;
import org.rubychinaandroid.api.RubyChinaApiWrapper;
import org.rubychinaandroid.model.UserModel;
import org.rubychinaandroid.utils.RubyChinaConstants;
import org.rubychinaandroid.utils.oauth.OAuthManager;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class ProfileActivity extends SwipeBackActivity {

    private String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final ImageView avatar = (ImageView) findViewById(R.id.avatar);
        final TextView username = (TextView) findViewById(R.id.username);
        final TextView email = (TextView) findViewById(R.id.email);

        String userLogin = OAuthManager.getInstance().getUserLogin();
        assert(!"".equals(userLogin));

        Log.d("userLogin=", userLogin);

        RubyChinaApiWrapper.getUserProfile(userLogin, new RubyChinaApiListener<UserModel>() {
            @Override
            public void onSuccess(UserModel data) {
                username.setText(data.getName());
                email.setText(data.getEmail());
                ImageLoader.getInstance().displayImage(data.getAvatarUrl(), avatar, MyApplication.imageLoaderOptions);
            }

            @Override
            public void onFailure(String data) {
                Log.d(TAG, "failure");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
