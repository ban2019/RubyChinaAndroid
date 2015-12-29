package org.rubychinaandroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RubyChinaOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ruby_china_android.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String NOT_NULL = " not null";
    private static final String COMMA_SEP = ",";

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + Contract.Entry.TABLE_NAME + " (" +
                    Contract.Entry._ID + " INTEGER PRIMARY KEY," +
                    Contract.Entry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    Contract.Entry.COLUMN_NAME_AUTHOR + TEXT_TYPE + COMMA_SEP +
                    Contract.Entry.COLUMN_NAME_LOGIN + TEXT_TYPE + COMMA_SEP +
                    Contract.Entry.COLUMN_NAME_AVATAR + TEXT_TYPE + COMMA_SEP +
                    Contract.Entry.COLUMN_NAME_TIME + TEXT_TYPE + COMMA_SEP +
                    Contract.Entry.COLUMN_NAME_CATEGORY + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    Contract.Entry.COLUMN_NAME_PAGE + INTEGER_TYPE + NOT_NULL +
                    ")";
    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + Contract.Entry.TABLE_NAME;


    public RubyChinaOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }
}
