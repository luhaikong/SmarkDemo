package com.smack.xmpp;

import android.text.TextUtils;

import com.smack.xmppentity.RoomHosted;
import com.smack.xmppentity.RoomMucInfo;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *
 * @author MyPC
 * @date 2018/5/25
 */

public class XmppRoomManager {

    private List<RoomHosted> roomHosteds;

    private static class Holder {
        private static XmppRoomManager singleton = new XmppRoomManager();
    }

    public static XmppRoomManager newInstance(){
        return XmppRoomManager.Holder.singleton;
    }

    /**
     * 获取服务器上的所有群组
     */
    public List<RoomHosted> getHostedRooms(XMPPTCPConnection connection) {
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        try {
            List<HostedRoom> list = manager.getHostedRooms(connection.getServiceName());
            roomHosteds = new ArrayList<>();
            if (list!=null&&list.size()>0){
                for (HostedRoom room:list){
                    RoomHosted hosted = new RoomHosted();
                    hosted.setJid(room.getJid());
                    hosted.setName(room.getName());
                    roomHosteds.add(hosted);
                }
            }

            List<String> joinRooms = queryMucRooms(connection);
            if (joinRooms!=null&&joinRooms.size()>0){
                for (String room:joinRooms){
                    RoomHosted hosted = new RoomHosted();
                    hosted.setName(room);
                    roomHosteds.add(hosted);
                }
            }
            return roomHosteds;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取服务器上的所有群组
     */
    public List<RoomHosted> getHostedRooms2(XMPPTCPConnection connection, String serviceName) {
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        try {
            List<HostedRoom> list = manager.getHostedRooms(serviceName);
            roomHosteds = new ArrayList<>();
            if (list!=null&&list.size()>0){
                for (HostedRoom room:list){
                    RoomHosted hosted = new RoomHosted();
                    hosted.setJid(room.getJid());
                    hosted.setName(room.getName());
                    roomHosteds.add(hosted);
                }
            }

            List<String> joinRooms = queryMucRooms(connection);
            if (joinRooms!=null&&joinRooms.size()>0){
                for (String room:joinRooms){
                    RoomHosted hosted = new RoomHosted();
                    hosted.setName(room);
                    roomHosteds.add(hosted);
                }
            }
            return roomHosteds;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 监听这个群的所有会话消息
     * @param jid 格式为>>群组名称@conference.ip
     */
    private void initListener(XMPPTCPConnection connection, String jid) {
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
    public void sendMessage(XMPPTCPConnection connection, String jid){
        MultiUserChat multiUserChat = MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(jid);
        try {
            multiUserChat.sendMessage("Hello World");
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送一条私聊消息给某个群里的某个人
     * @param connection
     * @param mucjid
     * @param userjid
     */
    public void sendMessageToUserOfRoom(XMPPTCPConnection connection, String mucjid, String userjid){
        // TODO
    }

    /**
     * 创建群聊聊天室
     * @param jid 聊天室jid
     * @param nickName 创建者在聊天室中的昵称
     * @param password 聊天室密码
     * @return
     */
    public MultiUserChat createChatRoom(XMPPTCPConnection connection, String jid, String nickName, String password) {
        try {
            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
            MultiUserChat muc = manager.getMultiUserChat(jid);
            muc.create(nickName);
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
    public MultiUserChat join(XMPPTCPConnection connection, String jid, String nickName, String password) {
        try {
            // 使用XMPPConnection创建一个MultiUserChat窗口
            MultiUserChat muc = MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(jid);
            // 聊天室服务将会决定要接受的历史记录数量
            DiscussionHistory history = new DiscussionHistory();
            history.setMaxChars(0);
            history.setSince(new Date());
            // 用户加入聊天室
            muc.join(nickName, password, history,
                    SmackConfiguration.getDefaultPacketReplyTimeout());
            return muc;
        } catch (XMPPException | SmackException e) {
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
    public MultiUserChat invitations(XMPPTCPConnection connection, String mucJid, String otherJid, String nickName, String password){
        try {
            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
            MultiUserChat muc2 = manager.getMultiUserChat(mucJid);
            muc2.join(nickName);
            // 监听房间邀请或拒绝邀请
            muc2.addInvitationRejectionListener(new InvitationRejectionListener() {
                @Override
                public void invitationDeclined(String invitee, String reason) {

                }
            });
            // 邀请otherJid用户加入聊天室
            muc2.invite(otherJid, "Meet me in this excellent room");
            return muc2;
        } catch (SmackException.NoResponseException
                | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException e) {
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
    public MultiUserChat changesOnRoomSubject(XMPPTCPConnection connection, String mucJid){
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        MultiUserChat muc2 = manager.getMultiUserChat(mucJid);
        muc2.addSubjectUpdatedListener(new SubjectUpdatedListener() {
            @Override
            public void subjectUpdated(String subject, String from) {
                // 监听聊天室的主题变更
            }
        });
        try {
            muc2.changeSubject("New Subject");
        } catch (SmackException.NoResponseException
                | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException e) {
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
    public boolean queryMucSupport(XMPPTCPConnection connection, String otherJid){
        boolean supports = false;
        try {
            supports = MultiUserChatManager.getInstanceFor(connection).isServiceEnabled(otherJid);
        } catch (SmackException.NoResponseException
                | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return supports;
    }

    /**
     * 查询已加入的房间
     * @param connection
     * @return
     */
    public List<String> queryMucRooms(XMPPTCPConnection connection){
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        List<String> joinedRooms = new ArrayList<>();
        Set<String> joinedRoomSet = manager.getJoinedRooms();
        if (joinedRoomSet!=null&&joinedRoomSet.size()>0){
            for (String s:joinedRoomSet){
                joinedRooms.add(s);
            }
        }
        return joinedRooms;
    }

    /**
     * 查询一个聊天室mucJid的信息
     * @param connection
     * @param mucJid
     * @return
     */
    public RoomMucInfo queryMucRoomInfo(XMPPTCPConnection connection, String mucJid){
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        RoomMucInfo mucInfo = null;
        try {
            RoomInfo info = manager.getRoomInfo(mucJid);
            mucInfo = new RoomMucInfo();
            mucInfo.setName(info.getName());
            mucInfo.setContactJid(info.getContactJids());
            mucInfo.setDescription(info.getDescription());
            mucInfo.setLang(info.getLang());
            mucInfo.setLdapgroup(info.getLdapGroup());
            String logs = info.getLogsUrl()==null?"":info.getLogsUrl().toString();
            mucInfo.setLogs(logs);
            mucInfo.setMaxhistoryfetch(info.getMaxHistoryFetch());
            mucInfo.setMembersOnly(info.isMembersOnly());
            mucInfo.setModerated(info.isModerated());
            mucInfo.setNonanonymous(info.isNonanonymous());
            mucInfo.setOccupantsCount(info.getOccupantsCount());
            mucInfo.setPasswordProtected(info.isPasswordProtected());
            mucInfo.setPersistent(info.isPersistent());
            mucInfo.setPubsub(info.getPubSub());
            mucInfo.setRoom(info.getRoom());
            mucInfo.setSubject(info.getSubject());
            mucInfo.setSubjectmod(info.isSubjectModifiable());
        } catch (SmackException.NoResponseException
                | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return mucInfo;
    }
}
