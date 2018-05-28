package com.smack;

import android.util.Log;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by MyPC on 2018/5/25.
 */

public class XmppManager {

    //openfire服务器地址
    private static String host = "222.195.xxx.xxx";
    private static int port = 5222;
    //openfire服务器名称
    private static String serviceName = "openfireName";
    private XMPPTCPConnection connection;
    private boolean isLogoutNormal = false;//是否是正常退出

    private static class Holder {
        private static XmppManager singleton = new XmppManager();
    }

    public static XmppManager newInstance(){
        return Holder.singleton;
    }

    public XmppManager (){

    }

    /**
     * 获得所有联系人
     */
    public Roster getContact() {
        Roster roster = Roster.getInstanceFor(connection);
        //获得所有的联系人组
        Collection<RosterGroup> groups = roster.getGroups();
        for (RosterGroup group : groups) {
            //获得每个组下面的好友
            List<RosterEntry> entries = group.getEntries();
            for (RosterEntry entry : entries) {
                //获得好友基本信息
                entry.getJid();
                entry.getName();
                entry.getType();
            }
        }
        return roster;
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
                        presence.setTo(entry.getJid());
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
        } catch (SmackException.NotConnectedException|InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 一上线获取离线消息(注：登录成功后调用)
     * 设置登录状态为在线
     */
    public List<Message> getOffLineMessage() {
        OfflineMessageManager offlineManager = new OfflineMessageManager(connection);
        List<Message> list = null;
        try {
            list = offlineManager.getMessages();
            //删除离线消息
            offlineManager.deleteMessages();
            //将状态设置成在线
            Presence presence = new Presence(Presence.Type.available);
            connection.sendStanza(presence);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 登录
     * 登陆成功需要调用-获取离线消息
     * @param userName 用户名
     * @param password 密码
     */
    public void login(final String userName, final String password, ImConnectionListener listener) {
        try {
            connection.login(userName, password);
            connection.addConnectionListener(listener);
        } catch (XMPPException | SmackException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出登录
     *
     * @return code
     * @code true 退出成功
     * @code false 退出失败
     */
    public boolean logout() {
        try {
            connection.instantShutdown();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 修改密码
     *
     * @param newPassword 　新密码
     * @return code
     * @code true 修改成功
     * @code false 修改失败
     */
    public boolean changePassword(String newPassword) {
        try {
            AccountManager manager = AccountManager.getInstance(connection);
            manager.changePassword(newPassword);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建一个新用户
     *
     * @param username 用户名
     * @param password 密码
     * @param attr     一些用户资料
     * @see AccountManager
     */
    public boolean registerAccount(String username, String password, Map<String, String> attr) {
        AccountManager manager = AccountManager.getInstance(connection);
        Localpart localpart = null;
        try {
            localpart = Localpart.from(username);
            if (attr == null) {
                manager.createAccount(localpart, password);
            } else {
                manager.createAccount(localpart, password, attr);
            }
            return true;
        } catch (XmppStringprepException
                |SmackException.NoResponseException
                |XMPPException.XMPPErrorException
                |SmackException.NotConnectedException
                |InterruptedException e){
            e.printStackTrace();
            return false;
        }
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
                    .setHost(host)
                    //服务器端口
                    .setPort(port)
                    //服务器名称(管理界面的 主机名)
                    .setXmppDomain(serviceName)
                    //是否开启安全模式
                    .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
                    //是否开启压缩
                    .setCompressionEnabled(false)
                    //设置离线状态，以便获取离线消息
                    .setSendPresence(false)
                    //开启调试模式
                    .setDebuggerEnabled(true)
                    .build();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        return config;
    }

    /**
     * 获得与服务器的连接
     * @return
     */
    public XMPPTCPConnection getConnection() {
        try {
            if (connection == null) {
                connection = new XMPPTCPConnection(getConfiguration());
                connection.connect();
            }
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    private class ImConnectionListener implements ConnectionListener{

        private String ofUserName;
        private String ofPassword;

        public ImConnectionListener(String userName, String password) {
            this.ofUserName = userName;
            this.ofPassword = password;
        }

        /**
         * 通知连接已成功连接到远程端点（例如xmpp服务器）
         * 请注意，该连接可能还没有经过验证，因此可能只能进行有限的操作，如注册账户
         * @param connection
         */
        @Override
        public void connected(XMPPConnection connection) {

        }

        /**
         * 连接已通过验证
         * @param connection
         * @param resumed
         */
        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {

        }

        /**
         * 连接正常关闭
         */
        @Override
        public void connectionClosed() {
            if (isLogoutNormal){
                logout();
            } else {
                login(ofUserName,ofPassword,this);
            }
        }

        /**
         * 连接异常关闭
         * @param e
         */
        @Override
        public void connectionClosedOnError(Exception e) {

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
