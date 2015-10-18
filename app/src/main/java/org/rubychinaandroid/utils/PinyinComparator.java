package org.rubychinaandroid.utils;


import net.sourceforge.pinyin4j.PinyinHelper;

import org.rubychinaandroid.model.NodeModel;

import java.util.Comparator;

/**
 * Created by yw on 2015/4/22.
 */
public class PinyinComparator implements Comparator<NodeModel> {
    public int compare(NodeModel obj1, NodeModel obj2) {
        String ostr1 = obj1.getName();
        String ostr2 = obj2.getName();
        for (int i = 0; i < ostr1.length() && i < ostr2.length(); i++) {
            int codePoint1 = ostr1.charAt(i);
            int codePoint2 = ostr2.charAt(i);
            if (Character.isSupplementaryCodePoint(codePoint1)
                    || Character.isSupplementaryCodePoint(codePoint2)) {
                i++;
            }
            if (codePoint1 != codePoint2) {
                if (Character.isSupplementaryCodePoint(codePoint1)
                        || Character.isSupplementaryCodePoint(codePoint2)) {
                    return codePoint1 - codePoint2;
                }
                String pinyin1 = pinyin((char) codePoint1);
                String pinyin2 = pinyin((char) codePoint2);

                if (pinyin1 != null && pinyin2 != null) { // 两个字符都是汉字
                    if (!pinyin1.equals(pinyin2)) {
                        return pinyin1.compareTo(pinyin2);
                    }
                } else {
                    return codePoint1 - codePoint2;
                }
            }
        }
        return ostr1.length() - ostr2.length();
    }

    // 获得汉字拼音的首字符
    private String pinyin(char c) {
        String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c);
        if (pinyins == null) {
            return null;
        }
        return pinyins[0];
    }
}
