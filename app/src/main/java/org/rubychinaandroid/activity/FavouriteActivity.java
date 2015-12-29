package org.rubychinaandroid.activity;

import android.os.Bundle;

import org.rubychinaandroid.R;
import org.rubychinaandroid.fragments.TopicsFragment;
import org.rubychinaandroid.utils.RubyChinaArgKeys;
import org.rubychinaandroid.utils.oauth.OAuthManager;
import org.rubychinaandroid.view.JumpToolbar;

public class FavouriteActivity extends BaseActivity {
    @Override
    public void configToolbar() {
        mToolbar = (JumpToolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("我的收藏");
        setToolbarBackButton();
        attachToolbar(createFragment());
    }
    private TopicsFragment createFragment() {
        TopicsFragment fragment = new TopicsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RubyChinaArgKeys.USER_LOGIN, OAuthManager.getInstance().getUserLogin());
        bundle.putBoolean(RubyChinaArgKeys.IS_FROM_FAVOURITE_ACTIVITY, true);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        return fragment;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        configToolbar();
    }
}
