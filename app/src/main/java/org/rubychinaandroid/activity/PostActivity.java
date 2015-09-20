package org.rubychinaandroid.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.rubychinaandroid.MyApplication;
import org.rubychinaandroid.R;
import org.rubychinaandroid.api.RubyChinaApiListener;
import org.rubychinaandroid.api.RubyChinaApiWrapper;
import org.rubychinaandroid.model.PostModel;
import org.rubychinaandroid.utils.RubyChinaConstants;
import org.rubychinaandroid.utils.RubyChinaTypes;
import org.rubychinaandroid.utils.Utility;
import org.rubychinaandroid.view.RichTextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class PostActivity extends SwipeBackActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Button mButtonDisplayReply;

    private TextView mTitle;
    private RichTextView mContent;
    private ImageView mAvatar;
    private TextView mAuthor;
    private TextView mTime;
    private TextView mNode;
    private String mTopicId;

    private Toolbar mToolbar;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // Creating The Toolbar and setting it as the Toolbar for the activity
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("话题内容");

        View postLayout = findViewById(R.id.post_layout);

        mTitle = (TextView) postLayout.findViewById(R.id.read_post_title);
        mContent = (RichTextView) postLayout.findViewById(R.id.read_post_content);
        mAvatar = (ImageView) postLayout.findViewById(R.id.read_post_author_avatar);
        mAuthor = (TextView) postLayout.findViewById(R.id.read_post_author);
        mTime = (TextView) postLayout.findViewById(R.id.read_post_publish_time);
        mNode = (TextView) postLayout.findViewById(R.id.read_post_node_name);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_red_dark, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);

        final Intent intent = getIntent();
        mTopicId = intent.getStringExtra(RubyChinaConstants.TOPIC_ID);

        mButtonDisplayReply = (Button) findViewById(R.id.button_display_reply);
        mButtonDisplayReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentForReplyActivity = new Intent(PostActivity.this, ReplyActivity.class);
                intentForReplyActivity.putExtra(RubyChinaConstants.TOPIC_ID, mTopicId);
                startActivity(intentForReplyActivity);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            }
        });

        // trigger the swipe refresh layout's animation
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                refreshTopic();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshTopic() {

        RubyChinaApiWrapper.getPostContent(mTopicId, new RubyChinaApiListener<PostModel>() {

            @Override
            public void onSuccess(PostModel data) {

                // stop the swipe refresh layout's animation
                mSwipeRefreshLayout.setRefreshing(false);

                mTitle.setText(data.getTopic().getTitle());

                boolean displayImage = true;
                mContent.setRichText(data.getBodyHtml(), displayImage);

                ImageLoader.getInstance().displayImage(data.getTopic().getUserAvatarUrl(),
                        mAvatar, MyApplication.imageLoaderOptions);

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

    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                refreshTopic();
            }
        }, 500);
    }
}
