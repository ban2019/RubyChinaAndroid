package org.rubychinaandroid.activity;

import android.content.Intent;
import android.os.Bundle;

import org.rubychinaandroid.R;
import org.rubychinaandroid.fragments.ReplyFragment;
import org.rubychinaandroid.utils.RubyChinaArgKeys;
import org.rubychinaandroid.view.JumpToolbar;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public class ReplyActivity extends SwipeBackActivity {

    JumpToolbar mToolbar;
    ReplyFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        final Intent intent = getIntent();
        String topicId = intent.getStringExtra(RubyChinaArgKeys.TOPIC_ID);

        mFragment = new ReplyFragment();

        Bundle args = new Bundle();
        args.putString(RubyChinaArgKeys.TOPIC_ID, topicId);
        mFragment.setArguments(args);

        mToolbar = (JumpToolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("回复");
        mToolbar.attachTo(mFragment);

        getSupportFragmentManager().beginTransaction().replace(R.id.reply, mFragment).commit();
    }
}