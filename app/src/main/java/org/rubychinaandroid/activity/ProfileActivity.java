package org.rubychinaandroid.activity;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import org.rubychinaandroid.utils.oauth.OAuthManager;
import org.rubychinaandroid.view.FootUpdate.HeaderViewRecyclerAdapter;
import org.rubychinaandroid.view.FootUpdate.OnScrollToBottomListener;

import java.util.ArrayList;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class ProfileActivity extends SwipeBackActivity implements OnScrollToBottomListener {

    private String TAG = "ProfileActivity";

    private Toolbar mToolbar;
    private ImageView mAvatar;
    private TextView mUsername;
    private TextView mEmail;

    private ArrayList<TopicModel> mUserTopics;
    private RecyclerView mRecyclerView;
    private TopicItemAdapter mRecyclerViewAdapter;
    HeaderViewRecyclerAdapter mHeaderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("用户资料");

        mAvatar = (ImageView) findViewById(R.id.avatar);
        mUsername = (TextView) findViewById(R.id.username);
        mEmail = (TextView) findViewById(R.id.email);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mUserTopics = new ArrayList<TopicModel>();

        String userLogin = OAuthManager.getInstance().getUserLogin();
        assert(!"".equals(userLogin));

        RubyChinaApiWrapper.getUserProfile(userLogin, new RubyChinaApiListener<UserModel>() {
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

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewAdapter = new TopicItemAdapter(getApplicationContext(), mUserTopics, this);
        mHeaderAdapter = new HeaderViewRecyclerAdapter(mRecyclerViewAdapter);
        mRecyclerView.setAdapter(mHeaderAdapter);

        RubyChinaApiWrapper.getUserTopics(0, userLogin, new RubyChinaApiListener<ArrayList<TopicModel>>() {
            @Override
            public void onSuccess(ArrayList<TopicModel> data) {
                for (TopicModel topic : data) {
                    mUserTopics.add(topic);
                }
                mRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "error:" + error);
            }
        });
    }

    @Override
    public void onLoadMore() {

    }
}
