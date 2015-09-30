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

import java.io.File;

public class MyApplication extends Application {

    public static MyApplication gAppContext;
    private String LOG_TAG = "MyApplication";

    /* ImageLoader and options for avatar loading in getView() */
    public static DisplayImageOptions imageLoaderOptions = new DisplayImageOptions.Builder()
            .showImageOnFail(R.drawable.avatar_doge_mm)
            .imageScaleType(ImageScaleType.EXACTLY)
            .cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new RoundedBitmapDisplayer(10))
            .build();

    @Override
    public void onCreate() {
        super.onCreate();

        gAppContext = (MyApplication) getApplicationContext();

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

    public static MyApplication getInstance() {
        return gAppContext;
    }

    public int getMemorySize() {
        final ActivityManager mgr = (ActivityManager) getApplicationContext().
                getSystemService(Activity.ACTIVITY_SERVICE);
        return mgr.getMemoryClass();
    }
}
