package org.rubychinaandroid.activity;


import android.content.Intent;
import android.os.Bundle;

import org.rubychinaandroid.R;
import org.rubychinaandroid.fragments.TopicsFragment;
import org.rubychinaandroid.utils.RubyChinaArgKeys;
import org.rubychinaandroid.view.JumpToolbar;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class NodeActivity extends SwipeBackActivity {
    JumpToolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node);

        Intent intent = getIntent();
        String nodeName = intent.getStringExtra(RubyChinaArgKeys.NODE_NAME);
        String nodeId = intent.getStringExtra(RubyChinaArgKeys.NODE_ID);

        TopicsFragment fragment = new TopicsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RubyChinaArgKeys.NODE_ID, nodeId);
        bundle.putBoolean(RubyChinaArgKeys.IS_FROM_FAVOURITE_ACTIVITY, false);
        fragment.setArguments(bundle);

        mToolbar = (JumpToolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle(nodeName);
        mToolbar.attachTo(fragment);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }
}
