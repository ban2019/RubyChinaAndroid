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
    private SharedPreferences mPref;
    private ArrayList<TopicModel> mTopicList;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private FootUpdate mFootUpdate = new FootUpdate();
    // Whether no more topics can be provided.
    private boolean mNoMore = false;
    // Used to prevent onLoadMore being called again and again when
    // the selection stays at the end.
    private boolean isFailToLoadMore = false;

    // These member variables should be assigned by parameters passed by parent activity,
    // and at most one of the three is not null.
    private RubyChinaCategory mCategory;
    private String mUserLogin;
    private String mNode;

    private int mGetTopicsByWhat;
    private final int BY_CATEGORY = 0;
    private final int BY_USER = 1;
    private final int BY_NODE = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_topic, container, false);
        mParentActivity = (MainActivity) getActivity();
        
        // Parse parameters.
        Bundle args = getArguments();
        // There is history only for the topics under some category, none for topics by user,
        // so when request user's topics, there is no need to load shared preference.
        // (Only negative values are valid for category.)
        int value = args.getInt(RubyChinaConstants.TOPIC_CATEGORY);
        mCategory = new RubyChinaCategory(value);
        mUserLogin = args.getString(RubyChinaConstants.USER_LOGIN);
        mNode = args.getString(RubyChinaConstants.NODE);
        
        if (value < 0) {
            mPref = mParentActivity.getSharedPreferences(ACTIVITY_NAME, Context.MODE_PRIVATE);
            // Different TopicsFragment's mCachedPages instance is saved in separate files.
            mCachedPages = mPref.getInt(Integer.toString(mCategory.getValue()) + "mCachedPages", 0);
            mGetTopicsByWhat = BY_CATEGORY;
        } else if (mUserLogin != null) {
            mGetTopicsByWhat = BY_USER;
        } else if (mNode != null) {
            mGetTopicsByWhat = BY_NODE;
        }
        
        mTopicList = new ArrayList<TopicModel>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mParentActivity));
        mRecyclerViewAdapter = new TopicItemAdapter(mParentActivity, mTopicList, this);
        mHeaderAdapter = new HeaderViewRecyclerAdapter(mRecyclerViewAdapter);
        mRecyclerView.setAdapter(mHeaderAdapter);
        mFootUpdate.init(mHeaderAdapter, LayoutInflater.from(getActivity()), new FootUpdate.LoadMore() {
            @Override
            public void loadMore() {
                requestMoreTopics();
            }
        });
        mParentActivity.getFloatingActionButton().attachToRecyclerView(mRecyclerView);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_red_dark, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        mSwipeRefreshLayout.setRefreshing(true);

        requestTopics();
        return view;
    }

    public class TopicListHttpCallbackListener implements RubyChinaApiListener<ArrayList<TopicModel>> {
        @Override
        public void onSuccess(ArrayList<TopicModel> topicModelList) {

            // If it run out of topics, no more topics can be received.
            if (topicModelList.size() == 0) {
                mNoMore = true;
            }

            // Stop refresh anim.
            mSwipeRefreshLayout.setRefreshing(false);

            // Update the displayed topics
            if (mCachedPages > 0) {
                clearDBByCategory();
            }
            for (TopicModel topic : topicModelList) {
                mTopicList.add(topic);
            }
            mRecyclerViewAdapter.notifyDataSetChanged();

            // If getting topics by category, update the db.
            if (mGetTopicsByWhat == BY_CATEGORY) {
                saveToDBByCategory();
            }

            isFailToLoadMore = false;
        }

        @Override
        public void onFailure(String error) {
            Utility.showToast("加载话题列表失败");
            mSwipeRefreshLayout.setRefreshing(false);

            for (TopicModel topic : loadFromDBByCategory()) {
                mTopicList.add(topic);
            }
            mRecyclerViewAdapter.notifyDataSetChanged();
            isFailToLoadMore = true;
        }

        private void saveToDBByCategory() {
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

        private ArrayList<TopicModel> loadFromDBByCategory() {
            ArrayList<TopicModel> cachedTopics = (ArrayList<TopicModel>) RubyChinaDBManager
                    .getInstance(mParentActivity)
                    .loadTopics(mCategory, mCurrentPage);
            return cachedTopics;
        }

        private void clearDBByCategory() {
            for (int i = 0; i <= mCachedPages; i++) {
                RubyChinaDBManager.getInstance(mParentActivity)
                        .removeOnePageTopics(i, mCategory.getValue());
            }
            mCachedPages = 0;
        }
    }

    @Override
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
        if (!mNoMore && !isFailToLoadMore) {
            requestMoreTopics();
        }
    }

    private void requestTopics() {
        mCurrentPage = 0;
        mTopicList.clear();

        if (mGetTopicsByWhat == BY_CATEGORY) {
            requestTopicsByCategory();
        } else if (mGetTopicsByWhat == BY_USER) {
            requestTopicsByUserLogin();
        } else if (mGetTopicsByWhat == BY_NODE) {
            requestTopicsByNode();
        } else {
            Log.d(LOG_TAG, "Unknown problems");
        }
    }

    private void requestMoreTopics() {
        ++mCurrentPage;
        if (mGetTopicsByWhat == BY_CATEGORY) {
            requestTopicsByCategory();
        } else if (mGetTopicsByWhat == BY_USER) {
        } else if (mGetTopicsByWhat == BY_NODE) {
        }
    }

    private void requestTopicsByCategory() {
        RubyChinaApiWrapper.getTopicsByCategory(mCategory, mCurrentPage, new TopicListHttpCallbackListener());
    }
    private void requestTopicsByUserLogin() {
    }
    private void requestTopicsByNode() {
    }
    private void requestTopicsFavourite() {
    }
}