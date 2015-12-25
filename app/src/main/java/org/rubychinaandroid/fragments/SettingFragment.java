package org.rubychinaandroid.fragments;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import org.rubychinaandroid.MyConfig;
import org.rubychinaandroid.R;
import org.rubychinaandroid.utils.FavouriteUtils;
import org.rubychinaandroid.utils.FileUtils;
import org.rubychinaandroid.utils.RubyChinaArgKeys;
import org.rubychinaandroid.utils.Utility;
import org.rubychinaandroid.utils.oauth.OAuthManager;

public class SettingFragment extends PreferenceFragment {
    final String TAG = "SettingFragment";
    SharedPreferences mPreferences;
    Preference mCache;
    Preference mAbout;
    CheckBoxPreference mLoadImage;
    Preference mHelp;
    Button mLogout;
    Activity mHostActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mHostActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mHostActivity);

        ViewGroup root = (ViewGroup) getView();
        ListView localListView = (ListView) root.findViewById(android.R.id.list);
        localListView.setBackgroundColor(0);
        localListView.setCacheColorHint(0);
        root.removeView(localListView);

        ViewGroup localViewGroup = (ViewGroup) LayoutInflater.from(mHostActivity)
                .inflate(R.layout.fragment_setting, null);
        ((ViewGroup) localViewGroup.findViewById(R.id.setting_content))
                .addView(localListView, -1, -1);
        localViewGroup.setVisibility(View.VISIBLE);
        root.addView(localViewGroup);

        //退出登录
        mLogout = (Button) localViewGroup.findViewById(R.id.setting_logout);
        boolean isLogin = OAuthManager.getInstance().getLoggedInState();
        if (isLogin) {
            mLogout.setVisibility(View.VISIBLE);
        } else {
            mLogout.setVisibility(View.GONE);
        }
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mHostActivity);
                builder.setTitle(R.string.settings_dialog_hint)
                        .setMessage(R.string.settings_logout_or_not)
                        .setPositiveButton(R.string.title_confirm_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                OAuthManager.logOut();
                                FavouriteUtils.updateFavouriteRecord();
                                Intent intent = new Intent();
                                mHostActivity.setResult(RubyChinaArgKeys.RESULT_LOGGED_OUT, intent);
                                mHostActivity.finish();
                            }
                        }).setNegativeButton(R.string.title_confirm_cancel, null).show();
            }
        });

        // 加载图片
        mLoadImage = (CheckBoxPreference) findPreference("pref_noimage_nowifi");
        MyConfig.getInstance().setBooleanPreference(RubyChinaArgKeys.DISPLAY_IMAGE_IF_MOBILE,
                mLoadImage.isChecked());
        mLoadImage.setSummary(mLoadImage.isChecked()
                ? R.string.settings_no_image_no_wifi_summary
                : R.string.settings_image_no_wifi_summary);
        mLoadImage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                MyConfig.getInstance().setBooleanPreference(
                        RubyChinaArgKeys.DISPLAY_IMAGE_IF_MOBILE,
                        mLoadImage.isChecked());
                mLoadImage.setSummary(mLoadImage.isChecked()
                        ? R.string.settings_no_image_no_wifi_summary
                        : R.string.settings_image_no_wifi_summary);
                return true;
            }
        });

        // 清除缓存
        mCache = findPreference("pref_cache");
        mCache.setSummary(FileUtils.getCacheSize());
        mCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mHostActivity);
                builder.setTitle(R.string.settings_dialog_hint)
                        .setMessage(R.string.settings_clear_cache_or_not)
                        .setPositiveButton(R.string.title_confirm_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FileUtils.clearApplicationData();
                                mCache.setSummary(FileUtils.getCacheSize());
                            }
                        })
                        .setNegativeButton(R.string.title_confirm_cancel, null).show();
                return true;
            }
        });

        // 关于我们
        mAbout = findPreference("pref_about");
        mAbout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                showAboutMe();
                return true;
            }
        });

        mHelp = findPreference("pref_help");
        mHelp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mHostActivity);
                builder.setMessage("浏览帖子和评论时，长按标题栏可以跳转到最下，" +
                        "双击标题栏可以跳转到最上。")
                        .setCancelable(false)
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                builder.show();
                return false;
            }
        });
    }

    private void showAboutMe() {
        Utility.showToast("感谢使用");
    }

}
