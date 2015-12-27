package org.rubychinaandroid.db;


import android.provider.BaseColumns;

public class Contract {
    public Contract() {}
    public static abstract class Entry implements BaseColumns {
        public static final String TABLE_NAME = "Topic";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_LOGIN = "user_login";
        public static final String COLUMN_NAME_AVATAR = "avatar_url";
        public static final String COLUMN_NAME_TIME = "create_time";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_PAGE = "page";
    }
}
