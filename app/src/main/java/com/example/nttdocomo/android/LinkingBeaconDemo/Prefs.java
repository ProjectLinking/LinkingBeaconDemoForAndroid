package com.example.nttdocomo.android.LinkingBeaconDemo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/** SharedPreferences　　 Save/Load **/
public class Prefs {
    private static final String PREF_KEY = "my_preferences";
    private static String KEY_BLT_ACCURACY = "blt_accuracy";

    /*
    * KEY_BLT_ACCURACY　セーブ
    * @param context：コンテキスト
    * @param data：
    * */
    public static boolean saveBltAccuracy(Context context, int data) {
        SharedPreferences pref = context.getSharedPreferences(PREF_KEY, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(KEY_BLT_ACCURACY, data);
        return editor.commit();
    }

    /*
    * KEY_BLT_ACCURACY　ロード
    * @param context：コンテキスト
    * */
    public static int loadBltAccuracy(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_KEY, Activity.MODE_PRIVATE);
        // 通常モードがデフォルトなので、それに合わせてデフォルト値を1にする
        return pref.getInt(KEY_BLT_ACCURACY, 1);
    }
}
