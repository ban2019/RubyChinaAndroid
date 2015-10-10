package org.rubychinaandroid.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.rubychinaandroid.R;
import org.rubychinaandroid.activity.MainActivity;
import org.rubychinaandroid.adapter.TopicItemAdapter;
import org.rubychinaandroid.api.RubyChinaApiListener;
import org.rubychinaandroid.api.RubyChinaApiWrapper;
import org.rubychinaandroid.db.RubyChinaDBManager;
import org.rubychinaandroid.model.TopicModel;
import org.rubychinaandroid.utils.RubyChinaCategory;
import org.rubychinaandroid.utils.RubyChinaConstants;
import org.rubychinaandroid.utils.RubyChinaTypes;
import org.rubychinaandroid.utils.Utility;
import org.rubychinaandroid.view.FootUpdate.FootUpdate;
import org.rubychinaandroid.view.FootUpdate.HeaderViewRecyclerAdapter;
import org.rubychinaandroid.view.FootUpdate.OnScrollToBottomListener;

import java.util.ArrayList;

public class TopicsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnScrollToBottomListener {

    private final String ACTIVITY_NAME = "MainActivity";
    private final String LOG_TAG = "TopicsFragment";

    private MainActivity mParentActivity;

    RecyclerView mRecyclerView;

    HeaderViewRecyclerAdapter mHeaderAdapter;
    TopicItemAdapter mRecyclerViewAdapter;
    private int mCurrentPage = 0;
    private int mCachedPages = 0;
    private ArrayList<TopicModel> mTopicList;
    private RubyChinaCategory mCategory;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private FootUpdate mFootUpdate = new FootUpdate();
    private boolean mNoMore = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_topic, container, false);

        Log.d("TopicsFragment", "onCreateView");
        mParentActivity = (MainActivity) getActivity();

        SharedPreferences pref = mParentActivity
                .getSharedPreferences(ACTIVITY_NAME, Context.MODE_PRIVATE);

        mTopicList = new ArrayList<TopicModel>();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mParentActivity));

        Bundle args = getArguments();
        mCategory = new RubyChinaCategory(args.getInt(RubyChinaConstants.TOPIC_CATEGORY));

        // Save each TopicsFragment's mCachedPages instance to different files
        mCachedPages = pref.getInt(Integer.toString(mCategory.getValue()) + "mCachedPages", 0);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_red_dark, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);

        mParentActivity.getFloatingActionButton().attachToRecyclerView(mRecyclerView);

        mSwipeRefreshLayout.setRefreshing(true);

        mRecyclerViewAdapter = new TopicItemAdapter(mParentActivity, mTopicList, this);
        mHeaderAdapter = new HeaderViewRecyclerAdapter(mRecyclerViewAdapter);
        mRecyclerView.setAdapter(mHeaderAdapter);
        mFootUpdate.init(mHeaderAdapter, LayoutInflater.from(getActivity()), new FootUpdate.LoadMore() {
            @Override
            public void loadMore() {
                requestMoreTopics();
            }
        });

        requestTopics();

        return view;
    }

    public class TopicListHttpCallbackListener implements RubyChinaApiListener<ArrayList<TopicModel>> {
        @Override
        public void onSuccess(ArrayList<TopicModel> topicModelList) {

            if (topicModelList.size() == 0) {
                mNoMore = true;
            }

            // stop refresh anim
            mSwipeRefreshLayout.setRefreshing(false);

            // clear topic list
            for (int i = 0; i <= mCachedPages; i++) {
                RubyChinaDBManager.getInstance(mParentActivity)
                        .removeOnePageTopics(i, mCategory.getValue());
            }
            mTopicList.clear();

            for (TopicModel topic : topicModelList) {
                mTopicList.add(topic);
            }
            mRecyclerViewAdapter.notifyDataSetChanged();

            for (TopicModel topic : mTopicList) {
                RubyChinaDBManager.getInstance(mParentActivity)
                        .saveTopic(topic, mCategory, mCurrentPage);
            }

            if (mCurrentPage > mCachedPages) {
                mCachedPages = mCurrentPage;

                SharedPreferences.Editor editor = mParentActivity
                        .getSharedPreferences(ACTIVITY_NAME, Context.MODE_PRIVATE).edit();

                editor.putInt(Integer.toString(mCategory.getValue()) + "mCachedPages", mCachedPages);
                editor.commit();
            }
        }

        @Override
        public void onFailure(String error) {

            mSwipeRefreshLayout.setRefreshing(false);

            Utility.showToast("加载话题列表失败");

            ArrayList<TopicModel> cachedTopics = (ArrayList<TopicModel>) RubyChinaDBManager
                    .getInstance(mParentActivity)
                    .loadTopics(mCategory, mCurrentPage);

            mTopicList.clear();
            for (TopicModel topic : cachedTopics) {
                mTopicList.add(topic);
            }
            mRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    public void requestTopics() {
        mCurrentPage = 0;
        mCachedPages = 0;
        RubyChinaApiWrapper.getTopicsByCategory(mCategory, mCurrentPage, new TopicListHttpCallbackListener());
    }

    private void requestMoreTopics() {
        /*
        ++mCurrentPage;
        RubyChinaApiWrapper.getTopics(mCurrentPage, mCategory,
                new RubyChinaApiListener<ArrayList<TopicModel>>() {
                    @Override
                    public void onSuccess(ArrayList<TopicModel> topicModelList) {

                        for (TopicModel topic : topicModelList) {
                            mTopicList.add(topic);
                        }
                        mRecyclerViewAdapter.notifyDataSetChanged();

                        for (TopicModel topic : mTopicList) {
                            RubyChinaDBManager.getInstance(mParentActivity)
                                    .saveTopic(topic, mCategory, mCurrentPage);
                        }

                        if (mCurrentPage > mCachedPages) {
                            mCachedPages = mCurrentPage;

                            SharedPreferences.Editor editor = mParentActivity
                                    .getSharedPreferences(ACTIVITY_NAME, Context.MODE_PRIVATE).edit();

                            editor.putInt(Integer.toString(mCategory.getValue()) + "mCachedPages", mCachedPages);
                            editor.commit();
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                    }
                });
                */
    }

    public void onRefresh() {
        mNoMore = false;

        new Handler().postDelayed(new Runnable() {
            public void run() {
                // start refresh anim
                mSwipeRefreshLayout.setRefreshing(true);
                requestTopics();
            }
        }, 500);
    }

    @Override
    public void onLoadMore() {
        if (!mNoMore) {
            requestMoreTopics();
        }
    }

    private void requestTopicsByCategory() {
    }

    private void requestTopicsByUserLogin() {

    }

    private void requestTopicsByNode() {

    }

    private void requestTopicsFavourite() {

    }
}