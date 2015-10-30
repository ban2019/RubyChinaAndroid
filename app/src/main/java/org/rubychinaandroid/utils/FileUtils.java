package org.rubychinaandroid.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.nostra13.universalimageloader.utils.StorageUtils;

import org.rubychinaandroid.MyApplication;
import org.rubychinaandroid.db.RubyChinaDBManager;

import java.io.File;
import java.util.ArrayList;

public class FileUtils {
    private final static String TAG = "FileUtils";
    private static final int RATIO = 1024;
    public static String getCacheSize() {
        File appDir = StorageUtils.getOwnCacheDirectory(MyApplication.getInstance(), "Pictures/rubychina");
        long bytes = folderSize(appDir);
        double kb = bytes / RATIO;
        if ((int)kb == 0) {
            return bytes + "B";
        }

        double mb = kb / RATIO;
        if ((int)mb == 0) {
            return kb + "KB";
        }

        double gb = mb / RATIO;
        if ((int)gb == 0) {
            return mb + "MB";
        }
        return gb + "GB";
    }

    public static long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }

    public static void clearApplicationData() {
        File appDir = StorageUtils.getOwnCacheDirectory(MyApplication.getInstance(), "Pictures/rubychina");
        DeleteRecursive(appDir);
    }

    private static void DeleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles())
                DeleteRecursive(child);

        }

        Log.d(fileOrDirectory.getAbsolutePath(), "file is deleted");
        fileOrDirectory.delete();
    }
}
