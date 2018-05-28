package com.smack;

import android.text.TextUtils;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.MucEnterConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.jid.util.JidUtil;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
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
     * 发送一条私聊消息给某个群里的某个人
     * @param connection
     * @param mucjid
     * @param userjid
     */
    public void sendMessageToUserOfRoom(XMPPTCPConnection connection, EntityBareJid mucjid, EntityBareJid userjid){
        // TODO
    }

    /**
     * 创建群聊聊天室
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

    /**
     * 加入一个群聊聊天室
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
     * 管理会议室邀请,邀请其他用户（otherJid）加入聊天室（mucJid）
     * @param connection
     * @param mucJid
     * @param otherJid
     * @param nickName
     * @param password
     * @return
     */
    public MultiUserChat invitations(XMPPTCPConnection connection, EntityBareJid mucJid, EntityBareJid otherJid, String nickName, String password){
        try {
            Resourcepart resourcepart = Resourcepart.from(nickName);
            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
            MultiUserChat muc2 = manager.getMultiUserChat(mucJid);
            muc2.join(resourcepart);
            // 监听房间邀请或拒绝邀请
            muc2.addInvitationRejectionListener(new InvitationRejectionListener() {
                @Override
                public void invitationDeclined(EntityBareJid invitee, String reason, Message message, MUCUser.Decline rejection) {

                }
            });
            // 邀请otherJid用户加入聊天室
            muc2.invite(otherJid, "Meet me in this excellent room");
            return muc2;
        } catch (XmppStringprepException
                | SmackException.NoResponseException
                | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException
                | InterruptedException
                | MultiUserChatException.NotAMucServiceException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 改变聊天室主题
     * @param connection
     * @param mucJid
     * @return
     */
    public MultiUserChat changesOnRoomSubject(XMPPTCPConnection connection, EntityBareJid mucJid){
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        MultiUserChat muc2 = manager.getMultiUserChat(mucJid);
        muc2.addSubjectUpdatedListener(new SubjectUpdatedListener() {
            @Override
            public void subjectUpdated(String subject, EntityFullJid from) {
                // 监听聊天室的主题变更
            }
        });
        try {
            muc2.changeSubject("New Subject");
        } catch (SmackException.NoResponseException
                | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException
                | InterruptedException e) {
            e.printStackTrace();
        }
        return muc2;
    }

    /**
     * 查询用户otherJid是否支持群聊
     * @param connection
     * @param otherJid
     * @return
     */
    public boolean queryMucSupport(XMPPTCPConnection connection, EntityBareJid otherJid){
        boolean supports = false;
        try {
            supports = MultiUserChatManager.getInstanceFor(connection).isServiceEnabled(otherJid);
        } catch (SmackException.NoResponseException
                | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException
                | InterruptedException e) {
            e.printStackTrace();
        }
        return supports;
    }

    /**
     * 查询联系人userJid所在的房间
     * @param connection
     * @param userJid
     * @return
     */
    public List<EntityBareJid> queryMucRooms(XMPPTCPConnection connection, EntityBareJid userJid){
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        List<EntityBareJid> joinedRooms = new ArrayList<>();
        try {
             joinedRooms = manager.getJoinedRooms(userJid);
        } catch (SmackException.NoResponseException
                | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException
                | InterruptedException e) {
            e.printStackTrace();
        }
        return joinedRooms;
    }

    /**
     * 查询一个聊天室mucJid的信息
     * @param connection
     * @param mucJid
     * @return
     */
    public RoomInfo queryMucRoomInfo(XMPPTCPConnection connection, EntityBareJid mucJid){
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        RoomInfo info = null;
        try {
            info = manager.getRoomInfo(mucJid);
            System.out.println("Number of occupants:" + info.getOccupantsCount());
            System.out.println("Room Subject:" + info.getSubject());
        } catch (SmackException.NoResponseException
                | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException
                | InterruptedException e) {
            e.printStackTrace();
        }
        return info;
    }
}
