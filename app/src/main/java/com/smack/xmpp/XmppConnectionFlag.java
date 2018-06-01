package com.smack.xmpp;

/**
 * Created by MyPC on 2018/5/31.
 */

public class XmppConnectionFlag {

    //openfire服务器地址
    public final static String HOST = "10.180.120.11";
    public final static int PORT = 5222;
    //openfire服务器名称
    public final static String SERVICENAME = "rocketmq-03";

    //Xmpp连接状态码
    public final static int KEY_CONNECTED = 0;
    public final static int KEY_AUTHENTICATED = 1;
    public final static int KEY_CONNECTIONCLOSED = 2;
    public final static int KEY_CONNECTIONCLOSEDONERROR = 3;
    public final static int KEY_RECONNECTIONSUCCESSFUL = 4;
    public final static int KEY_RECONNECTINGIN = 5;
    public final static int KEY_RECONNECTIONFAILED = 6;

    //登出状态码
    public final static int KEY_LOGOUT_SUCCESS = 7;
    public final static int KEY_LOGOUT_FAIL = 8;

    //修改密码状态码
    public final static int KEY_CHANGEPASSWORD_SUCCESS = 9;
    public final static int KEY_CHANGEPASSWORD_FAIL = 10;

    //注册状态码
    public final static int KEY_REGISTER_SUCCESS = 11;
    public final static int KEY_REGISTER_FAIL = 12;

    //获取Roster状态码
    public final static int KEY_ROSTERENTRY_SUCCESS = 13;
    public final static int KEY_ROSTERENTRY_FAIL = 14;
    public final static int KEY_ROSTERGROUP_SUCCESS = 15;
    public final static int KEY_ROSTERGROUP_FAIL = 16;
    public final static int KEY_FRIENDS_SUCCESS = 17;
    public final static String KEY_FRIENDS_SUCCESS_PARAMS = "params";
    public final static int KEY_FRIENDS_FAIL = 18;

    public final static int KEY_CHATCREATED_SUCCESS = 19;
    public final static String KEY_CHATCREATED_SUCCESS_PARAMS = "params";

    public final static int KEY_SENDMESSAGESIN_SUCCESS = 20;
    public final static String KEY_SENDMESSAGESIN_SUCCESS_PARAMS = "params";
}
