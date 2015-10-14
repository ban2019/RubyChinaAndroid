package org.rubychinaandroid.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.rubychinaandroid.MyApplication;
import org.rubychinaandroid.R;
import org.rubychinaandroid.adapter.TopicItemAdapter;
import org.rubychinaandroid.api.RubyChinaApiListener;
import org.rubychinaandroid.api.RubyChinaApiWrapper;
import org.rubychinaandroid.model.TopicModel;
import org.rubychinaandroid.model.UserModel;
import org.rubychinaandroid.utils.ScreenUtils;
import org.rubychinaandroid.utils.oauth.OAuthManager;
import org.rubychinaandroid.view.FootUpdate.HeaderViewRecyclerAdapter;
import org.rubychinaandroid.view.FootUpdate.OnScrollToBottomListener;

import java.util.ArrayList;

public class ProfileFragment extends TopicsFragment/* implements SwipeRefreshLayout.OnRefreshListener, OnScrollToBottomListener*/ {
    private String TAG = "ProfileFragment";

    private View mProfileHeaderView;
    private ImageView mAvatar;
    private TextView mUsername;
    private TextView mEmail;

    private View mTopicsRootView;
    //private ArrayList<TopicModel> mUserTopics;
    //private RecyclerView mRecyclerView;
    //private TopicItemAdapter mRecyclerViewAdapter;
    private HeaderViewRecyclerAdapter mHeaderAdapter;

    //private SwipeRefreshLayout mSwipeRefreshLayout;

    private String mUserLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //mTopicsRootView = inflater.inflate(R.layout.fragment_topics, container, false);
        mTopicsRootView = super.onCreateView(inflater, container, savedInstanceState);

        //mRecyclerView = (RecyclerView) mTopicsRootView.findViewById(R.id.recycler_view);
        //mSwipeRefreshLayout = (SwipeRefreshLayout) mTopicsRootView.findViewById(R.id.swipe_refresh);

        // Set header's height according to ratio.
        mProfileHeaderView = inflater.inflate(R.layout.fragment_profile, container, false);
        int ScreenHeight = ScreenUtils.getDisplayHeight(getActivity());
        float headerDisplayPercent = 0.22f;
        int height = (int) (ScreenHeight * headerDisplayPercent);
        RecyclerView.LayoutParams headerLayoutParams = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height);
        mProfileHeaderView.setLayoutParams(headerLayoutParams);

        mAvatar = (ImageView) mProfileHeaderView.findViewById(R.id.avatar);
        mUsername = (TextView) mProfileHeaderView.findViewById(R.id.username);
        mEmail = (TextView) mProfileHeaderView.findViewById(R.id.email);

        mUserLogin = OAuthManager.getInstance().getUserLogin();
        assert (!"".equals(mUserLogin));

        RubyChinaApiWrapper.getUserProfile(mUserLogin, new RubyChinaApiListener<UserModel>() {
            @Override
            public void onSuccess(UserModel data) {
                mUsername.setText(data.getName());
                mEmail.setText(data.getEmail());
                ImageLoader.getInstance().displayImage(data.getAvatarUrl(), mAvatar, MyApplication.imageLoaderOptions);
            }

            @Override
            public void onFailure(String data) {
                Log.d(TAG, "failure");
            }
        });

        //mUserTopics = new ArrayList<TopicModel>();

        //mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //mRecyclerViewAdapter = new TopicItemAdapter(getActivity().getApplicationContext(), mUserTopics, this);
        mHeaderAdapter = new HeaderViewRecyclerAdapter(mRecyclerViewAdapter);
        mHeaderAdapter.addHeaderView(mProfileHeaderView);
        mRecyclerView.setAdapter(mHeaderAdapter);

        //mSwipeRefreshLayout.setOnRefreshListener(this);

        return mTopicsRootView;
    }

    /*
    @Override
    public void onLoadMore() {
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                // start refresh anim
                mSwipeRefreshLayout.setRefreshing(true);
                mUserLogin = "juanito";
                RubyChinaApiWrapper.getUserTopics(0, mUserLogin, new RubyChinaApiListener<ArrayList<TopicModel>>() {
                    @Override
                    public void onSuccess(ArrayList<TopicModel> data) {
                        // stop refresh anim
                        mSwipeRefreshLayout.setRefreshing(false);
                        for (TopicModel topic : data) {
                            mUserTopics.add(topic);
                        }
                        mRecyclerViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String error) {
                        // stop refresh anim
                        mSwipeRefreshLayout.setRefreshing(false);
                        Log.d(TAG, "error:" + error);
                    }
                });
            }
        }, 500);
    }*/
}