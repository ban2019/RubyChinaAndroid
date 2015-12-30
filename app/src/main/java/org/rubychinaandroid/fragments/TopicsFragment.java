package org.rubychinaandroid.fragments;

import android.content.Context;
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
import org.rubychinaandroid.utils.RubyChinaArgKeys;
import org.rubychinaandroid.utils.RubyChinaCategory;
import org.rubychinaandroid.utils.Utility;
import org.rubychinaandroid.view.FootUpdate.FootUpdate;
import org.rubychinaandroid.view.FootUpdate.HeaderViewRecyclerAdapter;
import org.rubychinaandroid.view.FootUpdate.OnScrollToBottomListener;
import org.rubychinaandroid.view.JumpToolbar;

import java.util.ArrayList;

public class TopicsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        OnScrollToBottomListener, JumpToolbar.ScrollCallback {
    private final String LOG_TAG = "TopicsFragment";
    private final String HINT_CACHE = "尝试从缓存加载";
    private final String HINT_FAIL = "加载话题列表失败";
    private String mErrorHint = HINT_FAIL;

    private Context mContext;
    RecyclerView mRecyclerView;
    HeaderViewRecyclerAdapter mHeaderAdapter;
    protected TopicItemAdapter mRecyclerViewAdapter;
    private ArrayList<TopicModel> mTopicList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FootUpdate mFootUpdate = new FootUpdate();
    // Whether no more topics can be provided.
    private boolean mNoMore = false;
    // Used to prevent onLoadMore being called again and again when
    // the selection stays at the end.
    private boolean isFailToLoadMore = false;
    private boolean isClearDB = false;
    private RubyChinaDBManager mDBManager;
    private int mPageIndex = 0;

    // These member variables should be assigned by parameters passed by parent activity,
    // and at most one of the three is not null.
    private RubyChinaCategory mCategory;
    private String mUserLogin;
    private String mNodeId;

    private final int INVALID = -1;
    private final int BY_CATEGORY = 0;
    private final int BY_USER = 1;
    private final int BY_USER_FAVOURITE = 2;
    private final int BY_NODE = 3;
    private int mGetTopicsByWhat = INVALID;

    private void parseArguments() {
        assert(mContext != null && mRecyclerView != null);
        // Parse parameters.
        Bundle args = getArguments();
        int value = args.getInt(RubyChinaArgKeys.TOPIC_CATEGORY);
        final int NO_MAPPING = 0; // getInt returns 0 when no mapping
        mCategory = new RubyChinaCategory(value);
        mUserLogin = args.getString(RubyChinaArgKeys.USER_LOGIN);
        mNodeId = args.getString(RubyChinaArgKeys.NODE_ID);

        if (value != NO_MAPPING) {
            mGetTopicsByWhat = BY_CATEGORY;
        } else if (mUserLogin != null) {
            boolean isFromFavouriteActivity = args
                    .getBoolean(RubyChinaArgKeys.IS_FROM_FAVOURITE_ACTIVITY);
            if (!isFromFavouriteActivity) {
                mGetTopicsByWhat = BY_USER;
            } else {
                mGetTopicsByWhat = BY_USER_FAVOURITE;
            }
        } else if (mNodeId != null) {
            mGetTopicsByWhat = BY_NODE;
        }

        if (mGetTopicsByWhat == BY_CATEGORY) {
            ((MainActivity) mContext).getFloatingActionButton().attachToRecyclerView(mRecyclerView);
        }
    }

    private void resetPageIndex() {
        assert(mGetTopicsByWhat != INVALID);
        // If browsing topics by node, the topics are got from HTML parsed by JSoup.
        // In browser, ?page=0 has the same effect as ?page=1, so, if mPageIndex is 0-based,
        // the first page will be loaded twice. So, mPageIndex must be 1-based for this case.
        mPageIndex = mGetTopicsByWhat == BY_NODE ? 1 : 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        mDBManager = RubyChinaDBManager.getInstance(mContext);

        View view = inflater.inflate(R.layout.fragment_topics, container, false);
        mTopicList = new ArrayList<TopicModel>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerViewAdapter = new TopicItemAdapter(mContext, mTopicList, this);

        mHeaderAdapter = new HeaderViewRecyclerAdapter(mRecyclerViewAdapter);
        mRecyclerView.setAdapter(mHeaderAdapter);
        mFootUpdate.init(mHeaderAdapter, LayoutInflater.from(getActivity()),
                new FootUpdate.LoadMore() {
            @Override
            public void loadMore() {
                requestMoreTopics();
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(
                android.R.color.holo_red_dark,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);
        mSwipeRefreshLayout.setRefreshing(true);

        parseArguments();
        resetPageIndex();
        requestTopics();
        return view;
    }

    public class TopicListHttpCallbackListener implements
            RubyChinaApiListener<ArrayList<TopicModel>> {
        @Override
        public void onSuccess(ArrayList<TopicModel> topicModelList) {
            mFootUpdate.showLoading();
            // If it run out of topics, no more topics can be received.
            if (topicModelList.size() == 0) {
                mNoMore = true;
                mFootUpdate.dismiss();
            }

            mSwipeRefreshLayout.setRefreshing(false); // Stop refresh anim.
            mRecyclerViewAdapter.setAllItemsEnabled(true);
            mTopicList.addAll(topicModelList);
            mRecyclerViewAdapter.notifyDataSetChanged();

            assert(mGetTopicsByWhat != INVALID);
            // Update the displayed topics
            if (mGetTopicsByWhat == BY_CATEGORY) {
                if (isClearDB) {
                    mDBManager.clearTopics(mCategory);
                }
                mDBManager.saveTopics(topicModelList, mCategory, mPageIndex);
            }

            ++mPageIndex;
            isFailToLoadMore = false;
        }

        @Override
        public void onFailure(String error) {
            mFootUpdate.showFail();
            if (!"Internal Server Error".equals(error)) {
                Utility.showToast(mErrorHint);
            }

            mSwipeRefreshLayout.setRefreshing(false);
            mRecyclerViewAdapter.setAllItemsEnabled(true);

            if (mGetTopicsByWhat == BY_CATEGORY) {
                ArrayList<TopicModel> topics = mDBManager.query(mCategory, mPageIndex);
                mTopicList.addAll(topics);
                ++mPageIndex;
            }

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
                mRecyclerViewAdapter.setAllItemsEnabled(false);
                requestTopics();
            }
        }, 500);
    }

    @Override
    public void onLoadMore() {
        mErrorHint = HINT_FAIL;
        if ((!mNoMore && !isFailToLoadMore)) {
            requestMoreTopics();
        } else if (mGetTopicsByWhat == BY_CATEGORY &&
                !mDBManager.isNoMoreEntries(mCategory, mPageIndex)) {
            mErrorHint = HINT_CACHE;
            requestMoreTopics();
        }

    }

    private void requestTopics() {
        isClearDB = true;
        resetPageIndex();
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
        RubyChinaApiWrapper.getTopicsByCategory(mCategory, mPageIndex,
                new TopicListHttpCallbackListener());
    }

    private void requestTopicsByUserLogin() {
        RubyChinaApiWrapper.getUserTopics(mUserLogin, mPageIndex,
                new TopicListHttpCallbackListener());
    }

    private void requestTopicsByNode() {
        RubyChinaApiWrapper.getNodeTopicsFromBrowser(mNodeId, mPageIndex,
                new TopicListHttpCallbackListener());
    }

    private void requestTopicsByFavourite() {
        RubyChinaApiWrapper.getFavouriteTopics(mUserLogin, mPageIndex,
                new TopicListHttpCallbackListener());
    }

    @Override
    public void scrollTo(int direction) {
        if (direction == View.FOCUS_DOWN) {
            mRecyclerView.scrollToPosition(mTopicList.size() - 1);
        } else if (direction == View.FOCUS_UP) {
            mRecyclerView.scrollToPosition(0);
        }
    }
}