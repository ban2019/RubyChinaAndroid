package org.rubychinaandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.rubychinaandroid.model.TopicModel;
import org.rubychinaandroid.utils.RubyChinaCategory;

import java.util.ArrayList;

public class RubyChinaDBManager {
    private final String LOG_TAG = "RubyChinaDBManager";
    private static volatile RubyChinaDBManager mInstance;
    private RubyChinaOpenHelper mDbHelper;
    private volatile SQLiteDatabase mDb;
    private Context mContext;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private String mSharedPreferenceFileName = "RubyChinaDBManager";
    private final String KEY_PAGE_SUM = "page_num";

    private RubyChinaDBManager(Context context) {
        mDbHelper = new RubyChinaOpenHelper(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDb = mDbHelper.getWritableDatabase();
            }
        }).start();
        mContext = context;
        mPref = mContext.getSharedPreferences(mSharedPreferenceFileName,
                Context.MODE_PRIVATE);
        mEditor = mContext.getSharedPreferences(mSharedPreferenceFileName,
                Context.MODE_PRIVATE).edit();
    }

    public static RubyChinaDBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (RubyChinaDBManager.class) {
                if (mInstance == null) {
                    mInstance = new RubyChinaDBManager(context);
                }
            }
        }

        return mInstance;
    }

    private void insert(TopicModel topic, RubyChinaCategory category, int page) {
        if (topic != null) {
            ContentValues values = new ContentValues();
            values.put(Contract.Entry.COLUMN_NAME_TITLE, topic.getTitle());
            values.put(Contract.Entry.COLUMN_NAME_AUTHOR, topic.getUserName());
            values.put(Contract.Entry.COLUMN_NAME_AVATAR, topic.getUserAvatarUrl());
            values.put(Contract.Entry.COLUMN_NAME_LOGIN, topic.getUserLogin());
            values.put(Contract.Entry.COLUMN_NAME_TIME, topic.getCreatedAt());
            values.put(Contract.Entry.COLUMN_NAME_CATEGORY, category.getValue());
            values.put(Contract.Entry.COLUMN_NAME_PAGE, page);
            mDb.insert(Contract.Entry.TABLE_NAME, null, values);
        }
    }

    public ArrayList<TopicModel> query(RubyChinaCategory category, int page) {
        String projection[] = {
                Contract.Entry.COLUMN_NAME_TITLE,
                Contract.Entry.COLUMN_NAME_AUTHOR,
                Contract.Entry.COLUMN_NAME_LOGIN,
                Contract.Entry.COLUMN_NAME_AVATAR,
                Contract.Entry.COLUMN_NAME_TIME,
                Contract.Entry.COLUMN_NAME_PAGE,
                Contract.Entry.COLUMN_NAME_CATEGORY
        };
        String sortOrder = Contract.Entry.COLUMN_NAME_TIME + " DESC";
        String selection = Contract.Entry.COLUMN_NAME_CATEGORY + "=?" + " and " +
                Contract.Entry.COLUMN_NAME_PAGE + "=?";
        String selectionArgs[] = {
                Integer.toString(category.getValue()),
                Integer.toString(page)
        };
        Cursor cursor = mDb.query(
                Contract.Entry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        ArrayList<TopicModel> list = new ArrayList<TopicModel>();
        if (cursor.moveToFirst()) {
            do {
                TopicModel topicModel = new TopicModel();
                topicModel.setTitle(cursor.getString(cursor.getColumnIndex(
                        Contract.Entry.COLUMN_NAME_TITLE)));
                topicModel.setUserName(cursor.getString(cursor.getColumnIndex(
                        Contract.Entry.COLUMN_NAME_AUTHOR)));
                topicModel.setUserLogin(cursor.getString(cursor.getColumnIndex(
                        Contract.Entry.COLUMN_NAME_LOGIN)));
                topicModel.setUserAvatarUrl(cursor.getString(cursor.getColumnIndex(
                        Contract.Entry.COLUMN_NAME_AVATAR)));
                topicModel.setCreatedAt(cursor.getString(cursor.getColumnIndex(
                        Contract.Entry.COLUMN_NAME_TIME)));
                list.add(topicModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    private int mPageSum = 0;

    public boolean isNoMoreEntries(RubyChinaCategory category, int page) {
        mPageSum = pageSum(category);
        return page >= mPageSum;
    }

    // Different TopicsFragment's mCachedPages instance is saved in separate files.
    private int pageSum(RubyChinaCategory category) {
        return mPref.getInt(category.getValue() + KEY_PAGE_SUM, 0);
    }

    private void putEntrySum(RubyChinaCategory category, int pages) {
        mEditor.putInt(category.getValue() + KEY_PAGE_SUM, pages);
        mEditor.commit();
    }

    public void saveTopics(ArrayList<TopicModel> topics,
                           RubyChinaCategory category, int page) {
        deleteEntries(page, category);
        for (TopicModel topic : topics) {
            insert(topic, category, page);
        }

        mPageSum = pageSum(category);
        if (page >= mPageSum) {
            mPageSum = page + 1;
            putEntrySum(category, mPageSum);
        }
    }

    public void clearTopics(RubyChinaCategory category) {
        mPageSum = pageSum(category);
        for (int i = 0; i <= mPageSum; i++) {
            deleteEntries(i, category);
        }
        mPageSum = 0;
        putEntrySum(category, mPageSum);
    }

    private void deleteEntries(int page, RubyChinaCategory category) {
        String selection = Contract.Entry.COLUMN_NAME_PAGE + "=?" + " and " +
                Contract.Entry.COLUMN_NAME_CATEGORY + "=?";
        String[] selectionArgs = {Integer.toString(page), Integer.toString(category.getValue())};
        mDb.delete(Contract.Entry.TABLE_NAME, selection, selectionArgs);
    }
}
