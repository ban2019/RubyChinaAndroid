package org.rubychinaandroid.fragments;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.rubychinaandroid.MyApplication;
import org.rubychinaandroid.R;
import org.rubychinaandroid.activity.ReplyActivity;
import org.rubychinaandroid.api.RubyChinaApiListener;
import org.rubychinaandroid.api.RubyChinaApiWrapper;
import org.rubychinaandroid.model.PostModel;
import org.rubychinaandroid.utils.RubyChinaConstants;
import org.rubychinaandroid.utils.Utility;
import org.rubychinaandroid.view.RichTextView;


public class PostFragment extends Fragment {

    private TextView mTitle;
    private RichTextView mContent;
    private ImageView mAvatar;
    private TextView mAuthor;
    private TextView mTime;
    private TextView mNode;
    private String mTopicId;
    private FrameLayout mFrameLayout;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post, container, false);

        Log.d("PostFragment", "onCreateView");

        View postLayout = view.findViewById(R.id.id_post);

        mTitle = (TextView) postLayout.findViewById(R.id.title);
        mContent = (RichTextView) postLayout.findViewById(R.id.content);
        mAvatar = (ImageView) postLayout.findViewById(R.id.avatar);
        mAuthor = (TextView) postLayout.findViewById(R.id.author);
        mTime = (TextView) postLayout.findViewById(R.id.time);
        mNode = (TextView) postLayout.findViewById(R.id.node);

        mFrameLayout = (FrameLayout) view.findViewById(R.id.frame_post);

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
        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_red_dark, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);

        Button button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReplyActivity.class);
                intent.putExtra(RubyChinaConstants.TOPIC_ID, mTopicId);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        mTopicId = args.getString(RubyChinaConstants.TOPIC_ID);

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

                mFrameLayout.setVisibility(View.VISIBLE);

                // stop the swipe refresh layout's animation
                mSwipeRefreshLayout.setRefreshing(false);

                mTitle.setText(data.getTopic().getTitle());

                boolean displayImage = true;
                mContent.setRichText(data.getBodyHtml(), displayImage);

                Log.d("Post", "Image to be loaded: " + data.getTopic().getUserAvatarUrl());

                ImageLoader.getInstance().displayImage(data.getTopic().getUserAvatarUrl(),
                        mAvatar, MyApplication.imageLoaderOptions);

                Log.d("Post", "Image has been loaded: " + data.getTopic().getUserAvatarUrl());

                String author = data.getTopic().getUserName();
                author = ("".equals(author) ? data.getTopic().getUserLogin() : author);
                mAuthor.setText(author);

                mTime.setText(data.getTopic().getCreatedTime());

                mNode.setText(data.getTopic().getNodeName());
            }

            @Override
            public void onFailure(String error) {

                // stop the swipe refresh layout's animation
                mSwipeRefreshLayout.setRefreshing(false);

                Utility.showToast("加载帖子失败");
            }
        });
    }
}
