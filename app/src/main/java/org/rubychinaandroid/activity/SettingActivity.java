package org.rubychinaandroid.activity;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.rubychinaandroid.R;
import org.rubychinaandroid.fragments.SettingFragment;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class SettingActivity extends SwipeBackActivity {
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("偏好设置");

        getFragmentManager().beginTransaction().add(R.id.container, new SettingFragment()).commitAllowingStateLoss();
    }
}
