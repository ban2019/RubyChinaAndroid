package org.rubychinaandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;

import org.rubychinaandroid.R;
import org.rubychinaandroid.adapter.ReplyItemAdapter;
import org.rubychinaandroid.api.RubyChinaApiListener;
import org.rubychinaandroid.api.RubyChinaApiWrapper;
import org.rubychinaandroid.model.ReplyModel;
import org.rubychinaandroid.utils.RubyChinaConstants;
import org.rubychinaandroid.utils.Utility;
import org.rubychinaandroid.view.ReplyBar;

import java.util.ArrayList;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public class ReplyActivity extends SwipeBackActivity implements SwipeRefreshLayout.OnRefreshListener {

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

        // keep the soft input keyboard away from covering the reply list
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Intent intent = getIntent();
        mTopicId = intent.getStringExtra(RubyChinaConstants.TOPIC_ID);

        ReplyBar replyBar = (ReplyBar) findViewById(R.id.reply_bar);
        // inform reply bar of the topic id
        replyBar.setTopicId(mTopicId);

        mReplyListView = (ListView) findViewById(R.id.reply_list_view);

        mAdapter = new ReplyItemAdapter(ReplyActivity.this, R.layout.reply_item, mReplyList);

        mReplyListView.setAdapter(mAdapter);

        RubyChinaApiWrapper.getPostReplies(mTopicId, new ReplyHttpCallbackListener());

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_red_dark, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
    }

    public class ReplyHttpCallbackListener implements RubyChinaApiListener<ArrayList<ReplyModel>> {

        @Override
        public void onSuccess(ArrayList<ReplyModel> data) {

            mReplyList.clear();
            for (ReplyModel reply : data) {
                mReplyList.add(reply);
            }

            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(String error) {
            Utility.showToast("加载回复失败");
        }
    }

    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                refreshReplies();
            }
        }, 500);
    }

    private void refreshReplies() {
        RubyChinaApiWrapper.getPostReplies(mTopicId, new ReplyHttpCallbackListener());
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