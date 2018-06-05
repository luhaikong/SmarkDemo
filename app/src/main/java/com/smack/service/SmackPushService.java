package com.smack.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.smack.xmpp.XmppConnectionManager;

/**
 *
 * @author MyPC
 * @date 2018/6/4
 */

public class SmackPushService extends Service {

    private SmackPushBinder smackPushBinder = new SmackPushBinder();

    public class SmackPushBinder extends Binder {

        public SmackPushService getService(){
            return SmackPushService.this;
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return smackPushBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void initConnectionAndLogin(SmackPushCallBack smackPushCallBack){
        XmppConnectionManager.newInstance().initConnectionAndLogin(smackPushCallBack,getApplicationContext());
    }

    public void addChatListener(SmackPushCallBack smackPushCallBack){
        XmppConnectionManager.newInstance().addChatListener(smackPushCallBack);
    }


}
