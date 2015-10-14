package org.rubychinaandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.rubychinaandroid.R;
import org.rubychinaandroid.fragments.ReplyFragment;
import org.rubychinaandroid.utils.RubyChinaArgKeys;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public class ReplyActivity extends SwipeBackActivity {

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("回复");

        final Intent intent = getIntent();
        String topicId = intent.getStringExtra(RubyChinaArgKeys.TOPIC_ID);

        ReplyFragment replyFragment = new ReplyFragment();

        Bundle args = new Bundle();
        args.putString(RubyChinaArgKeys.TOPIC_ID, topicId);
        replyFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.reply, replyFragment).commit();
    }
}