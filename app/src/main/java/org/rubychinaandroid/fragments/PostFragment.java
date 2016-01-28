package org.rubychinaandroid.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.melnykov.fab.ObservableScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.rubychinaandroid.MyApplication;
import org.rubychinaandroid.R;
import org.rubychinaandroid.activity.PostActivity;
import org.rubychinaandroid.activity.ProfileActivity;
import org.rubychinaandroid.api.RubyChinaApiListener;
import org.rubychinaandroid.api.RubyChinaApiWrapper;
import org.rubychinaandroid.model.PostModel;
import org.rubychinaandroid.utils.RubyChinaArgKeys;
import org.rubychinaandroid.utils.Utility;
import org.rubychinaandroid.view.JumpToolbar;
import org.rubychinaandroid.view.RichTextView;


public class PostFragment extends Fragment implements JumpToolbar.ScrollCallback {

    private TextView mTitle;
    private RichTextView mContent;
    private ImageView mAvatar;
    private TextView mAuthor;
    private TextView mTime;
    private TextView mNode;
    private String mTopicId;
    private FrameLayout mFrameLayout;
    private CardView mCardView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ObservableScrollView mScrollView;
    private Activity mHostActivity;
    private String mBody;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        View postLayout = view.findViewById(R.id.id_post);

        mTitle = (TextView) postLayout.findViewById(R.id.title);
        mContent = (RichTextView) postLayout.findViewById(R.id.content);
        mAvatar = (ImageView) postLayout.findViewById(R.id.avatar);
        mAuthor = (TextView) postLayout.findViewById(R.id.author);
        mTime = (TextView) postLayout.findViewById(R.id.time);
        mNode = (TextView) postLayout.findViewById(R.id.node);
        mCardView = (CardView) view.findViewById(R.id.card_container);
        mFrameLayout = (FrameLayout) view.findViewById(R.id.frame_post);
        mScrollView = (ObservableScrollView) view.findViewById(R.id.scroll_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        refreshTopic();
                    }
                }, 500);
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_dark,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);

        ((PostActivity) mHostActivity).getFloatingActionButton().attachToScrollView(mScrollView);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof PostActivity) {
            mHostActivity = activity;
        } else {
            throw new RuntimeException("The host activity should be of PostActivity type.");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        mTopicId = args.getString(RubyChinaArgKeys.TOPIC_ID);
        Log.d("PostFragment", "onActivityCreated");
        // trigger the swipe refresh layout's animation
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                refreshTopic();
            }
        });
    }

    public void refreshTopic() {
        RubyChinaApiWrapper.getPostContent(mTopicId, new RubyChinaApiListener<PostModel>() {
            @Override
            public void onSuccess(PostModel data) {
                mCardView.setVisibility(View.VISIBLE);
                mFrameLayout.setVisibility(View.VISIBLE);

                // stop the swipe refresh layout's animation
                mSwipeRefreshLayout.setRefreshing(false);
                mTitle.setText(data.getTopic().getTitle());
                boolean displayImage = Utility.isDisplayImageNow();
                mBody = data.getBody();
                mContent.setRichText(data.getBodyHtml(), displayImage);
                ImageLoader.getInstance().displayImage(data.getTopic().getUserAvatarUrl(),
                        mAvatar, MyApplication.getInstance().getImageLoaderOptions());
                final String userName = data.getTopic().getUserName();
                final String userLogin = data.getTopic().getUserLogin();
                String author = ("".equals(userName) ? userLogin : userLogin + "(" + userName + ")");
                mAuthor.setText(author);
                mTime.setText(data.getTopic().getCreatedTime());
                mNode.setText(data.getTopic().getNodeName());

                mAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mHostActivity, ProfileActivity.class);
                        intent.putExtra(RubyChinaArgKeys.USER_LOGIN, userLogin);
                        startActivity(intent);
                        mHostActivity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                    }
                });

                mSwipeRefreshLayout.setAlpha(0f);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                final long duration = 300;
                mSwipeRefreshLayout.animate()
                        .alpha(1f)
                        .setDuration(duration)
                        .setListener(null).start();
            }

            @Override
            public void onFailure(String error) {
                // stop the swipe refresh layout's animation
                mSwipeRefreshLayout.setRefreshing(false);
                Utility.showToast("加载帖子失败");
            }
        });
    }

    public void scrollTo(int direction) {
        mScrollView.fullScroll(direction);
    }

    public String getTitle() {
        return mTitle.getText().toString();
    }
    public String getContent() {
        return mBody;
    }
}
