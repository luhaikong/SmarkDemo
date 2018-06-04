package com.smack.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.smack.xmpp.XmppConnectionManager;

/**
 *
 * @author MyPC
 * @date 2018/6/4
 */

public class SmackPushService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!=null){
            XmppConnectionManager.newInstance().initConnection(getApplicationContext());
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
