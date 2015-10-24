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
    public final String DB_NAME = "ruby_china_android";
    public final int VERSION = 1;
    private static RubyChinaDBManager mRubyChinaDBManager;
    private SQLiteDatabase db;
    private RubyChinaOpenHelper dbHelper;
    private Context mContext;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private String mSharedPreferenceFileName;
    private final String KEY_TOTAL_PAGES = "total_pages";

    private RubyChinaDBManager(Context context) {
        dbHelper = new RubyChinaOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
        mContext = context;
        mSharedPreferenceFileName = "RubyChinaDBManager";
        mPref = mContext.getSharedPreferences(mSharedPreferenceFileName, Context.MODE_PRIVATE);
        mEditor = mContext.getSharedPreferences(mSharedPreferenceFileName, Context.MODE_PRIVATE).edit();
    }

    public synchronized static RubyChinaDBManager getInstance(Context context) {
        if (mRubyChinaDBManager == null) {
            mRubyChinaDBManager = new RubyChinaDBManager(context);
        }

        return mRubyChinaDBManager;
    }

    private void saveTopic(TopicModel topic, RubyChinaCategory category, int page) {
        if (topic != null) {
            ContentValues values = new ContentValues();
            values.put("title", topic.getTitle());
            values.put("author", topic.getUserName());
            values.put("avatar_url", topic.getUserAvatarUrl());
            values.put("user_login", topic.getUserLogin());
            values.put("create_time", topic.getCreatedAt());
            values.put("category", category.getValue());
            values.put("page", page);
            db.insert("Topic", null, values);
        }
    }

    public ArrayList<TopicModel> loadTopics(RubyChinaCategory category, int page) {
        ArrayList<TopicModel> list = new ArrayList<TopicModel>();
        Cursor cursor = db.query("Topic", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(cursor.getColumnIndex("category")) == category.getValue()
                        && cursor.getInt(cursor.getColumnIndex("page")) == page) {
                    TopicModel topicModel = new TopicModel();
                    topicModel.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    topicModel.setUserName(cursor.getString(cursor.getColumnIndex("author")));
                    topicModel.setUserLogin(cursor.getString(cursor.getColumnIndex("user_login")));
                    topicModel.setUserAvatarUrl(cursor.getString(cursor.getColumnIndex("avatar_url")));
                    topicModel.setCreatedAt(cursor.getString(cursor.getColumnIndex("create_time")));

                    list.add(topicModel);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return list;
    }

    private int mTotalPages = 0;
    public boolean isAllTopicsLoaded(RubyChinaCategory category, int page) {
        mTotalPages = getTotalPages(category);
        return page >= mTotalPages;
    }

    // Different TopicsFragment's mCachedPages instance is saved in separate files.
    private int getTotalPages(RubyChinaCategory category) {
        return mPref.getInt(category.getValue() + KEY_TOTAL_PAGES, 0);
    }
    private void commitTotalPages(RubyChinaCategory category, int pages) {
        mEditor.putInt(category.getValue() + KEY_TOTAL_PAGES, pages);
        mEditor.commit();
    }

    public void clearAllTopicsByCategory(RubyChinaCategory category) {
        mTotalPages = getInstance(mContext).getTotalPages(category);
        for (int i = 0; i <= mTotalPages; i++) {
            dbHelper.deletePage(db, i, category);
        }
        mTotalPages = 0;
        getInstance(mContext).commitTotalPages(category, mTotalPages);
    }

    public void saveTopics(ArrayList<TopicModel> topics,
                           RubyChinaCategory category, int page) {
        dbHelper.deletePage(db, page, category);
        for (TopicModel topic : topics) {
            getInstance(mContext).saveTopic(topic, category, page);
        }

        mTotalPages = getInstance(mContext).getTotalPages(category);
        if (page >= mTotalPages) {
            mTotalPages = page + 1; // mTotalPages is 1-based, mIndexPage is 0-based.
            getInstance(mContext).commitTotalPages(category, mTotalPages);
        }
    }
}
