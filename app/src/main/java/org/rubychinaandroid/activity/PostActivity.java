package org.rubychinaandroid.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;

import org.rubychinaandroid.R;
import org.rubychinaandroid.api.RubyChinaApiListener;
import org.rubychinaandroid.api.RubyChinaApiWrapper;
import org.rubychinaandroid.fragments.PostFragment;
import org.rubychinaandroid.utils.FavouriteUtils;
import org.rubychinaandroid.utils.RubyChinaArgKeys;
import org.rubychinaandroid.utils.Utility;
import org.rubychinaandroid.utils.oauth.OAuthManager;
import org.rubychinaandroid.view.JumpToolbar;

import java.util.ArrayList;

public class PostActivity extends BaseActivity {
    private String TAG = "PostActivity";

    private FloatingActionButton mReplyButton;
    boolean mIsFavourite = false;
    private String mTopicId;

    @Override
    public void configToolbar() {
        mToolbar = (JumpToolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("话题内容");
        mToolbar.inflateMenu(R.menu.menu_post);
        setToolbarBackButton();

        // Set the favourite menu item's state according to history and logging state and
        // set click listener correspondingly.
        final Intent intent = getIntent();
        mTopicId = intent.getStringExtra(RubyChinaArgKeys.TOPIC_ID);
        ((JumpToolbar)mToolbar).attachTo(createFragment(mTopicId));
        configMenuItem(mTopicId);
    }

    private PostFragment createFragment(String topicId) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(RubyChinaArgKeys.TOPIC_ID, topicId);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        configToolbar();

        mReplyButton = (FloatingActionButton) findViewById(R.id.floating_action_button);
        mReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostActivity.this, ReplyActivity.class);
                intent.putExtra(RubyChinaArgKeys.TOPIC_ID, mTopicId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
    }

    public FloatingActionButton getFloatingActionButton() {
        return mReplyButton;
    }

    private void configMenuItem(final String topicId) {
        if (!OAuthManager.getInstance().isLoggedIn()) {
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.favourite) {
                        Utility.showToast("还没有登录哦");
                    }
                    return false;
                }
            });
            return;
        }

        ArrayList<String> topicIds = FavouriteUtils.loadFavourites();
        if (topicIds.contains(topicId)) {
            mIsFavourite = true;
            mToolbar.getMenu().getItem(0).setIcon(R.drawable.ic_post_favourite_active);
        }

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.favourite) {
                    if (mIsFavourite) {
                        item.setIcon(R.drawable.ic_post_favourite);
                        mIsFavourite = false;
                        final ProgressDialog dialog = ProgressDialog.show(
                                PostActivity.this, null, "正在取消收藏", true);
                        RubyChinaApiWrapper.unFavouriteTopic(topicId, new RubyChinaApiListener() {
                            @Override
                            public void onSuccess(Object data) {
                                Utility.showToast("已取消收藏");
                                FavouriteUtils.eraseFavourite(topicId);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                    }
                                }, 500);
                            }

                            @Override
                            public void onFailure(String data) {
                                Utility.showToast("Error:" + data);
                                dialog.dismiss();
                            }
                        });
                    } else {
                        item.setIcon(R.drawable.ic_post_favourite_active);
                        mIsFavourite = true;
                        final ProgressDialog dialog = ProgressDialog.show(
                                PostActivity.this, null, "正在收藏", true);
                        RubyChinaApiWrapper.favouriteTopic(topicId, new RubyChinaApiListener() {
                            @Override
                            public void onSuccess(Object data) {
                                Utility.showToast("已收藏");
                                FavouriteUtils.recordFavourite(topicId);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                    }
                                }, 500);
                            }

                            @Override
                            public void onFailure(String data) {
                                Utility.showToast("Error:" + data);
                                dialog.dismiss();
                            }
                        });
                    }
                }
                return false;
            }
        });
    }
}