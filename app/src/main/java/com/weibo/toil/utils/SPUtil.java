package com.weibo.toil.utils;

import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/6/4.
 */
public class SPUtil {

    private static SharedPreferences getShardPreferences() {
        return MainApplication.sp;
    }

    public static void putInt(String key, int value) {
        SharedPreferences.Editor editor = getShardPreferences().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getInt(String key) {
        int value = getShardPreferences().getInt(key, 0);
        return value;
    }

    public static void putLong(String key, long value) {
        SharedPreferences.Editor editor = getShardPreferences().edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static long getLong(String key) {
        long value = getShardPreferences().getLong(key, 0);
        return value;
    }

}
