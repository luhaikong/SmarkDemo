package com.smack.service;

import com.smack.xmpp.XmppUserConfig;

import org.jivesoftware.smack.ConnectionListener;

/**
 *
 * @author MyPC
 * @date 2018/6/5
 */

public interface SmackPushCallBack extends ConnectionListener {

    /**
     * 注册账号是否成功
     * @param success
     * @param msg
     */
    public void registerAccount(boolean success,String msg);

    /**
     * 收到消息回调
     * @param content
     * @param createdLocally
     */
    public void chatCreated(String content, boolean createdLocally);

    /**
     * 退出登录回调
     * @param config
     */
    public void logout(XmppUserConfig config);
}
