package org.rubychinaandroid.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.rubychinaandroid.MyApplication;
import org.rubychinaandroid.R;
import org.rubychinaandroid.api.RubyChinaApiListener;
import org.rubychinaandroid.api.RubyChinaApiWrapper;
import org.rubychinaandroid.model.NodeModel;
import org.rubychinaandroid.utils.RubyChinaConstants;
import org.rubychinaandroid.utils.Utility;
import org.rubychinaandroid.utils.oauth.OAuthManager;

import java.util.ArrayList;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public class NewActivity extends SwipeBackActivity {

    private Context mContext;
    private Spinner mNodesSpinner;
    private String mSelectedNodeId;

    private TextView mTitleTextView;
    private TextView mContentTextView;

    private Button mPublishButton;
    private Button mPreviewButton;

    private Toolbar mToolbar;

    private static final String[] mNodeNameCache = {
            "Ruby", "Rails", "Gem", "Python", "JavaScript", "MongoDB", "Redis", "Git",
            "Database", "Linux", "Nginx", "公告", "反馈", "社区开发", "工具控", "分享",
            "瞎扯淡", "其他", "重构", "产品控", "RubyTuesday", "iOS", "Android", "Go",
            "Erlang", "Testing", "书籍", "搜索分词", "算法", "CSS", "Mac", "Sinatra",
            "部署", "Mailer", "开源项目", "Obj-C", "插画", "RubyConf", "新手问题",
            "数学", "JRuby", "运维", "创业", "线下活动", "Clojure", "Haskell", "安全",
            "移民", "云服务", "健康", "求职", "mRuby", "Swift", "Elixir", "Rust",
            "AngularJS", "招聘", "NoPoint", "翻译", "产品推广", "ReactJS", "EmberJS",
            "RVM/Rbenv"
    };

    private static final String[] mNodeIdCache = {
            "1", "2", "3", "4", "5", "9", "10", "11", "12", "17", "18", "21", "22", "23",
            "24", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "37", "38",
            "39", "40", "41", "42", "43", "44", "46", "47", "48", "50", "51", "52", "53",
            "54", "55", "56", "57", "58", "59", "60", "62", "20", "63", "64", "66", "67",
            "65", "70", "71", "25", "61", "68", "69", "72", "73", "45"
    };

    final String LOG_TAG = "NewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("发表新帖");
        //setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleTextView = (TextView) findViewById(R.id.title);
        mContentTextView = (TextView) findViewById(R.id.content);

        mPublishButton = (Button) findViewById(R.id.publish_button);
        mPublishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPublishButton.setEnabled(false);

                if (!OAuthManager.getLoggedInState()) {
                    Utility.showToast("还没有在主页面登录哦");
                    mPublishButton.setEnabled(true);
                    return;
                }

                if ("".equals(mTitleTextView.getText().toString())) {
                    Utility.showToast("不要忘记加标题哦");
                    mPublishButton.setEnabled(true);
                    return;
                }

                if ("".equals(mContentTextView.getText().toString())) {
                    Utility.showToast("还没有写正文哦");
                    mPublishButton.setEnabled(true);
                    return;
                }

                RubyChinaApiWrapper.publishPost(mTitleTextView.getText().toString(),
                        mContentTextView.getText().toString(), mSelectedNodeId, new RubyChinaApiListener() {
                            @Override
                            public void onSuccess(Object data) {
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                Utility.showToast("发表成功");
                                finish();
                            }

                            @Override
                            public void onFailure(String error) {
                                Utility.showToast("发表失败");
                                mPublishButton.setEnabled(true);
                            }
                        });
            }
        });

        mPreviewButton = (Button) findViewById(R.id.preview_button);
        mPreviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewActivity.this, PreviewActivity.class);
                intent.putExtra(RubyChinaConstants.POST_CONTENT, mContentTextView.getText().toString());
                startActivity(intent);
            }
        });

        boolean updateNodes = false;
        if (updateNodes) {
            RubyChinaApiWrapper.getAllNodes(new RubyChinaApiListener<ArrayList<NodeModel>>() {
                @Override
                public void onSuccess(ArrayList<NodeModel> data) {

                    for (int i = 0; i < data.size(); i++) {
                        NodeModel node = data.get(i);
                        Log.d(LOG_TAG, node.getId());
                    }
                }

                @Override
                public void onFailure(String error) {
                }
            });
        }

        final int DEFAULT_NODE_INDEX = 1;
        mSelectedNodeId = mNodeIdCache[DEFAULT_NODE_INDEX];

        mContext = MyApplication.gAppContext;

        mNodesSpinner = (Spinner) findViewById(R.id.node_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, mNodeNameCache);
        adapter.setDropDownViewResource(R.layout.node_spinner);
        mNodesSpinner.setAdapter(adapter);

        mNodesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedNodeId = mNodeIdCache[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
}
