package org.rubychinaandroid.utils;

import android.util.Log;

public class RubyChinaCategory {
    private final String TAG = "RubyChinaCategory";
    private final int OFFSET = 5;
    private int MAX_CATEGORIES = 4;

    private int EXCELLENT = -5;
    private int NO_REPLY = -4;
    private int RECENT = -3;
    private int LAST_ACTIVE = -2;
    private int POPULAR = -1;

    public RubyChinaCategory(int value) {
        if (value >= 0 && value < MAX_CATEGORIES) {
            this.value = value - OFFSET;
        } else if (value >= EXCELLENT && value <= POPULAR) {
            this.value = value;
        } else {
            Log.d(TAG, "Unknown category");
            assert(false);
        }
    }

    private final String[] EXPRESSIONS = {"excellent", "no_reply", "recent", "last_actived", "popular"};
    private int value = 0;

    public int getValue() {
        return this.value;
    }

    public String getExpr() {
        return EXPRESSIONS[this.value + OFFSET];
    }
}
