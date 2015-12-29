package org.rubychinaandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import org.rubychinaandroid.utils.FavouriteUtils;
import org.rubychinaandroid.utils.RubyChinaArgKeys;
import org.rubychinaandroid.utils.Utility;
import org.rubychinaandroid.utils.oauth.OAuthManager;
import org.rubychinaandroid.view.SlidingTabLayout;
import org.scribe.model.Token;


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
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView mDrawerUsername;

    private final String Titles[] = {"精华贴", "无人问津", "最后回复", "最近创建"};
    private final int NumOfTabs = Titles.length;

    // REQUEST CODES used to recognize the result returned by different activity
    private final int LOGIN_ACTIVITY_REQUEST_CODE = 1;
    private final int NEW_ACTIVITY_REQUEST_CODE = 2;
    private final int SETTING_ACTIVITY_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creating The Toolbar and setting it as the Toolbar for the activity
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("Ruby China");
        setSupportActionBar(mToolbar);

        // Set the toolbar to display burger icon animation when drawer is closed or opened.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, 0, 0);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, NumOfTabs);

        // Assigning ViewPager View and setting the adapter
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        // cache pages
        mPager.setOffscreenPageLimit(3);

        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        mTabs.setDistributeEvenly(true);
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
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });

        configDrawer();
    }

    public FloatingActionButton getFloatingActionButton() {
        return mAddButton;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOGIN_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Utility.showToast("已登录");
                    Token accessToken = (Token) data.getSerializableExtra(OAuthManager.Keys.ACCESS_TOKEN);
                    // Save access token persistently
                    OAuthManager.getInstance().saveAccessTokenString(accessToken.getToken());
                    OAuthManager.getInstance().saveLoggedInState(true);
                    // request the user login and store it
                    RubyChinaApiWrapper.hello(new RubyChinaApiListener<UserModel>() {
                        @Override
                        public void onSuccess(UserModel data) {
                            OAuthManager.getInstance().saveUserLogin(data.getUserLogin());
                            OAuthManager.getInstance().saveAvatarUrl(data.getAvatarUrl());
                            // Update the drawer.
                            mDrawerUsername.setText(data.getName());
                            ImageLoader.getInstance().displayImage(data.getAvatarUrl(), mDrawerAvatar,
                                    MyApplication.getInstance().getImageLoaderOptions());
                            FavouriteUtils.updateFavouriteRecord();
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
            case SETTING_ACTIVITY_REQUEST_CODE:
                if (resultCode == RubyChinaArgKeys.RESULT_LOGGED_OUT) {
                    // Reset drawer.
                    mDrawerUsername.setText("未命名");
                    ImageLoader.getInstance().displayImage(
                            "drawable://" + R.drawable.avatar_default,
                            mDrawerAvatar,
                            MyApplication.getInstance().getImageLoaderOptions());
                }
                break;
            default:
                break;
        }
    }

    private void configDrawer() {
        mDrawerAvatar = (ImageView) findViewById(R.id.avatar);
        mDrawerUsername = (TextView) findViewById(R.id.username);

        if (OAuthManager.getInstance().isLoggedIn()) {
            String url = OAuthManager.getInstance().getAvatarUrl();
            String login = OAuthManager.getInstance().getUserLogin();
            if (!"".equals(url) && !"".equals(login)) {
                ImageLoader.getInstance().displayImage(
                        url,
                        mDrawerAvatar,
                        MyApplication.getInstance().getImageLoaderOptions());
                mDrawerUsername.setText(login);
            } else {
                RubyChinaApiWrapper.getUserProfile(OAuthManager.getInstance().getUserLogin(), new RubyChinaApiListener<UserModel>() {
                    @Override
                    public void onSuccess(UserModel data) {
                        mDrawerUsername.setText(data.getName());
                        ImageLoader.getInstance().displayImage(data.getAvatarUrl(), mDrawerAvatar,
                                MyApplication.getInstance().getImageLoaderOptions());
                    }
                    @Override
                    public void onFailure(String data) {
                    }
                });
            }
        }

        mDrawerAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (OAuthManager.getInstance().isLoggedIn()) {
                    intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.putExtra(RubyChinaArgKeys.USER_LOGIN, OAuthManager.getInstance().getUserLogin());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_in);
                } else {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, LOGIN_ACTIVITY_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_in);
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
                        if (menuItem.getItemId() == R.id.favourite) {
                            Intent intent = new Intent(MainActivity.this, FavouriteActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                        } else if (menuItem.getItemId() == R.id.all_nodes) {
                            Intent intent = new Intent(MainActivity.this, AllNodesActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                        } else if (menuItem.getItemId() == R.id.setting) {
                            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                            startActivityForResult(intent, SETTING_ACTIVITY_REQUEST_CODE);
                            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                        }

                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }
}
