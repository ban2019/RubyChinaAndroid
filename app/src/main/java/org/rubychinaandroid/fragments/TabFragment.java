package org.rubychinaandroid.fragments;

import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import org.rubychinaandroid.R;
import org.rubychinaandroid.activity.MainActivity;
import org.rubychinaandroid.activity.PostActivity;
import org.rubychinaandroid.adapter.TopicItemAdapter;
import org.rubychinaandroid.api.RubyChinaApiListener;
import org.rubychinaandroid.api.RubyChinaApiWrapper;
import org.rubychinaandroid.db.RubyChinaDBManager;
import org.rubychinaandroid.model.TopicModel;
import org.rubychinaandroid.utils.RubyChinaConstants;
import org.rubychinaandroid.utils.RubyChinaTypes;
import org.rubychinaandroid.utils.Utility;

public class TabFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private final String ACTIVITY_NAME = "MainActivity";
    private final String LOG_TAG = "TabFragment";

    private MainActivity mParentActivity;

    RecyclerView mRecyclerView;

    private TextView mBlankTextView;
    private TopicItemAdapter mTopicItemAdapter;
    private int mCurrentPage = 0;
    private int mCachedPages = 0;
    private ArrayList<TopicModel> mTopicList;
    private RubyChinaTypes.TOPIC_CATEGORY mCategory;

    private LinearLayout footViewLayout;
    private Button prevButton;
    private Button nextButton;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab, container, false);

        mParentActivity = (MainActivity) getActivity();

        SharedPreferences pref = mParentActivity
                .getSharedPreferences(ACTIVITY_NAME, Context.MODE_PRIVATE);

        /* SET list view */
        footViewLayout = (LinearLayout) LayoutInflater.from(getActivity())
                .inflate(R.layout.foot_view, null);

        prevButton = (Button) footViewLayout.getChildAt(0);
        nextButton = (Button) footViewLayout.getChildAt(1);
        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);

        mTopicList = new ArrayList<TopicModel>();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mParentActivity));
        mRecyclerView.setAdapter(new TopicItemAdapter(mParentActivity, mTopicList));

        mBlankTextView = (TextView) view.findViewById(R.id.fragment_tab_text_view);

        Bundle args = getArguments();
        mCategory = RubyChinaTypes.TOPIC_CATEGORY.valueOf(args.getInt(RubyChinaConstants.TOPIC_CATEGORY));

        // Save each tabFragment's mCachedPages instance to different files
        mCachedPages = pref.getInt(Integer.toString(mCategory.getValue()) + "mCachedPages", 0);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_red_dark, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);

        mParentActivity.getFloatingActionButton().attachToRecyclerView(mRecyclerView);

        mSwipeRefreshLayout.setRefreshing(true);
        refreshTabTopics();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public class TopicListHttpCallbackListener implements RubyChinaApiListener<ArrayList<TopicModel>> {
        @Override
        public void onSuccess(ArrayList<TopicModel> topicModelList) {

            mSwipeRefreshLayout.setRefreshing(false);

            mBlankTextView.setVisibility(View.GONE);

            mTopicList.clear();
            for (TopicModel topic : topicModelList) {
                mTopicList.add(topic);
            }

            /* UPDATE the topic list in database */
            RubyChinaDBManager.getInstance(mParentActivity)
                    .removeOnePageTopics(mCurrentPage, mCategory.getValue());
            for (TopicModel topic : mTopicList) {
                RubyChinaDBManager.getInstance(mParentActivity)
                        .saveTopic(topic, mCategory, mCurrentPage);
            }

            if (mCurrentPage > 0) {
                prevButton.setEnabled(true);
            }
            nextButton.setEnabled(true);

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

            Utility.showToast("加载话题列表失败");

            if (mCurrentPage > mCachedPages) {
                mCurrentPage = mCachedPages;
                nextButton.setEnabled(false);
            } else if (mCurrentPage < mCachedPages) {
                nextButton.setEnabled(true);
            }

            if (mCurrentPage == 0) {
                prevButton.setEnabled(false);
            } else if (mCurrentPage > 0) {
                prevButton.setEnabled(true);
            } else {
                // Error
            }

            ArrayList<TopicModel> cachedTopics = (ArrayList<TopicModel>) RubyChinaDBManager
                    .getInstance(mParentActivity)
                    .loadTopics(mCategory, mCurrentPage);

            if (mTopicList == null && cachedTopics.size() == 0) {
                mBlankTextView.setVisibility(View.VISIBLE);
                return;
            }

            mTopicList.clear();
            for (TopicModel topic : cachedTopics) {
                mTopicList.add(topic);
            }
        }
    }

    @Override
    public void onClick(View v) {

        prevButton.setEnabled(false);
        nextButton.setEnabled(false);

        if (v.getId() == R.id.button_next_page) {
            ++mCurrentPage;
        } else if (v.getId() == R.id.button_prev_page && mCurrentPage >= 1) {
            --mCurrentPage;
        }

        refreshTabTopics();
    }

    public void refreshTabTopics() {
        RubyChinaApiWrapper.getTopics(mCurrentPage, mCategory,
                new TopicListHttpCallbackListener());
    }

    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                refreshTabTopics();
            }
        }, 500);
    }
}