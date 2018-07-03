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
import android.util.Log;

import com.smack.ChatActivity;
import com.smack.xmpp.XmppConnectionManager;
import com.smack.xmpp.XmppUserConfig;

import org.jivesoftware.smack.XMPPConnection;

import java.util.List;

/**
 *
 * @author MyPC
 * @date 2018/6/4
 */

public class SmackPushService extends Service implements SmackPushCallBack {

    public final static String TAG = SmackPushService.class.getSimpleName();
    private SmackPushBinder smackPushBinder = new SmackPushBinder();

    public class SmackPushBinder extends Binder {

        public SmackPushService getService(){
            return SmackPushService.this;
        }

    }

    private SmackPushCallBack mSmackPushCallBack;

    public void setSmackPushCallBack(SmackPushCallBack smackPushCallBack) {
        this.mSmackPushCallBack = smackPushCallBack;
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
        XmppConnectionManager.newInstance().addChatListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void registerAccount(boolean success, String msg) {
        Log.d(TAG,"----------------registerAccount注册账号回调---------------");
        if (mSmackPushCallBack!=null){
            mSmackPushCallBack.registerAccount(success, msg);
        }
    }

    @Override
    public void chatCreated(String content, boolean createdLocally) {
        Log.d(TAG,"----------------chatCreated接收消息回调---------------");
        if (mSmackPushCallBack!=null){
            mSmackPushCallBack.chatCreated(content, createdLocally);
        }
    }

    @Override
    public void logout(XmppUserConfig config) {
        Log.d(TAG,"----------------logout登出回调---------------");
        if (mSmackPushCallBack!=null){
            mSmackPushCallBack.logout(config);
        }
    }

    @Override
    public void processMessage(String content, boolean createdLocally) {
        Log.d(TAG,"----------------processMessage接受群消息回调---------------");
        if (mSmackPushCallBack!=null){
            mSmackPushCallBack.processMessage(content, createdLocally);
        }
    }

    @Override
    public void subjectUpdated(String subject, String from) {
        Log.d(TAG,"----------------subjectUpdated修改群主题回调---------------");
        if (mSmackPushCallBack!=null){
            subjectUpdated(subject, from);
        }
    }

    @Override
    public void connected(XMPPConnection connection) {
        Log.d(TAG,"----------------connected连接XMPP回调---------------");
        if (mSmackPushCallBack!=null){
            mSmackPushCallBack.connected(connection);
        }
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        Log.d(TAG,"----------------authenticated登录认证XMPP回调---------------");
        if (mSmackPushCallBack!=null){
            mSmackPushCallBack.authenticated(connection, resumed);
        }
    }

    @Override
    public void connectionClosed() {
        Log.d(TAG,"----------------connectionClosed连接关闭回调---------------");
        if (mSmackPushCallBack!=null){
            mSmackPushCallBack.connectionClosed();
        }
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.d(TAG,"----------------connectionClosedOnError连接异常关闭回调---------------");
        if (mSmackPushCallBack!=null){
            mSmackPushCallBack.connectionClosedOnError(e);
        }
    }

    @Override
    public void reconnectionSuccessful() {
        Log.d(TAG,"----------------reconnectionSuccessful断线重连成功回调---------------");
        if (mSmackPushCallBack!=null){
            mSmackPushCallBack.reconnectionSuccessful();
        }
    }

    @Override
    public void reconnectingIn(int seconds) {
        Log.d(TAG,"----------------reconnectingIn连接将在指定的秒数中重试重新连接回调---------------");
        if (mSmackPushCallBack!=null){
            mSmackPushCallBack.reconnectingIn(seconds);
        }
    }

    @Override
    public void reconnectionFailed(Exception e) {
        Log.d(TAG,"----------------reconnectionFailed连接到服务器的尝试失败了，连接将在一瞬间继续尝试重新连接到服务器---------------");
        if (mSmackPushCallBack!=null){
            mSmackPushCallBack.reconnectionFailed(e);
        }
    }

    public void initConnectionAndLogin(){
        XmppConnectionManager.newInstance().initConnectionAndLogin(this,getApplicationContext());
    }

    public void addChatListener(String jid){
        XmppConnectionManager.newInstance().addChatRoomListener(this,jid);
    }

}
