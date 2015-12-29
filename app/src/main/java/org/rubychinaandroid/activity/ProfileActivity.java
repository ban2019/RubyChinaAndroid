package org.rubychinaandroid.activity;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.rubychinaandroid.R;
import org.rubychinaandroid.fragments.ProfileFragment;
import org.rubychinaandroid.utils.RubyChinaArgKeys;

public class ProfileActivity extends BaseActivity {
    private String TAG = "ProfileActivity";

    public void configToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("用户资料");
        setToolbarBackButton();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        configToolbar();

        String userLogin = getIntent().getStringExtra(RubyChinaArgKeys.USER_LOGIN);
        Bundle bundle = new Bundle();
        bundle.putString(RubyChinaArgKeys.USER_LOGIN, userLogin);
        bundle.putBoolean(RubyChinaArgKeys.IS_FROM_FAVOURITE_ACTIVITY, false);

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }
}
