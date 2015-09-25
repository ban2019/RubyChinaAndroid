package org.rubychinaandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;

import org.rubychinaandroid.R;
import org.rubychinaandroid.adapter.ReplyItemAdapter;
import org.rubychinaandroid.fragments.ReplyFragment;
import org.rubychinaandroid.model.ReplyModel;
import org.rubychinaandroid.utils.RubyChinaConstants;


import java.util.ArrayList;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public class ReplyActivity extends SwipeBackActivity {

    Toolbar mToolbar;
    ListView mReplyListView;
    ArrayList<ReplyModel> mReplyList = new ArrayList<ReplyModel>();
    ReplyItemAdapter mAdapter;
    String mTopicId;

    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("回复");

        final Intent intent = getIntent();
        String topicId = intent.getStringExtra(RubyChinaConstants.TOPIC_ID);

        ReplyFragment replyFragment = new ReplyFragment();

        Bundle args = new Bundle();
        args.putString(RubyChinaConstants.TOPIC_ID, topicId);
        replyFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.reply, replyFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}