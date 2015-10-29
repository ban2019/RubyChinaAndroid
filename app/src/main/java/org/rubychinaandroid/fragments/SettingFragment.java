package org.rubychinaandroid.fragments;


import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import org.rubychinaandroid.MyApplication;
import org.rubychinaandroid.R;
import org.rubychinaandroid.utils.FileUtils;
import org.rubychinaandroid.utils.RubyChinaArgKeys;
import org.rubychinaandroid.utils.oauth.OAuthManager;

public class SettingFragment extends PreferenceFragment {
    SharedPreferences mPreferences;
    Preference mCache;
    Preference mAbout;
    CheckBoxPreference mLoadImage;
    Preference mHelp;
    Button mLogout;
    MyApplication mApp = MyApplication.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        ViewGroup root = (ViewGroup) getView();
        ListView localListView = (ListView) root.findViewById(android.R.id.list);
        localListView.setBackgroundColor(0);
        localListView.setCacheColorHint(0);
        root.removeView(localListView);

        ViewGroup localViewGroup = (ViewGroup) LayoutInflater.from(getActivity())
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.settings_dialog_hint)
                        .setMessage(R.string.settings_logout_or_not)
                        .setPositiveButton(R.string.title_confirm_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                OAuthManager.logOut();
                                Intent intent = new Intent();
                                getActivity().setResult(RubyChinaArgKeys.RESULT_LOGGED_OUT, intent);
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton(R.string.title_confirm_cancel, null).show();
            }
        });

        // 加载图片
        mLoadImage = (CheckBoxPreference) findPreference("pref_noimage_nowifi");
        //mLoadImage.setChecked(!mApp.isLoadImageInMobileNetwork());
        mLoadImage.setSummary(mLoadImage.isChecked()
                ? R.string.settings_no_image_no_wifi_summary
                : R.string.settings_image_no_wifi_summary);
        mLoadImage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                //mApp.setConfigLoadImageInMobileNetwork(!mLoadImage.isChecked());
                mLoadImage.setSummary(mLoadImage.isChecked()
                        ? R.string.settings_no_image_no_wifi_summary
                        : R.string.settings_image_no_wifi_summary);
                return true;
            }
        });

        // 清除缓存
        mCache = findPreference("pref_cache");
        //mCache.setSummary(FileUtils.getFileSize(FileUtils.getCacheSize(getActivity())));
        mCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.settings_dialog_hint)
                        .setMessage(R.string.settings_clear_cache_or_not)
                        .setPositiveButton(R.string.title_confirm_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //FileUtils.clearAppCache(getActivity());
                                mCache.setSummary("0KB");
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
    }

    private void showAboutMe() {
        //Intent intent = new Intent(getActivity(), AboutActivity.class);
        //startActivity(intent);
    }

}
