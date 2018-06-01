package com.smack;

import android.app.Application;

import com.smack.utils.BadgeUtil;

/**
 * Created by MyPC on 2018/5/30.
 */

public class SmackApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BadgeUtil.setBadgeCount(getApplicationContext(),100,R.mipmap.ic_launcher);
    }
}
