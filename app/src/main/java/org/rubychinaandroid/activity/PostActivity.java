package org.rubychinaandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;

import org.rubychinaandroid.R;
import org.rubychinaandroid.api.RubyChinaApiListener;
import org.rubychinaandroid.api.RubyChinaApiWrapper;
import org.rubychinaandroid.fragments.PostFragment;
import org.rubychinaandroid.utils.RubyChinaArgKeys;
import org.rubychinaandroid.utils.Utility;
import org.rubychinaandroid.utils.oauth.OAuthManager;

import java.util.ArrayList;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class PostActivity extends SwipeBackActivity {

    private String TAG = "PostActivity";

    private Toolbar mToolbar;
    private FloatingActionButton mReplyButton;
    boolean mIsFavourite = false; // whether current topic is favourite

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // Creating The Toolbar and setting it as the Toolbar for the activity
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("话题内容");
        mToolbar.inflateMenu(R.menu.menu_post);

        final Intent intent = getIntent();
        final String topicId = intent.getStringExtra(RubyChinaArgKeys.TOPIC_ID);

        // Set the favourite menu item's state according to history and logging state and
        // set click listener correspondingly.
        configMenuItem(topicId);

        mReplyButton = (FloatingActionButton) findViewById(R.id.floating_action_button);
        mReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostActivity.this, ReplyActivity.class);
                intent.putExtra(RubyChinaArgKeys.TOPIC_ID, topicId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            }
        });

        PostFragment postFragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(RubyChinaArgKeys.TOPIC_ID, topicId);
        postFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, postFragment).commit();
    }

    public FloatingActionButton getFloatingActionButton() {
        return mReplyButton;
    }

    private void configMenuItem(final String topicId) {
        if (!OAuthManager.getLoggedInState()) {
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

        ArrayList<String> topicIds = Utility.readTopicsFromFile(RubyChinaArgKeys.MY_FAVOURITES);
        Log.d(TAG + "size:", "" + topicIds.size());
        if (topicIds.contains(topicId)) {
            mToolbar.getMenu().getItem(0).setIcon(R.drawable.ic_post_favourite_active);
        }

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.favourite) {
                    if (mIsFavourite) {
                        item.setIcon(R.drawable.ic_post_favourite);
                        mIsFavourite = false;
                    } else {
                        item.setIcon(R.drawable.ic_post_favourite_active);
                        mIsFavourite = true;
                        RubyChinaApiWrapper.favouriteTopic(topicId, new RubyChinaApiListener() {
                            @Override
                            public void onSuccess(Object data) {
                                Utility.showToast("已收藏");
                                Utility.storeTopicsToFile(RubyChinaArgKeys.MY_FAVOURITES, topicId);
                            }

                            @Override
                            public void onFailure(String data) {
                                Utility.showToast("Error:" + data);
                            }
                        });
                    }
                }
                return false;
            }
        });
    }
}
