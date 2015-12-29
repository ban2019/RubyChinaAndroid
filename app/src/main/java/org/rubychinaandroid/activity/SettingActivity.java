package org.rubychinaandroid.activity;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.rubychinaandroid.R;
import org.rubychinaandroid.fragments.SettingFragment;

public class SettingActivity extends BaseActivity {

    @Override
    public void configToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("偏好设置");
        setToolbarBackButton();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        configToolbar();
        getFragmentManager().beginTransaction().add(R.id.container, new SettingFragment()).commitAllowingStateLoss();
    }
}
