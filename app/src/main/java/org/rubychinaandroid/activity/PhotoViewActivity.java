package org.rubychinaandroid.activity;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.rubychinaandroid.R;
import org.rubychinaandroid.adapter.PhotoViewerPagerAdapter;
import org.rubychinaandroid.view.PhotoViewCallbacks;
import org.rubychinaandroid.view.PhotoViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class PhotoViewActivity extends SwipeBackActivity
        implements ViewPager.OnPageChangeListener,
        PhotoViewPager.OnInterceptTouchListener, PhotoViewCallbacks {
    public static final String EXTRA_PHOTO_INDEX = "photo_index";
    public static final String EXTRA_PHOTO_DATAS = "photo_arrays";

    private PhotoViewPager mViewPager;
    private PhotoViewerPagerAdapter mAdapter;
    private Toolbar mToolbar;

    private ArrayList<String> mPhotoUrls;

    /**
     * The index of the currently viewed photo
     */
    private int mCurrentPhotoIndex;

    /**
     * The listeners wanting full screen state for each screen position
     */
    private final Map<Integer, OnScreenListener>
            mScreenListeners = new HashMap<Integer, OnScreenListener>();

    public static void launch(Context context, int position, ArrayList<String> photoUrls) {
        Intent intent = new Intent(context, PhotoViewActivity.class);
        intent.putExtra(EXTRA_PHOTO_INDEX, position);
        intent.putExtra(EXTRA_PHOTO_DATAS, photoUrls);
        context.startActivity(intent);
        assert(context instanceof Activity);
        Activity activity = (Activity) context;
        activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);

        final Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_PHOTO_DATAS)) {
            mPhotoUrls = (ArrayList<String>) getIntent().getSerializableExtra(EXTRA_PHOTO_DATAS);
        }

        mCurrentPhotoIndex = getIntent().getIntExtra(EXTRA_PHOTO_INDEX, 0);

        mAdapter = new PhotoViewerPagerAdapter(getSupportFragmentManager());
        mAdapter.setData(mPhotoUrls);

        mViewPager = (PhotoViewPager) findViewById(R.id.photo_view_pager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setOnInterceptTouchListener(this);
        mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.photo_page_margin));
        mViewPager.setCurrentItem(mCurrentPhotoIndex);

        setTitle();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPhotoIndex = position;
        setTitle();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public PhotoViewPager.InterceptType onTouchIntercept(float origX, float origY) {
        boolean interceptLeft = false;
        boolean interceptRight = false;

        for (OnScreenListener listener : mScreenListeners.values()) {
            if (!interceptLeft) {
                interceptLeft = listener.onInterceptMoveLeft(origX, origY);
            }
            if (!interceptRight) {
                interceptRight = listener.onInterceptMoveRight(origX, origY);
            }
        }

        if (interceptLeft) {
            if (interceptRight) {
                return PhotoViewPager.InterceptType.BOTH;
            }
            return PhotoViewPager.InterceptType.LEFT;
        } else if (interceptRight) {
            return PhotoViewPager.InterceptType.RIGHT;
        }
        return PhotoViewPager.InterceptType.NONE;
    }

    @Override
    public void addScreenListener(int position, OnScreenListener listener) {
        mScreenListeners.put(position, listener);
    }

    @Override
    public void removeScreenListener(int position) {
        mScreenListeners.remove(position);
    }

    @Override
    public boolean isFragmentActive(Fragment fragment) {
        if (mViewPager == null || mAdapter == null) {
            return false;
        }
        return mViewPager.getCurrentItem() == mAdapter.getItemPosition(fragment);
    }

    private void setTitle() {
        //super.setTitle(String.format("%d / %d", mCurrentPhotoIndex + 1, mPhotoUrls.size()));
        mToolbar.setTitle(String.format("%d / %d", mCurrentPhotoIndex + 1, mPhotoUrls.size()));
    }
}
