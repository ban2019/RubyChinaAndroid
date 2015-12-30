package org.rubychinaandroid.activity;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;

import org.rubychinaandroid.R;
import org.rubychinaandroid.api.RubyChinaApiListener;
import org.rubychinaandroid.api.RubyChinaApiWrapper;
import org.rubychinaandroid.model.NodeModel;
import org.rubychinaandroid.utils.Utility;
import org.rubychinaandroid.view.AllNodesAdapter;
import org.rubychinaandroid.view.IndexableRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AllNodesActivity extends BaseActivity
        implements RubyChinaApiListener<ArrayList<NodeModel>> {
    private static final String TAG = "AllNodesActivity";
    IndexableRecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    AllNodesAdapter mNodeAdapter;
    SwipeRefreshLayout mSwipeLayout;
    List<NodeModel> mNodes = new ArrayList<NodeModel>();
    boolean mIsAdapterSet = false;

    public void configToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("所有节点");
        setToolbarBackButton();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_nodes);
        configToolbar();

        mRecyclerView = (IndexableRecyclerView) findViewById(R.id.grid_all_node);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setFastScrollEnabled(true);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestNode();
            }
        });
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        mSwipeLayout.setRefreshing(true);
        requestNode();
    }

    @Override
    public void onSuccess(ArrayList<NodeModel> data) {
        mSwipeLayout.setRefreshing(false);
        mNodes.clear();
        mNodes.addAll(data);

        mNodeAdapter = new AllNodesAdapter(AllNodesActivity.this, mNodes);
        mNodeAdapter.update(data);
        if (!mIsAdapterSet) {
            mRecyclerView.setAdapter(mNodeAdapter);
            mIsAdapterSet = true;
        }
    }

    @Override
    public void onFailure(String error) {
        mSwipeLayout.setRefreshing(false);
        Utility.showToast("加载节点失败");
    }

    private void requestNode() {
        RubyChinaApiWrapper.getAllNodes(this);
    }
}
