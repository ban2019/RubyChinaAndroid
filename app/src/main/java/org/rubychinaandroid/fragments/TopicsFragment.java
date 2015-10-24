package org.rubychinaandroid.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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
import org.rubychinaandroid.utils.RubyChinaArgKeys;
import org.rubychinaandroid.utils.RubyChinaCategory;
import org.rubychinaandroid.utils.Utility;
import org.rubychinaandroid.view.FootUpdate.FootUpdate;
import org.rubychinaandroid.view.FootUpdate.HeaderViewRecyclerAdapter;
import org.rubychinaandroid.view.FootUpdate.OnScrollToBottomListener;

import java.util.ArrayList;

public class TopicsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnScrollToBottomListener {
    private final String LOG_TAG = "TopicsFragment";

    private AppCompatActivity mAppCompatActivity;
    private Activity mActivity;
    private Context mContext;
    private String mErrorHint;

    RecyclerView mRecyclerView;
    HeaderViewRecyclerAdapter mHeaderAdapter;
    protected TopicItemAdapter mRecyclerViewAdapter;
    private int mPageIndex = 0;
    private ArrayList<TopicModel> mTopicList;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private FootUpdate mFootUpdate = new FootUpdate();
    // Whether no more topics can be provided.
    private boolean mNoMore = false;
    // Used to prevent onLoadMore being called again and again when
    // the selection stays at the end.
    private boolean isFailToLoadMore = false;
    private boolean isClearDB = false;

    // These member variables should be assigned by parameters passed by parent activity,
    // and at most one of the three is not null.
    private RubyChinaCategory mCategory;
    private String mUserLogin;
    private String mNodeId;

    private int mGetTopicsByWhat;
    private final int BY_CATEGORY = 0;
    private final int BY_USER = 1;
    private final int BY_USER_FAVOURITE = 2;
    private final int BY_NODE = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topics, container, false);
        // Parse parameters.
        Bundle args = getArguments();

        int value = args.getInt(RubyChinaArgKeys.TOPIC_CATEGORY);
        int NO_MAPPING = 0; // getInt returns 0 when no mapping
        mCategory = new RubyChinaCategory(value);
        mUserLogin = args.getString(RubyChinaArgKeys.USER_LOGIN);
        mNodeId = args.getString(RubyChinaArgKeys.NODE_ID);

        boolean isFromFavouriteActivity = args.getBoolean(RubyChinaArgKeys.IS_FROM_FAVOURITE_ACTIVITY);

        // Store topics under categories only to db.
        if (value != NO_MAPPING) {
            mAppCompatActivity = (MainActivity) getActivity();
            // Different TopicsFragment's mCachedPages instance is saved in separate files.
            mGetTopicsByWhat = BY_CATEGORY;
            mContext = mAppCompatActivity;
        } else if (mUserLogin != null) {
            if (!isFromFavouriteActivity) {
                mGetTopicsByWhat = BY_USER;
            } else {
                mGetTopicsByWhat = BY_USER_FAVOURITE;
            }
            mActivity = getActivity();
            mContext = mActivity;
        } else if (mNodeId != null) {
            mGetTopicsByWhat = BY_NODE;
            mActivity = getActivity();
            mContext = mActivity;
        }

        mTopicList = new ArrayList<TopicModel>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        if (mGetTopicsByWhat == BY_CATEGORY) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mAppCompatActivity));
            mRecyclerViewAdapter = new TopicItemAdapter(mAppCompatActivity, mTopicList, this);
            ((MainActivity) mAppCompatActivity).getFloatingActionButton().attachToRecyclerView(mRecyclerView);
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
            mRecyclerViewAdapter = new TopicItemAdapter(mActivity, mTopicList, this);
        }

        mHeaderAdapter = new HeaderViewRecyclerAdapter(mRecyclerViewAdapter);
        mRecyclerView.setAdapter(mHeaderAdapter);
        mFootUpdate.init(mHeaderAdapter, LayoutInflater.from(getActivity()), new FootUpdate.LoadMore() {
            @Override
            public void loadMore() {
                requestMoreTopics();
            }
        });

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
            if (isClearDB && mGetTopicsByWhat == BY_CATEGORY) {
                RubyChinaDBManager.getInstance(mAppCompatActivity)
                        .clearAllTopicsByCategory(mCategory);
            }
            mTopicList.addAll(topicModelList);
            mRecyclerViewAdapter.notifyDataSetChanged();

            if (mGetTopicsByWhat == BY_CATEGORY) {
                RubyChinaDBManager.getInstance(mAppCompatActivity)
                        .saveTopics(topicModelList, mCategory, mPageIndex);
            }
            ++mPageIndex;
            isFailToLoadMore = false;
        }

        @Override
        public void onFailure(String error) {
            Utility.showToast("加载话题列表失败");
            mSwipeRefreshLayout.setRefreshing(false);

            if (mGetTopicsByWhat == BY_CATEGORY) {
                ArrayList<TopicModel> topics = RubyChinaDBManager.getInstance(mAppCompatActivity)
                        .loadTopics(mCategory, mPageIndex);
                mTopicList.addAll(topics);
            }
            ++mPageIndex;
            mRecyclerViewAdapter.notifyDataSetChanged();
            isFailToLoadMore = true;
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
        if ((!mNoMore && !isFailToLoadMore) ||
                !RubyChinaDBManager.getInstance(mContext).isAllTopicsLoaded(mCategory, mPageIndex)) {
            requestMoreTopics();
        }
    }

    private void requestTopics() {
        isClearDB = true;
        mPageIndex = 0;
        mTopicList.clear();

        if (mGetTopicsByWhat == BY_CATEGORY) {
            requestTopicsByCategory();
        } else if (mGetTopicsByWhat == BY_USER) {
            requestTopicsByUserLogin();
        } else if (mGetTopicsByWhat == BY_USER_FAVOURITE) {
            requestTopicsByFavourite();
        } else if (mGetTopicsByWhat == BY_NODE) {
            requestTopicsByNode();
        } else {
            Log.d(LOG_TAG, "Unknown problems");
        }
    }

    private void requestMoreTopics() {
        isClearDB = false;

        if (mGetTopicsByWhat == BY_CATEGORY) {
            requestTopicsByCategory();
        } else if (mGetTopicsByWhat == BY_USER) {
            requestTopicsByUserLogin();
        } else if (mGetTopicsByWhat == BY_USER_FAVOURITE) {
            requestTopicsByFavourite();
        } else if (mGetTopicsByWhat == BY_NODE) {
            requestTopicsByNode();
        }
    }

    private void requestTopicsByCategory() {
        RubyChinaApiWrapper.getTopicsByCategory(mCategory, mPageIndex, new TopicListHttpCallbackListener());
    }

    private void requestTopicsByUserLogin() {
        RubyChinaApiWrapper.getUserTopics(mUserLogin, mPageIndex, new TopicListHttpCallbackListener());
    }

    private void requestTopicsByNode() {
        RubyChinaApiWrapper.getNodeTopicsFromBrowser(mNodeId, mPageIndex, new TopicListHttpCallbackListener());
    }

    private void requestTopicsByFavourite() {
        RubyChinaApiWrapper.getFavouriteTopics(mUserLogin, mPageIndex, new TopicListHttpCallbackListener());
    }
}