package org.rubychinaandroid.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.rubychinaandroid.R;
import org.rubychinaandroid.fragments.TopicsFragment;
import org.rubychinaandroid.utils.RubyChinaArgKeys;
import org.rubychinaandroid.utils.oauth.OAuthManager;
import org.rubychinaandroid.view.JumpToolbar;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class FavouriteActivity extends SwipeBackActivity {
    JumpToolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        TopicsFragment fragment = new TopicsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RubyChinaArgKeys.USER_LOGIN, OAuthManager.getInstance().getUserLogin());
        bundle.putBoolean(RubyChinaArgKeys.IS_FROM_FAVOURITE_ACTIVITY, true);
        fragment.setArguments(bundle);

        mToolbar = (JumpToolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("我的收藏");
        mToolbar.attachTo(fragment);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }
}
