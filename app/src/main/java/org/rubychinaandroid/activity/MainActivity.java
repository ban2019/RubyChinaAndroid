package org.rubychinaandroid.activity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.rubychinaandroid.MyApplication;
import org.rubychinaandroid.R;
import org.rubychinaandroid.adapter.ViewPagerAdapter;
import org.rubychinaandroid.api.RubyChinaApiListener;
import org.rubychinaandroid.api.RubyChinaApiWrapper;
import org.rubychinaandroid.model.UserModel;
import org.rubychinaandroid.utils.Utility;
import org.rubychinaandroid.utils.oauth.OAuthManager;
import org.rubychinaandroid.view.SlidingTabLayout;
import org.scribe.model.Token;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = "MainActivity";

    private Toolbar mToolbar;
    private ViewPager mPager;
    private ViewPagerAdapter mAdapter;
    private SlidingTabLayout mTabs;
    private FloatingActionButton mAddButton;

    private ImageView mDrawerAvatar;
    private NavigationView mDrawer;
    private DrawerLayout mDrawerLayout;
    private TextView mDrawerUsername;

    private final String Titles[] = {"精华贴", "无人问津", "最后回复", "最近创建"};
    private final int NumOfTabs = Titles.length;

    // REQUEST CODES used to recognize the result returned by different activity
    private final int LOGIN_ACTIVITY_REQUEST_CODE = 1;
    private final int NEW_ACTIVITY_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creating The Toolbar and setting it as the Toolbar for the activity
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("Ruby China");
        setSupportActionBar(mToolbar);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, NumOfTabs);

        // Assigning ViewPager View and setting the adapter
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        // cache pages
        mPager.setOffscreenPageLimit(3);

        // Assigning the Sliding Tab Layout View
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        mTabs.setViewPager(mPager);

        mAddButton = (FloatingActionButton) findViewById(R.id.floating_action_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewActivity.class);
                startActivityForResult(intent, NEW_ACTIVITY_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            }
        });

        mDrawerAvatar = (ImageView) findViewById(R.id.avatar);
        mDrawerUsername = (TextView) findViewById(R.id.username);

        if (OAuthManager.getInstance().getLoggedInState()) {
            RubyChinaApiWrapper.getUserProfile(OAuthManager.getInstance().getUserLogin(), new RubyChinaApiListener<UserModel>() {
                @Override
                public void onSuccess(UserModel data) {
                    mDrawerUsername.setText(data.getName());
                    //email.setText(data.getEmail());
                    ImageLoader.getInstance().displayImage(data.getAvatarUrl(), mDrawerAvatar, MyApplication.imageLoaderOptions);
                }

                @Override
                public void onFailure(String data) {
                }
            });
        }

        mDrawerAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (OAuthManager.getInstance().getLoggedInState()) {
                    intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                } else {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, LOGIN_ACTIVITY_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                }

                mDrawerLayout.closeDrawers();
            }
        });

        mDrawer = (NavigationView) findViewById(R.id.drawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    public FloatingActionButton getFloatingActionButton() {
        return mAddButton;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case LOGIN_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Utility.showToast("已登录");
                    Token accessToken = (Token) data.getSerializableExtra(OAuthManager.ACCESS_TOKEN);

                    // Save access token persistently
                    OAuthManager.getInstance().saveAccessTokenString(accessToken.getToken());
                    OAuthManager.getInstance().saveLoggedInState(true);

                    // request the user login and store it
                    RubyChinaApiWrapper.hello(new RubyChinaApiListener<UserModel>() {
                        @Override
                        public void onSuccess(UserModel data) {
                            OAuthManager.getInstance().saveUserLogin(data.getUserLogin());

                            // Update the drawer.
                            mDrawerUsername.setText(data.getName());
                            ImageLoader.getInstance().displayImage(data.getAvatarUrl(), mDrawerAvatar, MyApplication.imageLoaderOptions);
                            Log.d(LOG_TAG, data.getUserLogin());
                        }

                        @Override
                        public void onFailure(String data) {
                            Log.d(LOG_TAG, "onFailure");
                        }
                    });

                } else {
                    OAuthManager.getInstance().saveLoggedInState(false);
                    Utility.showToast("登录失败");
                }
                break;

            case NEW_ACTIVITY_REQUEST_CODE:

                if (resultCode == RESULT_OK) {
                    Log.d(LOG_TAG, "Succeeded to publish topic.");
                } else {
                    Log.d(LOG_TAG, "Failed to publish topic.");
                }
                break;

            default:
                break;
        }
    }
}
