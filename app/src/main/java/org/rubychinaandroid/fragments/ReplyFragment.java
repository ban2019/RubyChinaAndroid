package org.rubychinaandroid.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import org.rubychinaandroid.R;
import org.rubychinaandroid.adapter.ReplyItemAdapter;
import org.rubychinaandroid.api.RubyChinaApiListener;
import org.rubychinaandroid.api.RubyChinaApiWrapper;
import org.rubychinaandroid.model.ReplyModel;
import org.rubychinaandroid.utils.RubyChinaConstants;
import org.rubychinaandroid.utils.Utility;
import org.rubychinaandroid.view.FootUpdate.FootUpdate;
import org.rubychinaandroid.view.FootUpdate.HeaderViewRecyclerAdapter;
import org.rubychinaandroid.view.FootUpdate.OnScrollToBottomListener;
import org.rubychinaandroid.view.ReplyInputBox;

import java.util.ArrayList;

public class ReplyFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private ArrayList<ReplyModel> mReplyList = new ArrayList<ReplyModel>();
    private ReplyItemAdapter mRecyclerViewAdapter;
    private String mTopicId;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private FootUpdate mFootUpdate = new FootUpdate();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reply, container, false);

        Activity activity = getActivity();

        // keep the soft input keyboard away from covering the reply list
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Intent intent = activity.getIntent();
        mTopicId = intent.getStringExtra(RubyChinaConstants.TOPIC_ID);

        ReplyInputBox replyInputBox = (ReplyInputBox) view.findViewById(R.id.reply_input_box);
        replyInputBox.setTopicId(mTopicId);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRecyclerViewAdapter = new ReplyItemAdapter(activity, mReplyList, new OnScrollToBottomListener() {
            @Override
            public void onLoadMore() {
                /*
                if (!mNoMore) {
                    requestMoreReplies();
                }
                */
            }
        });
        //mRecyclerView.setAdapter();

        mRecyclerViewAdapter = new ReplyItemAdapter(getActivity(), mReplyList, null);
        HeaderViewRecyclerAdapter mHeaderAdapter = new HeaderViewRecyclerAdapter(mRecyclerViewAdapter);
        mRecyclerView.setAdapter(mHeaderAdapter);
        mFootUpdate.init(mHeaderAdapter, LayoutInflater.from(getActivity()), new FootUpdate.LoadMore() {
            @Override
            public void loadMore() {
                //requestMoreTopics();
            }
        });

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

            mRecyclerViewAdapter.notifyDataSetChanged();
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
