package com.smack.xmpp;

import android.content.Context;
import android.content.Intent;

import com.smack.receiver.IntentReceiver;
import com.smack.service.SmackPushCallBack;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.jiveproperties.JivePropertiesManager;
import org.jivesoftware.smackx.offline.OfflineMessageManager;

import java.util.List;
import java.util.Set;

/**
 *
 * @author MyPC
 * @date 2018/5/25
 */

public class XmppMsgManager {

    private static class Holder {
        private static XmppMsgManager singleton = new XmppMsgManager();
    }

    public static XmppMsgManager newInstance(){
        return XmppMsgManager.Holder.singleton;
    }


    private SmackPushCallBack smackPushCallBack;

    public void setSmackPushCallBack(SmackPushCallBack smackPushCallBack) {
        this.smackPushCallBack = smackPushCallBack;
    }

    /**
     * 一上线获取离线消息(注：登录成功后调用)
     * 设置登录状态为在线
     */
    public List<Message> getOffLineMessage(XMPPTCPConnection connection) {
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
     * 初始化聊天消息监听
     */
    public void initListener(final XMPPTCPConnection connection, final Context context) {
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        if (chatManager.getChatListeners()!=null){
            Set<ChatManagerListener> set = chatManager.getChatListeners();
            for (ChatManagerListener aSet : set) {
                chatManager.removeChatListener(aSet);
            }
        }
        chatManager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, final boolean createdLocally) {
                chat.addMessageListener(new ChatMessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        if (message.getBody()!=null){
                            if (smackPushCallBack!=null){
                                smackPushCallBack.chatCreated(message.getBody(),createdLocally);
                            }
                            if (context!=null){
                                Intent intent = new Intent();
                                intent.setAction(IntentReceiver.IntentEnum.NOTIFICATION_RECEIVED);
                                intent.putExtra(IntentReceiver.EXTRA,message.getBody());
                                context.sendBroadcast(intent);
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * 向jid用户发送消息
     * @param connection
     * @param jid  好友jid
     */
    public void sendMessage(XMPPTCPConnection connection,String jid,OutGoMsgListener listener,String content){
        try {
            ChatManager chatManager =  ChatManager.getInstanceFor(connection);
            Chat chat = chatManager.createChat(jid);
            Message newMessage = new Message();
            newMessage.setBody(content);
            JivePropertiesManager.addProperty(newMessage, "favoriteColor", "red");
            chat.sendMessage(newMessage);
            if (listener!=null){
                listener.onOutGoSuccess(content);
            }
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            if (listener!=null){
                listener.onOutGoFail(e.getMessage());
            }
        }
    }


    /**
     * 添加好友
     * @param connection
     * @param user  好友jid
     * @param name  好友昵称
     * @param groups  好友分组
     */
    public void addFriend(XMPPTCPConnection connection, String user, String name, String[] groups){
        try {
            Roster.getInstanceFor(connection).createEntry(user, name, groups);
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加好友请求信息监听
     */
    public void addFriendListener(XMPPTCPConnection connection) {
        //条件过滤
        StanzaFilter filter = new AndFilter();
        StanzaListener listener = new StanzaListener() {
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                DiscoverInfo p = (DiscoverInfo) packet;
                //p中可以得到对方的信息
                if (p.getType().toString().equals("subscrib")) {
                    //好友申请
                } else if (p.getType().toString().equals("subscribed")) {
                    //通过了好友请求
                } else if (p.getType().toString().equals("unsubscribe")) {
                    //拒绝好友请求
                }
            }
        };
        connection.addAsyncStanzaListener(listener, filter);
    }
}
