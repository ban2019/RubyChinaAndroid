package org.rubychinaandroid.activity;

import android.content.Intent;
import android.os.Bundle;

import org.rubychinaandroid.R;
import org.rubychinaandroid.fragments.ReplyFragment;
import org.rubychinaandroid.utils.RubyChinaArgKeys;
import org.rubychinaandroid.view.JumpToolbar;


public class ReplyActivity extends BaseActivity {
    public void configToolbar() {
        mToolbar = (JumpToolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("回复");
        final Intent intent = getIntent();
        String topicId = intent.getStringExtra(RubyChinaArgKeys.TOPIC_ID);
        ((JumpToolbar) mToolbar).attachTo(createFragment(topicId));
        setToolbarBackButton();
    }

    private ReplyFragment createFragment(String topicId) {
        ReplyFragment fragment = new ReplyFragment();

        Bundle args = new Bundle();
        args.putString(RubyChinaArgKeys.TOPIC_ID, topicId);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.reply, fragment).commit();
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        configToolbar();
    }
}