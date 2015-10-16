package org.rubychinaandroid.fragments;

import android.os.Bundle;
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
import org.rubychinaandroid.api.RubyChinaApiListener;
import org.rubychinaandroid.api.RubyChinaApiWrapper;
import org.rubychinaandroid.model.UserModel;
import org.rubychinaandroid.utils.ScreenUtils;
import org.rubychinaandroid.utils.oauth.OAuthManager;
import org.rubychinaandroid.view.FootUpdate.HeaderViewRecyclerAdapter;

public class ProfileFragment extends TopicsFragment {
    private String TAG = "ProfileFragment";

    private View mProfileHeaderView;
    private ImageView mAvatar;
    private TextView mUsername;
    private TextView mEmail;

    private View mTopicsRootView;
    private HeaderViewRecyclerAdapter mHeaderAdapter;

    private String mUserLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTopicsRootView = super.onCreateView(inflater, container, savedInstanceState);

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
                ImageLoader.getInstance().displayImage(data.getAvatarUrl(), mAvatar,
                        MyApplication.getInstance().getImageLoaderOptions());
            }

            @Override
            public void onFailure(String data) {
                Log.d(TAG, "failure");
            }
        });

        mHeaderAdapter = new HeaderViewRecyclerAdapter(mRecyclerViewAdapter);
        mHeaderAdapter.addHeaderView(mProfileHeaderView);
        mRecyclerView.setAdapter(mHeaderAdapter);

        return mTopicsRootView;
    }
}