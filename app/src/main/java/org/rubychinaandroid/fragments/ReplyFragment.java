package org.rubychinaandroid.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.rubychinaandroid.R;
import org.rubychinaandroid.activity.ReplyActivity;
import org.rubychinaandroid.adapter.ReplyItemAdapter;
import org.rubychinaandroid.api.RubyChinaApiListener;
import org.rubychinaandroid.api.RubyChinaApiWrapper;
import org.rubychinaandroid.model.ReplyModel;
import org.rubychinaandroid.utils.RubyChinaConstants;
import org.rubychinaandroid.utils.Utility;
import org.rubychinaandroid.view.ReplyInputBox;

import java.util.ArrayList;

public class ReplyFragment extends Fragment {

    private LinearLayout mReplyLinearLayout;
    private ListView mReplyListView;
    private ArrayList<ReplyModel> mReplyList = new ArrayList<ReplyModel>();
    private ReplyItemAdapter mAdapter;
    private String mTopicId;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reply, container, false);

        Activity activity = getActivity();

        mReplyLinearLayout = (LinearLayout) activity.findViewById(R.id.linear_layout_reply);

        // keep the soft input keyboard away from covering the reply list
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Intent intent = activity.getIntent();
        mTopicId = intent.getStringExtra(RubyChinaConstants.TOPIC_ID);

        ReplyInputBox replyBar = (ReplyInputBox) view.findViewById(R.id.reply_input_box);
        //ReplyBar replyBar = new ReplyBar(activity, null);
        //replyBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        // inform reply bar of the topic id
        replyBar.setTopicId(mTopicId);
        //mReplyLinearLayout.addView(replyBar);

        mReplyListView = (ListView) view.findViewById(R.id.reply_list_view);

        mAdapter = new ReplyItemAdapter(activity, R.layout.item_reply, mReplyList);

        mReplyListView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        refreshReplies();
                    }
                }, 500);
            }
        });

        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_red_dark, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                refreshReplies();
            }
        });

        return view;
    }

    public class ReplyHttpCallbackListener implements RubyChinaApiListener<ArrayList<ReplyModel>> {

        @Override
        public void onSuccess(ArrayList<ReplyModel> data) {

            mSwipeRefreshLayout.setRefreshing(false);

            mReplyList.clear();
            for (ReplyModel reply : data) {
                mReplyList.add(reply);
            }

            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(String error) {
            mSwipeRefreshLayout.setRefreshing(false);
            Utility.showToast("加载回复失败");
        }
    }

    private void refreshReplies() {
        RubyChinaApiWrapper.getPostReplies(mTopicId, new ReplyHttpCallbackListener());
    }
}
