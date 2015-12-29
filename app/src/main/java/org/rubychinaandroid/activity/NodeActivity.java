package org.rubychinaandroid.activity;


import android.content.Intent;
import android.os.Bundle;

import org.rubychinaandroid.R;
import org.rubychinaandroid.fragments.TopicsFragment;
import org.rubychinaandroid.utils.RubyChinaArgKeys;
import org.rubychinaandroid.view.JumpToolbar;

public class NodeActivity extends BaseActivity {
    public void configToolbar() {
        Intent intent = getIntent();
        String nodeName = intent.getStringExtra(RubyChinaArgKeys.NODE_NAME);
        String nodeId = intent.getStringExtra(RubyChinaArgKeys.NODE_ID);
        mToolbar = (JumpToolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle(nodeName);
        ((JumpToolbar) mToolbar).attachTo(createFragments(nodeId));
        setToolbarBackButton();
    }

    private TopicsFragment createFragments(String nodeId) {
        TopicsFragment fragment = new TopicsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RubyChinaArgKeys.NODE_ID, nodeId);
        bundle.putBoolean(RubyChinaArgKeys.IS_FROM_FAVOURITE_ACTIVITY, false);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node);
        configToolbar();
    }
}
