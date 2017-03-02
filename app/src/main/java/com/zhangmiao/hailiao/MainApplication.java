package com.zhangmiao.hailiao;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

/*
 * Created by zhangmiao on 2017/2/23.
 */
public class MainApplication extends Application {

    public static Context applicationContext;
    private static MainApplication instance;

    @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();
        applicationContext = this;
        instance = this;

        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);

        EMClient.getInstance().init(applicationContext, options);
        EMClient.getInstance().setDebugMode(true);
    }

    public static MainApplication getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
