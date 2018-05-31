package com.smack.xmpp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.smack.xmppwrap.xmppentity.ItemFriend;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * Created by MyPC on 2018/5/25.
 */

public class XmppConnectionManager {

    private XMPPTCPConnection connection;
    private boolean isLogoutNormal = false;//是否是正常退出

    private String ofUserName, ofPassword;

    private ScheduledExecutorService executorService;

    private static class Holder {
        private static XmppConnectionManager singleton = new XmppConnectionManager();
    }

    public static XmppConnectionManager newInstance(){
        return Holder.singleton;
    }

    public XmppConnectionManager(){
        executorService = new ScheduledThreadPoolExecutor(3, new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                Thread thread = new Thread(r, "Smack Executor Service");
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    private void sendHandlerMsg(int what, Handler conHandler){
        if (conHandler!=null){
            Message conMessage = new Message();
            conMessage.what = what;
            conHandler.sendMessage(conMessage);
        }
    }

    /**
     * 更改用户状态
     */
    public void setPresence(int code) {
        if (connection == null) {
            return;
        }
        Presence presence;
        try {
            switch (code) {
                case 0:
                    presence = new Presence(Presence.Type.available);
                    connection.sendStanza(presence);
                    Log.v("state", "设置在线");
                    break;
                case 1:
                    presence = new Presence(Presence.Type.available);
                    presence.setMode(Presence.Mode.chat);
                    connection.sendStanza(presence);
                    Log.v("state", "设置Q我吧");
                    System.out.println(presence.toXML());
                    break;
                case 2:
                    presence = new Presence(Presence.Type.available);
                    presence.setMode(Presence.Mode.dnd);
                    connection.sendStanza(presence);
                    Log.v("state", "设置忙碌");
                    System.out.println(presence.toXML());
                    break;
                case 3:
                    presence = new Presence(Presence.Type.available);
                    presence.setMode(Presence.Mode.away);
                    connection.sendStanza(presence);
                    Log.v("state", "设置离开");
                    System.out.println(presence.toXML());
                    break;
                case 4:
                    Roster roster = Roster.getInstanceFor(connection);
                    Collection<RosterEntry> entries = roster.getEntries();
                    for (RosterEntry entry : entries) {
                        presence = new Presence(Presence.Type.unavailable);
                        presence.setFrom(connection.getUser());
                        presence.setTo(entry.getUser());
                        connection.sendStanza(presence);
                        System.out.println(presence.toXML());
                    }
                    // 向同一用户的其他客户端发送隐身状态
                    presence = new Presence(Presence.Type.unavailable);
                    presence.setFrom(connection.getUser());
                    presence.setTo(connection.getUser());
                    connection.sendStanza(presence);
                    Log.v("state", "设置隐身");
                    break;
                case 5:
                    presence = new Presence(Presence.Type.unavailable);
                    connection.sendStanza(presence);
                    Log.v("state", "设置离线");
                    break;
                default:
                    break;
            }
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录
     * 登陆成功需要调用-获取离线消息
     * @param userName 用户名
     * @param password 密码
     */
    public void login(final String userName, final String password) {
        ofUserName = userName;
        ofPassword = password;
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    connection.login(userName, password);
                    // TODO 获取离线消息
                } catch (XMPPException | SmackException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 退出登录
     *
     * @return code
     * @code true 退出成功
     * @code false 退出失败
     */
    public void logout(final Handler handler) {
        isLogoutNormal = true;
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    connection.instantShutdown();
                    sendHandlerMsg(XmppConnectionFlag.KEY_LOGOUT_SUCCESS,handler);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendHandlerMsg(XmppConnectionFlag.KEY_LOGOUT_FAIL,handler);
                }
            }
        });
    }

    /**
     * 修改密码
     *
     * @param newPassword 　新密码
     * @return code
     * @code true 修改成功
     * @code false 修改失败
     */
    public void changePassword(final String newPassword, final Handler handler) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    AccountManager manager = AccountManager.getInstance(connection);
                    manager.changePassword(newPassword);
                    sendHandlerMsg(XmppConnectionFlag.KEY_CHANGEPASSWORD_SUCCESS,handler);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendHandlerMsg(XmppConnectionFlag.KEY_CHANGEPASSWORD_FAIL,handler);
                }
            }
        });
    }

    /**
     * 创建一个新用户
     *
     * @param username 用户名
     * @param password 密码
     * @param attr     一些用户资料
     * @see AccountManager
     */
    public void registerAccount(final String username, final String password, final Map<String, String> attr, final Handler handler) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                AccountManager manager = AccountManager.getInstance(connection);
                try {
                    if (attr == null) {
                        manager.createAccount(username, password);
                    } else {
                        manager.createAccount(username, password, attr);
                    }
                    sendHandlerMsg(XmppConnectionFlag.KEY_REGISTER_SUCCESS,handler);
                } catch (SmackException.NoResponseException
                        |XMPPException.XMPPErrorException
                        |SmackException.NotConnectedException e){
                    e.printStackTrace();
                    sendHandlerMsg(XmppConnectionFlag.KEY_REGISTER_FAIL,handler);
                }
            }
        });
    }

    /**
     * 获取XMPP连接配置
     * @return
     */
    private XMPPTCPConnectionConfiguration getConfiguration(){
        XMPPTCPConnectionConfiguration config = null;
        try {
            config = XMPPTCPConnectionConfiguration.builder()
                    //服务器IP地址
                    .setHost(XmppConnectionFlag.HOST)
                    //服务器端口
                    .setPort(XmppConnectionFlag.PORT)
                    //服务器名称(管理界面的 主机名)
                    .setServiceName(XmppConnectionFlag.SERVICENAME)
                    //是否开启安全模式
                    .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
                    //是否开启压缩
                    .setCompressionEnabled(false)
                    //设置离线状态，以便获取离线消息
                    .setSendPresence(true)
                    //开启调试模式
                    .setDebuggerEnabled(true)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return config;
    }

    /**
     * 初始化与服务器的连接
     * @param handler UI层回调展示结果
     */
    public void initConnection(Handler handler) {
        onImConnectionListener = new OnImConnectionListener();
        onImConnectionListener.setImHander(handler);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (connection == null) {
                        connection = new XMPPTCPConnection(getConfiguration());
                        connection.addConnectionListener(onImConnectionListener);
                        connection.connect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void initConnectionAndLogin(final Handler handler, String userName, String password) {
        this.ofUserName = userName;
        this.ofPassword = password;
        onImConnectionListener = new OnImConnectionListener();
        onImConnectionListener.setImHander(handler);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (connection == null) {
                        connection = new XMPPTCPConnection(getConfiguration());
                        connection.addConnectionListener(onImConnectionListener);
                        connection.connect();
                    } else {
                        if (!connection.isAuthenticated()){
                            login(ofUserName,ofPassword);
                        } else {
                            Message message = new Message();
                            message.what = XmppConnectionFlag.KEY_AUTHENTICATED;
                            handler.sendMessage(message);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getFriendList(final Handler handler){
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                List<ItemFriend> list = XmppRosterManager.newInstance().getFriendList(connection);
                if (handler!=null){
                    Message message = new Message();
                    message.what = XmppConnectionFlag.KEY_FRIENDS_SUCCESS;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(XmppConnectionFlag.KEY_FRIENDS_SUCCESS_PARAMS, (Serializable) list);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        });
    }

    public void getRosterGroupList(final Handler handler){
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Collection<RosterGroup> list = XmppRosterManager.newInstance().getRosterGroupList(connection);
                if (handler!=null){
                    Message message = new Message();
                    message.what = XmppConnectionFlag.KEY_ROSTERGROUP_SUCCESS;
                    handler.sendMessage(message);
                }
            }
        });
    }

    public void getUnfiledEntries(final Handler handler){
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Set<RosterEntry> list = XmppRosterManager.newInstance().getUnfiledEntries(connection);
                if (handler!=null){
                    Message message = new Message();
                    message.what = XmppConnectionFlag.KEY_ROSTERENTRY_SUCCESS;
                    handler.sendMessage(message);
                }
            }
        });
    }

    public XMPPTCPConnection getConnection() {
        if (connection==null){
            throw new RuntimeException("Xmpp连接还未初始化！！！");
        }
        return connection;
    }

    private OnImConnectionListener onImConnectionListener;

    /**
     * ImConnectionListener类的子类，ConnectionListener接口的扩展类
     */
    public class OnImConnectionListener extends ImConnectionListener{
        Handler imHander;
        Message imMessage;

        public void setImHander(Handler imHander) {
            this.imHander = imHander;
        }

        public OnImConnectionListener() {
            super();
        }

        @Override
        public void connected(XMPPConnection connection) {
            super.connected(connection);
            if (imHander!=null){
                imMessage = new Message();
                imMessage.what = XmppConnectionFlag.KEY_CONNECTED;
                imHander.sendMessage(imMessage);
            }
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            super.authenticated(connection, resumed);
            if (imHander!=null){
                imMessage = new Message();
                imMessage.what = XmppConnectionFlag.KEY_AUTHENTICATED;
                imHander.sendMessage(imMessage);
            }
        }
    }

    /**
     * ConnectionListener接口的实现类
     */
    class ImConnectionListener implements ConnectionListener{

        /**
         * 通知连接已成功连接到远程端点（例如xmpp服务器）
         * 请注意，该连接可能还没有经过验证，因此可能只能进行有限的操作，如注册账户
         * @param connection
         */
        @Override
        public void connected(XMPPConnection connection) {
            Log.d("ImConnectionListener","connected");
            login(ofUserName,ofPassword);
        }

        /**
         * 连接已通过验证
         * @param connection
         * @param resumed
         */
        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            Log.d("ImConnectionListener","authenticated");
            XmppMsgManager.newInstance().initListener((XMPPTCPConnection) connection);
        }

        /**
         * 连接正常关闭
         */
        @Override
        public void connectionClosed() {
            if (isLogoutNormal){
                logout(null);
            } else {
                login(ofUserName,ofPassword);
            }
        }

        /**
         * 连接异常关闭
         * @param e
         */
        @Override
        public void connectionClosedOnError(Exception e) {
            login(ofUserName,ofPassword);
        }

        /**
         * 连接已成功地重新连接到服务器。当上一个套接字连接突然关闭时，连接将重新连接到服务器。
         */
        @Override
        public void reconnectionSuccessful() {
            Log.d("ImConnectionListener","reconnectionSuccessful");
        }

        /**
         * 连接将在指定的秒数中重试重新连接。
         * @param seconds
         */
        @Override
        public void reconnectingIn(int seconds) {
            Log.d("ImConnectionListener","reconnectingIn");
        }

        /**
         * 连接到服务器的尝试失败了。连接将继续尝试重新连接到服务器在一瞬间。
         * @param e
         */
        @Override
        public void reconnectionFailed(Exception e) {
            Log.d("ImConnectionListener","reconnectionFailed");
        }
    }
}
