package org.rubychinaandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.rubychinaandroid.model.TopicModel;
import org.rubychinaandroid.utils.RubyChinaCategory;
import org.rubychinaandroid.utils.RubyChinaTypes;

import java.util.ArrayList;
import java.util.List;


public class RubyChinaDBManager {
    private final String LOG_TAG = "RubyChinaDBManager";
    public final String DB_NAME = "ruby_china_android";
    public final int VERSION = 1;
    private static RubyChinaDBManager rubyChinaDBManager;
    private SQLiteDatabase db;
    RubyChinaOpenHelper dbHelper;

    private RubyChinaDBManager(Context context) {
        dbHelper = new RubyChinaOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static RubyChinaDBManager getInstance(Context context) {
        if (rubyChinaDBManager == null) {
            rubyChinaDBManager = new RubyChinaDBManager(context);
        }
        return rubyChinaDBManager;
    }

    public void saveTopic(TopicModel topic, RubyChinaCategory category, int page) {
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

    public List<TopicModel> loadTopics(RubyChinaCategory category, int page) {
        List<TopicModel> list = new ArrayList<TopicModel>();
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

    public void removeOnePageTopics(int page, int category) {
        dbHelper.deletePage(db, page, category);
    }
}
