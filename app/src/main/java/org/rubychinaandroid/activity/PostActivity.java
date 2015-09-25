package org.rubychinaandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.rubychinaandroid.R;
import org.rubychinaandroid.fragments.PostFragment;
import org.rubychinaandroid.fragments.ReplyFragment;
import org.rubychinaandroid.utils.RubyChinaConstants;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class PostActivity extends SwipeBackActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // Creating The Toolbar and setting it as the Toolbar for the activity
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("话题内容");

        final Intent intent = getIntent();
        String topicId = intent.getStringExtra(RubyChinaConstants.TOPIC_ID);
        Log.d("PostActivity", topicId);

        PostFragment postFragment = new PostFragment();

        Bundle args = new Bundle();
        args.putString(RubyChinaConstants.TOPIC_ID, topicId);
        postFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, postFragment).commit();
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
}
