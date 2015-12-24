package org.rubychinaandroid;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.rubychinaandroid.utils.FavouriteUtils;

import java.io.File;

public class MyApplication extends Application {
    private String LOG_TAG = "MyApplication";
    private static MyApplication mContext;
    private DisplayImageOptions mImageLoaderOptions;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = (MyApplication) getApplicationContext();
        initImageLoader();
        updateFavRecord();
    }

    public static MyApplication getInstance() {
        return mContext;
    }

    public int getMemorySize() {
        final ActivityManager mgr = (ActivityManager) getApplicationContext().
                getSystemService(Activity.ACTIVITY_SERVICE);
        return mgr.getMemoryClass();
    }

    private void initImageLoader() {
        /* ImageLoader and options for avatar loading in getView() */
        mImageLoaderOptions = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.avatar_default)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(10))
                .build();

        File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "Pictures/rubychina");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(3)
                .memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .writeDebugLogs()
                .build();

        ImageLoader.getInstance().init(config);
    }

    public DisplayImageOptions getImageLoaderOptions() {
        return mImageLoaderOptions;
    }

    private void updateFavRecord() {
        FavouriteUtils.updateFavouriteRecord();
    }
}