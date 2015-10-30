package org.rubychinaandroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.rubychinaandroid.utils.RubyChinaCategory;

public class RubyChinaOpenHelper extends SQLiteOpenHelper {

    private static final String TOPIC_TABLE_NAME = "Topic";

    private static final String TOPIC_COLUMN_TITLE = "title";
    private static final String TOPIC_COLUMN_AUTHOR = "author";
    private static final String TOPIC_COLUMN_LOGIN = "user_login";
    private static final String TOPIC_COLUMN_AVATAR = "avatar_url";
    private static final String TOPIC_COLUMN_TIME = "create_time";
    private static final String TOPIC_COLUMN_CATEGORY = "category";
    private static final String TOPIC_COLUMN_PAGE = "page";

    public static final String CREATE_TOPIC_TABLE = "create table if not exists " + TOPIC_TABLE_NAME +
            "(" + "id integer primary key autoincrement," +
            TOPIC_COLUMN_TITLE + " text," +
            TOPIC_COLUMN_AUTHOR + " text," +
            TOPIC_COLUMN_LOGIN + " text," +
            TOPIC_COLUMN_AVATAR + " text," +
            TOPIC_COLUMN_TIME + " text," +
            TOPIC_COLUMN_CATEGORY + " integer not null," +
            TOPIC_COLUMN_PAGE + " integer not null)";

    public RubyChinaOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                               int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TOPIC_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TOPIC_TABLE_NAME);
    }

    public void deletePage(SQLiteDatabase db, int page, RubyChinaCategory category) {
        String DELETE_PAGE = "delete from " + TOPIC_TABLE_NAME + " " +
                "where page=" + Integer.toString(page) + " and " +
                "category=" + Integer.toString(category.getValue());
        db.execSQL(DELETE_PAGE);
    }

    public void destroy(SQLiteDatabase db) {
        db.execSQL("drop table if exists " + TOPIC_TABLE_NAME);
    }
}
