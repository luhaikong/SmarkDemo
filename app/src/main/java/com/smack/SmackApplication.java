package com.smack;

import android.app.Application;
import android.content.Intent;

import com.smack.service.SmackPushService;

/**
 *
 * @author MyPC
 * @date 2018/5/30
 */

public class SmackApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initSmackPushService();
    }

    private void initSmackPushService(){
        Intent intent = new Intent(getApplicationContext(), SmackPushService.class);
        startService(intent);
    }
}
