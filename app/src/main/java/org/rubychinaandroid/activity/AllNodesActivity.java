package org.rubychinaandroid.activity;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;

import org.rubychinaandroid.R;
import org.rubychinaandroid.api.RubyChinaApiListener;
import org.rubychinaandroid.api.RubyChinaApiWrapper;
import org.rubychinaandroid.model.NodeModel;
import org.rubychinaandroid.utils.Utility;
import org.rubychinaandroid.view.AllNodesAdapter;
import org.rubychinaandroid.view.IndexableRecyclerView;

import java.util.ArrayList;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class AllNodesActivity extends SwipeBackActivity implements RubyChinaApiListener<ArrayList<NodeModel>> {
    private static final String TAG = "AllNodesActivity";
    IndexableRecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    AllNodesAdapter mNodeAdapter;
    SwipeRefreshLayout mSwipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_nodes);

        final Context context = this;
        mNodeAdapter = new AllNodesAdapter(context);
        mRecyclerView = (IndexableRecyclerView) findViewById(R.id.grid_all_node);

        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //mRecyclerView.setAdapter(mNodeAdapter);
        mRecyclerView.setFastScrollEnabled(true);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestNode(true);
            }
        });
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        mSwipeLayout.setRefreshing(true);
        requestNode(false);
    }

    @Override
    public void onSuccess(ArrayList<NodeModel> data) {
        mNodeAdapter.update(data);
        mSwipeLayout.setRefreshing(false);
        mRecyclerView.setAdapter(mNodeAdapter);
        mLayoutManager.scrollToPosition(0);
    }

    @Override
    public void onFailure(String error) {
        mSwipeLayout.setRefreshing(false);
        //MessageUtils.showErrorMessage(getActivity(), error);
        Utility.showToast(error);
    }

    private void requestNode(boolean refresh) {
        //V2EXManager.getAllNodes(getActivity(), refresh, this);
        RubyChinaApiWrapper.getAllNodes(this);
    }
}
