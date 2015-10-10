package org.rubychinaandroid.utils;

public class RubyChinaCategory {

    public RubyChinaCategory(int value) {
        this.value = value;
    }

    private final String[] EXPRESSIONS = {"excellent", "no_reply", "recent", "last_actived", "popular"};
    private int value = 0;

    public int getValue() {
        return this.value;
    }

    public String getExpr() {
        return EXPRESSIONS[this.value];
    }

    public void assign(int v) {
        value = v;
    }
}
