package org.rubychinaandroid.utils;

import java.lang.reflect.Array;
import java.util.Arrays;

public class RubyChinaTypes {

    public enum TOPIC_CATEGORY {

        excellent(0), no_reply(1), recent(2), last_active(3), popular(4);

        final String[] EXPRESSIONS = {"excellent", "no_reply", "recent", "last_actived", "popular"};

        private int value = 0;

        private TOPIC_CATEGORY(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public String getExpr() {
            return EXPRESSIONS[this.value];
        }

        public static TOPIC_CATEGORY valueOf(int value) {
            switch (value) {
                case 0:
                    return excellent;
                case 1:
                    return no_reply;
                case 2:
                    return recent;
                case 3:
                    return last_active;
                case 4:
                    return popular;
                default:
                    return excellent;
            }
        }

    }
}
