package com.smack.service;

import android.content.ServiceConnection;

import com.smack.xmpp.XmppUserConfig;

/**
 *
 * @author MyPC
 * @date 2018/6/5
 */

public interface SmackPushCallBack extends ServiceConnection {
    public void connected();

    public void registerAccount(boolean success,String msg);

    public void authenticated();

    public void chatCreated(String content, boolean createdLocally);

    public void logout(XmppUserConfig config);
}