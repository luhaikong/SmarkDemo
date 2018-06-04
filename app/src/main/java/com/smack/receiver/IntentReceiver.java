package com.smack.receiver;

/**
 *
 * @author MyPC
 * @date 2018/6/4
 */

public class IntentReceiver {
    /**
     * 用户注册SDK的intent
     */
    public final static String REGISTRATION = "cn.xpush.android.intent.REGISTRATION";

    /**
     * 用户接收SDK消息的intent
     */
    public final static String MESSAGE_RECEIVED = "cn.xpush.android.intent.MESSAGE_RECEIVED";

    /**
     * 用户接收SDK通知栏信息的intent
     */
    public final static String NOTIFICATION_RECEIVED = "cn.xpush.android.intent.NOTIFICATION_RECEIVED";

    /**
     * 用户打开自定义通知栏的intent
     */
    public final static String NOTIFICATION_OPENED = "cn.xpush.android.intent.NOTIFICATION_OPENED";

    /**
     * 接收网络变化 连接/断开
     */
    public final static String CONNECTION = "cn.xpush.android.intent.CONNECTION";

    /**
     * 接收网络变化 连接/断开
     */
    public final static String AUTHENTICATED = "cn.xpush.android.intent.AUTHENTICATED";
}
