package com.smack.xmpp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.smack.receiver.IntentReceiver;
import com.smack.service.SmackPushCallBack;
import com.smack.xmppentity.GroupFriend;
import com.smack.xmppentity.RoomHosted;
import com.smack.xmppentity.RoomMucInfo;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * Created by MyPC on 2018/5/25.
 */

public class XmppConnectionManager {

    private Context ofContext;
    private XmppUserConfig ofXmppUserConfig;
    private XMPPTCPConnection connection;
    private boolean isLogoutNormal = false;//是否是正常退出
    private ScheduledExecutorService executorService;
    private SmackPushCallBack smackPushCallBack;

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

    public XmppUserConfig getOfXmppUserConfig() {
        return ofXmppUserConfig;
    }

    public void setOfXmppUserConfig(XmppUserConfig ofXmppUserConfig) {
        this.ofXmppUserConfig = ofXmppUserConfig;
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
        if (getConnectionAndInit() == null) {
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
     */
    public void login() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!connection.isAuthenticated()){
                        connection.login(ofXmppUserConfig.getOfUserName(), ofXmppUserConfig.getOfPassword());
                        // TODO 获取离线消息
                    } else {
                        smackPushCallBack.authenticated(connection,true);
                    }
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
        if (getConnectionAndInit() == null) {
            return;
        }
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    AccountManager manager = AccountManager.getInstance(connection);
                    manager.sensitiveOperationOverInsecureConnection(true);
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
     * @param smackPushCallBack
     * @see AccountManager
     */
    public void registerAccount(final SmackPushCallBack smackPushCallBack) {
        if (getConnectionAndInit() == null) {
            return;
        }
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                AccountManager manager = AccountManager.getInstance(connection);
                manager.sensitiveOperationOverInsecureConnection(true);
                try {
                    if (ofXmppUserConfig.getAttr() == null) {
                        manager.createAccount(ofXmppUserConfig.getOfUserName(), ofXmppUserConfig.getOfPassword());
                    } else {
                        manager.createAccount(ofXmppUserConfig.getOfUserName(), ofXmppUserConfig.getOfPassword(),ofXmppUserConfig.getAttr());
                    }
                    login();
                    if (smackPushCallBack!=null){
                        smackPushCallBack.registerAccount(true,"");
                    }
                    if (ofContext!=null){
                        Intent intent = new Intent(IntentReceiver.IntentEnum.REGISTRATION);
                        ofContext.sendBroadcast(intent);
                    }
                } catch (SmackException.NoResponseException
                        |XMPPException.XMPPErrorException
                        |SmackException.NotConnectedException e){
                    e.printStackTrace();
                    if (smackPushCallBack!=null){
                        smackPushCallBack.registerAccount(false,e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * 创建一个新用户,然后登录
     * @see AccountManager
     */
    private void registerAccount(){
        if (getConnectionAndInit() == null) {
            return;
        }
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                AccountManager manager = AccountManager.getInstance(connection);
                manager.sensitiveOperationOverInsecureConnection(true);
                try {
                    if (ofXmppUserConfig.getAttr() == null) {
                        manager.createAccount(ofXmppUserConfig.getOfUserName(), ofXmppUserConfig.getOfPassword());
                    } else {
                        manager.createAccount(ofXmppUserConfig.getOfUserName(), ofXmppUserConfig.getOfPassword(), ofXmppUserConfig.getAttr());
                    }
                    login();
                    if (smackPushCallBack!=null){
                        smackPushCallBack.registerAccount(true,"createAccount success！");
                    }
                    if (ofContext!=null){
                        Intent intent = new Intent(IntentReceiver.IntentEnum.REGISTRATION);
                        ofContext.sendBroadcast(intent);
                    }
                } catch (SmackException.NoResponseException
                        |XMPPException.XMPPErrorException
                        |SmackException.NotConnectedException e){
                    e.printStackTrace();
                    String message = "XMPPError: conflict - cancel";
                    if (e instanceof org.jivesoftware.smack.XMPPException && message.equals(e.getMessage())){
                        login();
                        return;
                    }
                    if (smackPushCallBack!=null){
                        smackPushCallBack.registerAccount(false,e.getMessage());
                    }
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
     * @param smackPushCallBack 子线程回调展示结果
     */
    public void initConnectionAndLogin(final SmackPushCallBack smackPushCallBack, final Context context) {
        this.ofContext = context;
        this.smackPushCallBack = smackPushCallBack;
        if (onImConnectionListener!=null){
            connection.removeConnectionListener(onImConnectionListener);
        }
        onImConnectionListener = new OnImConnectionListener();
        onImConnectionListener.setSmackPushCallBack(smackPushCallBack);
        onImConnectionListener.setImContext(context);
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
                            login();
                        } else {
                            registerAccount(smackPushCallBack);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addChatListener(SmackPushCallBack smackPushCallBack){
        if (getConnectionAndInit() == null) {
            return;
        }
        XmppMsgManager.newInstance().setSmackPushCallBack(smackPushCallBack);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                XmppMsgManager.newInstance().initListener(connection,ofContext);
            }
        });
    }

    public void addChatRoomListener(SmackPushCallBack smackPushCallBack, final String jid){
        if (getConnectionAndInit() == null) {
            return;
        }
        XmppRoomManager.newInstance().setSmackPushCallBack(smackPushCallBack);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                XmppRoomManager.newInstance().initListener(connection,jid,ofContext);
            }
        });
    }

    public void sendMessageSin(final OutGoMsgListener listener, final String toJid, final String content){
        if (getConnectionAndInit() == null) {
            return;
        }
        if (listener!=null){
            listener.onOutGoing();
        }
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                XmppMsgManager.newInstance().sendMessage(connection,toJid,listener,content);
            }
        });
    }

    public void sendMessageMuc(final OutGoMsgListener listener, final String toJid, final String content){
        if (getConnectionAndInit() == null) {
            return;
        }
        if (listener!=null){
            listener.onOutGoing();
        }
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                XmppRoomManager.newInstance().sendMessage(connection,listener,toJid,content);
            }
        });
    }

    public void getFriendList(final Handler handler){
        if (getConnectionAndInit() == null) {
            return;
        }
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                List<GroupFriend> list = XmppRosterManager.newInstance().getFriendList(connection,connection.getUser());
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

    public void getHostedRooms(final Handler handler){
        if (getConnectionAndInit() == null) {
            return;
        }
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                List<RoomHosted> list = XmppRoomManager.newInstance().getHostedRooms(connection);
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

    public void getHostedRooms2(final Handler handler, final String serviceName){
        if (getConnectionAndInit() == null) {
            return;
        }
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                List<RoomHosted> list = XmppRoomManager.newInstance().getHostedRooms2(connection,serviceName);
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

    public void getRoomInfo(final Handler handler, final String mucJid){
        if (getConnectionAndInit() == null) {
            return;
        }
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                RoomMucInfo roomMucInfo = XmppRoomManager.newInstance().queryMucRoomInfo(connection,mucJid);
                if (handler!=null){
                    Message message = new Message();
                    message.what = XmppConnectionFlag.KEY_FRIENDS_SUCCESS;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(XmppConnectionFlag.KEY_FRIENDS_SUCCESS_PARAMS, (Serializable) roomMucInfo);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        });
    }

    public void createChatRoom(final Handler handler, final String mucJid, final XmppRoomConfig config){
        if (getConnectionAndInit() == null) {
            return;
        }
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                MultiUserChat muc = XmppRoomManager.newInstance().createChatRoom(connection,mucJid,config);
                if (handler!=null){
                    Message message = new Message();
                    if (muc!=null){
                        message.what = XmppConnectionFlag.KEY_FRIENDS_SUCCESS;
                    } else {
                        message.what = XmppConnectionFlag.KEY_FRIENDS_FAIL;
                    }
                    handler.sendMessage(message);
                }
            }
        });
    }

    public void setChatRoom(final Handler handler, final String mucJid, final XmppRoomConfig config){
        if (getConnectionAndInit() == null) {
            return;
        }
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                MultiUserChat muc = XmppRoomManager.newInstance().setChatRoom(connection,mucJid,config);
                if (handler!=null){
                    Message message = new Message();
                    if (muc!=null){
                        message.what = XmppConnectionFlag.KEY_FRIENDS_SUCCESS;
                    } else {
                        message.what = XmppConnectionFlag.KEY_FRIENDS_FAIL;
                    }
                    handler.sendMessage(message);
                }
            }
        });
    }

    public void changeRoomSubject(final Handler handler, final String mucJid, final String subject){
        if (getConnectionAndInit() == null) {
            return;
        }
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                MultiUserChat muc = XmppRoomManager.newInstance().changeRoomSubject(connection,mucJid,subject);
                if (handler!=null){
                    Message message = new Message();
                    if (muc!=null){
                        message.what = XmppConnectionFlag.KEY_FRIENDS_SUCCESS;
                    } else {
                        message.what = XmppConnectionFlag.KEY_FRIENDS_FAIL;
                    }
                    handler.sendMessage(message);
                }
            }
        });
    }

    public void join(final Handler handler, final String jid, final String nickName, final String password){
        if (getConnectionAndInit() == null) {
            return;
        }
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                MultiUserChat muc = XmppRoomManager.newInstance().join(connection,jid,nickName,password);
                if (handler!=null){
                    Message message = new Message();
                    if (muc!=null){
                        message.what = XmppConnectionFlag.KEY_FRIENDS_SUCCESS;
                    } else {
                        message.what = XmppConnectionFlag.KEY_FRIENDS_FAIL;
                    }
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

    public XMPPTCPConnection getConnectionAndInit() {
        if (connection==null){
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
                                login();
                            } else {
                                registerAccount(smackPushCallBack);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        connection = null;
                    }
                }
            });
        }
        return connection;
    }

    private OnImConnectionListener onImConnectionListener;

    /**
     * ImConnectionListener类的子类，ConnectionListener接口的扩展类
     * 可注入Broadcast
     */
    public class OnImConnectionListener extends ImConnectionListener{
        SmackPushCallBack smackPushCallBack;
        Context imContext;

        public void setImContext(Context imContext) {
            this.imContext = imContext;
        }

        public void setSmackPushCallBack(SmackPushCallBack smackPushCallBack) {
            this.smackPushCallBack = smackPushCallBack;
        }

        public OnImConnectionListener() {
            super();
        }

        @Override
        public void connected(XMPPConnection connection) {
            super.connected(connection);
            if (smackPushCallBack!=null){
                smackPushCallBack.connected(connection);
            }
            if (imContext!=null){
                Intent intent = new Intent(IntentReceiver.IntentEnum.CONNECTION);
                imContext.sendBroadcast(intent);
            }
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            super.authenticated(connection, resumed);
            if (smackPushCallBack!=null){
                smackPushCallBack.authenticated(connection,resumed);
            }
            if (imContext!=null){
                Intent intent = new Intent(IntentReceiver.IntentEnum.AUTHENTICATED);
                imContext.sendBroadcast(intent);
            }
        }
    }

    /**
     * ConnectionListener接口的实现类,仅负责子线程中的操作
     */
    class ImConnectionListener implements ConnectionListener{

        /**
         * 通知连接已成功连接到远程端点（例如xmpp服务器）
         * 请注意，该连接可能还没有经过验证，因此可能只能进行有限的操作，如注册账户
         * @param connection
         */
        @Override
        public void connected(XMPPConnection connection) {
            registerAccount();
        }

        /**
         * 连接已通过验证
         * @param connection
         * @param resumed
         */
        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            XmppMsgManager.newInstance().setSmackPushCallBack(smackPushCallBack);
            XmppMsgManager.newInstance().initListener((XMPPTCPConnection) connection,ofContext);
        }

        /**
         * 连接正常关闭
         */
        @Override
        public void connectionClosed() {
            if (isLogoutNormal){
                logout(null);
            } else {
                registerAccount();
            }
        }

        /**
         * 连接异常关闭
         * @param e
         */
        @Override
        public void connectionClosedOnError(Exception e) {
            registerAccount();
        }

        /**
         * 连接已成功地重新连接到服务器。当上一个套接字连接突然关闭时，连接将重新连接到服务器。
         */
        @Override
        public void reconnectionSuccessful() {
        }

        /**
         * 连接将在指定的秒数中重试重新连接。
         * @param seconds
         */
        @Override
        public void reconnectingIn(int seconds) {
        }

        /**
         * 连接到服务器的尝试失败了。连接将继续尝试重新连接到服务器在一瞬间。
         * @param e
         */
        @Override
        public void reconnectionFailed(Exception e) {
        }
    }
}
