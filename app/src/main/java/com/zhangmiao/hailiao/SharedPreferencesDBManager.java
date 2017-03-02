package com.zhangmiao.hailiao;

import android.content.Context;
import android.content.SharedPreferences;

/*
 * Created by zhangmiao on 2017/2/24.
 */
public class SharedPreferencesDBManager {
    private SharedPreferences mSharedPreferences;
    public final static String PREFRENCE_FILE_KEY =
            "com.zhangmiao.hailiao.PREFRENCE_FILE_KEY";

    public SharedPreferencesDBManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(
                PREFRENCE_FILE_KEY, Context.MODE_PRIVATE
        );
    }


    public void writeData(User user) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("username", user.getUserName());
        editor.putString("password", user.getPassword());
        editor.apply();
    }

    public User readData() {
        String userName = mSharedPreferences.getString("username", "");
        String password = mSharedPreferences.getString("password", "");
        return new User(userName, password);
    }

}
