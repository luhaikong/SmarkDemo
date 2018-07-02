package com.smack.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.smack.xmpp.XmppConnectionManager;

import java.util.List;

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
    public void onCreate() {
        startServiceRepeated(this);
        super.onCreate();
    }

    private void startServiceRepeated(Context context) {
        Intent intent = getSmackPushServiceIntent(context);
        AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent mPendingIntent = PendingIntent.getService(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        long now = System.currentTimeMillis();
        mAlarmManager.setInexactRepeating(AlarmManager.RTC, now, 20 * 1000, mPendingIntent);
    }

    private Intent getSmackPushServiceIntent(Context context) {
        Intent intent = new Intent(SmackPushService.class.getName());
        intent.setPackage(context.getPackageName());

        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentServices(intent, 0);
        if (resolveInfos != null) {
            for (ResolveInfo info : resolveInfos) {
                Intent i = new Intent();
                i.setClassName(info.serviceInfo.packageName, info.serviceInfo.name);
                return i;
            }
        }
        return null;
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

    public void addChatListener(SmackPushCallBack smackPushCallBack,String jid){
        XmppConnectionManager.newInstance().addChatRoomListener(smackPushCallBack,jid);
    }

}
