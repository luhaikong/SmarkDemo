package com.smack;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.jiveproperties.JivePropertiesManager;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

/**
 * Created by MyPC on 2018/5/25.
 */

public class XmppMsgManager {

    /**
     * 初始化聊天消息监听
     */
    public void initListener(XMPPTCPConnection connection) {
        ChatManager manager = ChatManager.getInstanceFor(connection);
        IncomingChatMessageListener incomingChatMessageListener = new IncomingChatMessageListener() {
            @Override
            public void newIncomingMessage(EntityBareJid from, Message message, org.jivesoftware.smack.chat2.Chat chat) {
                System.out.println(message.getBody());
            }
        };
        OutgoingChatMessageListener outgoingChatMessageListener = new OutgoingChatMessageListener() {
            @Override
            public void newOutgoingMessage(EntityBareJid to, Message message, org.jivesoftware.smack.chat2.Chat chat) {
                System.out.println(message.getBody());
            }
        };
        manager.addIncomingListener(incomingChatMessageListener);
        manager.addOutgoingListener(outgoingChatMessageListener);
    }

    /**
     * 向jid用户发送消息
     * @param connection
     * @param jid  好友jid
     */
    public void sendMessage(XMPPTCPConnection connection,BareJid jid){
        try {
            ChatManager chatManager =  ChatManager.getInstanceFor(connection);
            chatManager.addIncomingListener(new IncomingChatMessageListener() {
                @Override
                public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                    System.out.println("New message from " + from + ": " + message.getBody());
                    // Send back the same text the other user sent us.
                    try {
                        chat.send(message.getBody());
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            EntityBareJid entityBareJid = JidCreate.entityBareFrom(jid);
            Chat chat = chatManager.chatWith(entityBareJid);
            Message newMessage = new Message();
            newMessage.setBody("Howdy!");
            // Additional modifications to the message Stanza.
            JivePropertiesManager.addProperty(newMessage, "favoriteColor", "red");
            chat.send(newMessage);
        } catch (SmackException.NotConnectedException|XmppStringprepException|InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 添加好友
     * @param connection
     * @param user  好友jid
     * @param name  好友昵称
     * @param groups  好友分组
     */
    public void addFriend(XMPPTCPConnection connection, BareJid user, String name, String[] groups){
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
        } catch (InterruptedException e) {
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
            public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException {
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
