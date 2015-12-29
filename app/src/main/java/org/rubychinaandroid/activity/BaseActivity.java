package org.rubychinaandroid.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.rubychinaandroid.R;
import org.rubychinaandroid.fragments.TopicsFragment;
import org.rubychinaandroid.view.JumpToolbar;

import java.util.ArrayList;
import java.util.List;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

class NullToolbarException extends RuntimeException {}

abstract public class BaseActivity extends SwipeBackActivity {
    public static List<BaseActivity> activities = new ArrayList<BaseActivity>();
    protected Toolbar mToolbar;
    abstract void configToolbar();
    protected void setToolbarBackButton() {
        if (mToolbar == null) {
            throw new NullToolbarException();
        }
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAll();
            }
        });
    }
    protected void attachToolbar(TopicsFragment fragment) {
        if (mToolbar == null) {
            throw new NullToolbarException();
        }
        if (fragment != null) {
            ((JumpToolbar) mToolbar).attachTo(fragment);
        }
    }
    public static void addActivity(BaseActivity activity) {
        activities.add(activity);
    }
    public static void removeActivity(BaseActivity activity) {
        activities.remove(activity);
    }
    public static void finishAll() {
        for (BaseActivity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addActivity(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }
}
