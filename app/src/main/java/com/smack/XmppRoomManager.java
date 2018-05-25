package com.smack;

import android.text.TextUtils;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MucEnterConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.jid.util.JidUtil;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.List;
import java.util.Set;

/**
 * Created by MyPC on 2018/5/25.
 */

public class XmppRoomManager {

    /**
     * 获取服务器上的所有群组
     */
    private List<HostedRoom> getHostedRoom(XMPPTCPConnection connection) {
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        try {
            //serviceNames->conference.106.14.20.176
            List<DomainBareJid> serviceNames = manager.getXMPPServiceDomains();
            for (int i = 0; i < serviceNames.size(); i++) {
                return manager.getHostedRooms(serviceNames.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加入一个群聊聊天室
     *
     * @param jid 聊天室ip 格式为>>群组名称@conference.ip
     * @param nickName 用户在聊天室中的昵称
     * @param password 聊天室密码 没有密码则传""
     * @return
     */
    public MultiUserChat join(XMPPTCPConnection connection, EntityBareJid jid, String nickName, String password) {
        try {
            Resourcepart resourcepart = Resourcepart.from(nickName);
            // 使用XMPPConnection创建一个MultiUserChat窗口
            MultiUserChat muc = MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(jid);
            // 聊天室服务将会决定要接受的历史记录数量
            MucEnterConfiguration configuration = muc.getEnterConfigurationBuilder(resourcepart)
                    .requestMaxCharsHistory(0)
                    .withPassword(password)
                    .timeoutAfter(connection.getPacketReplyTimeout())
                    .build();
            // 用户加入聊天室
            muc.join(configuration);
            return muc;
        } catch (XMPPException | XmppStringprepException | InterruptedException | SmackException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 监听这个群的所有会话消息
     * @param jid 格式为>>群组名称@conference.ip
     */
    private void initListener(XMPPTCPConnection connection, EntityBareJid jid) {
        MultiUserChat multiUserChat = MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(jid);
        multiUserChat.addMessageListener(new MessageListener() {
            @Override
            public void processMessage(final Message message) {
                //当消息返回为空的时候，表示用户正在聊天窗口编辑信息并未发出消息
                if (!TextUtils.isEmpty(message.getBody())) {
                    //收到的消息
                }
            }
        });
    }

    /**
     * 发送一条消息到jid这个群里
     * @param connection
     * @param jid
     */
    public void sendMessage(XMPPTCPConnection connection, EntityBareJid jid){
        MultiUserChat multiUserChat = MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(jid);
        try {
            multiUserChat.sendMessage("Hello World");
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建群聊聊天室
     *
     * @param jid 聊天室jid
     * @param nickName 创建者在聊天室中的昵称
     * @param password 聊天室密码
     * @return
     */
    public MultiUserChat createChatRoom(XMPPTCPConnection connection, EntityBareJid jid, String nickName, String password) {
        try {
            Resourcepart resourcepart = Resourcepart.from(nickName);
            // Get the MultiUserChatManager
            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
            // Create a MultiUserChat using an XMPPConnection for a room
            MultiUserChat muc = manager.getMultiUserChat(jid);
            // Prepare a list of owners of the new room
            Set<Jid> owners = JidUtil.jidSetFrom(new String[] { connection.getUser().asEntityBareJidString() });
            // Create the room
            muc.create(resourcepart)
                    .getConfigFormManager()
                    .setRoomOwners(owners)
                    .setAndEnablePassword(password)
                    .submitConfigurationForm();
            return muc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
