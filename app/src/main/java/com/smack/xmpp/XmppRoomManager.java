package com.smack.xmpp;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.smack.receiver.IntentReceiver;
import com.smack.service.SmackPushCallBack;
import com.smack.xmppentity.RoomHosted;
import com.smack.xmppentity.RoomMember;
import com.smack.xmppentity.RoomMucInfo;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 *
 * @author MyPC
 * @date 2018/5/25
 */

public class XmppRoomManager {

    private List<RoomHosted> roomHosteds;
    private Map<String,MessageListener> messageListeners = new HashMap<>();

    private static class Holder {
        private static XmppRoomManager singleton = new XmppRoomManager();
    }

    public static XmppRoomManager newInstance(){
        return XmppRoomManager.Holder.singleton;
    }

    private SmackPushCallBack smackPushCallBack;

    public void setSmackPushCallBack(SmackPushCallBack smackPushCallBack) {
        this.smackPushCallBack = smackPushCallBack;
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
    public void initListener(XMPPTCPConnection connection, final String jid, final Context context) {
        if (messageListeners.get(jid)==null){
            MultiUserChat multiUserChat = MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(jid);
            MessageListener listener = new MessageListener() {
                @Override
                public void processMessage(Message message) {
                    //当消息返回为空的时候，表示用户正在聊天窗口编辑信息并未发出消息
                    if (message.getBody()!=null) {
                        if (smackPushCallBack!=null){
                            XmppConnectionManager manager = XmppConnectionManager.newInstance();
                            String uNickName = manager.getOfXmppUserConfig().getAttr().get("name");
                            if (message.getFrom().equals(jid.concat("/").concat(uNickName))){
                                smackPushCallBack.processMessage(message.getBody(),true);
                            } else {
                                smackPushCallBack.processMessage(message.getBody(),false);
                            }
                        }
                        if (context!=null){
                            Intent intent = new Intent();
                            intent.setAction(IntentReceiver.IntentEnum.NOTIFICATION_RECEIVED);
                            intent.putExtra(IntentReceiver.EXTRA,message.getBody());
                            context.sendBroadcast(intent);
                        }
                    }
                }
            };
            messageListeners.put(jid,listener);
            multiUserChat.addMessageListener(listener);
        }
    }

    /**
     * 发送一条消息到jid这个群里
     * @param connection
     * @param jid
     */
    public void sendMessage(XMPPTCPConnection connection, OutGoMsgListener listener, String jid, String content){
        try {
            MultiUserChat multiUserChat = MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(jid);
            Message newMessage = new Message();
            newMessage.setBody(content);
            multiUserChat.sendMessage(newMessage);
            if (listener!=null){
                listener.onOutGoSuccess(content);
            }
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            if (listener!=null){
                listener.onOutGoFail(content);
            }
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
     *
     * @param connection
     * @param jid
     * @param config
     * @return
     */
    public MultiUserChat createChatRoom(XMPPTCPConnection connection, String jid, XmppRoomConfig config) {
        try {
            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
            MultiUserChat muc = manager.getMultiUserChat(config.getRoomJidPart().concat("@").concat(jid));
            muc.create(config.getRoomNick());
            return muc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 设置聊天室的属性
     * @param connection
     * @param jid
     * @param config
     * @return
     */
    public MultiUserChat setChatRoom(XMPPTCPConnection connection, String jid, XmppRoomConfig config){
        try {
            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
            MultiUserChat muc = manager.getMultiUserChat(config.getRoomJidPart().concat("@").concat(jid));
            Form form = muc.getConfigurationForm();
            Form answerForm = form.createAnswerForm();
            for(FormField field:form.getFields()){
                if (!FormField.Type.hidden.name().equals(field.getType())&&field.getVariable()!=null){
                    answerForm.setDefaultAnswer(field.getVariable());
                }
            }
            answerForm.setAnswer(XmppRoomConfig.FLAG_NAME,config.getRoomNick());
            answerForm.setAnswer(XmppRoomConfig.FLAG_DESC,config.getRoomDesc());
            answerForm.setAnswer(XmppRoomConfig.FLAG_PERSISTENTROOM,config.isPersistentroom());
            answerForm.setAnswer(XmppRoomConfig.FLAG_MEMBERSONLY,config.isMembersonly());
            answerForm.setAnswer(XmppRoomConfig.FLAG_ALLOWINVITES,config.isAllowinvites());
            //answerForm.setAnswer(XmppRoomConfig.FLAG_WHOIS,"anyone");
            answerForm.setAnswer(XmppRoomConfig.FLAG_ENABLELOGGING,config.isEnablelogging());
            answerForm.setAnswer(XmppRoomConfig.FLAG_RESERVEDNICK,config.isReservednick());
            answerForm.setAnswer(XmppRoomConfig.FLAG_CANCHANGENICK,config.isCanchangenick());
            answerForm.setAnswer(XmppRoomConfig.FLAG_REGISTRATION,config.isRegistration());
            answerForm.setAnswer(XmppRoomConfig.FLAG_CHANGESUBJECT,config.isChangesubject());
            muc.sendConfigurationForm(answerForm);
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
     * @param nickNameMySelf
     * @param password
     * @return
     */
    public MultiUserChat invitations(XMPPTCPConnection connection, String mucJid, String otherJid, String nickNameMySelf, String password){
        try {
            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
            MultiUserChat muc2 = manager.getMultiUserChat(mucJid);
            muc2.grantMembership(otherJid);//赋予群成员的权限
            // 监听房间邀请或拒绝邀请
            muc2.addInvitationRejectionListener(new InvitationRejectionListener() {
                @Override
                public void invitationDeclined(String invitee, String reason) {
                    String inv = invitee;
                    String res = reason;
                    Log.d("TAG",inv.concat(reason));
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

    public List<RoomMember> getMembers(XMPPTCPConnection connection, String mucJid){
        List<RoomMember> list = new ArrayList<>();
        try {
            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
            MultiUserChat muc2 = manager.getMultiUserChat(mucJid);
            List<Affiliate> tempOwner = new ArrayList<>();
            tempOwner = muc2.getOwners();
            for (Affiliate affiliate:tempOwner){
                RoomMember member = new RoomMember();
                member.setJid(affiliate.getJid());
                member.setNick(affiliate.getNick()==null?affiliate.getJid().split("@")[0]:affiliate.getNick());
                member.setMucRole(affiliate.getRole()==null?"":affiliate.getRole().name());
                member.setMucAffiliation(affiliate.getAffiliation()==null?"":affiliate.getAffiliation().name());
                list.add(member);
            }
            List<Affiliate> tempAdmin = new ArrayList<>();
            tempAdmin = muc2.getAdmins();
            for (Affiliate affiliate:tempAdmin){
                RoomMember member = new RoomMember();
                member.setJid(affiliate.getJid());
                member.setNick(affiliate.getNick()==null?affiliate.getJid().split("@")[0]:affiliate.getNick());
                member.setMucRole(affiliate.getRole()==null?"":affiliate.getRole().name());
                member.setMucAffiliation(affiliate.getAffiliation()==null?"":affiliate.getAffiliation().name());
                list.add(member);
            }
            List<Affiliate> tempMember = new ArrayList<>();
            tempMember = muc2.getMembers();
            for (Affiliate affiliate:tempMember){
                RoomMember member = new RoomMember();
                member.setJid(affiliate.getJid());
                member.setNick(affiliate.getNick()==null?affiliate.getJid().split("@")[0]:affiliate.getNick());
                member.setMucRole(affiliate.getRole()==null?"":affiliate.getRole().name());
                member.setMucAffiliation(affiliate.getAffiliation()==null?"":affiliate.getAffiliation().name());
                list.add(member);
            }
        } catch (SmackException.NoResponseException
                | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 改变聊天室主题
     * @param connection
     * @param mucJid
     * @return
     */
    public MultiUserChat changeRoomSubject(XMPPTCPConnection connection, String mucJid, String subject){
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        MultiUserChat muc2 = manager.getMultiUserChat(mucJid);
        muc2.addSubjectUpdatedListener(new SubjectUpdatedListener() {
            @Override
            public void subjectUpdated(String subject, String from) {
                if (smackPushCallBack!=null){
                    smackPushCallBack.subjectUpdated(subject, from);
                }
            }
        });
        try {
            muc2.changeSubject(subject);
            return muc2;
        } catch (SmackException.NoResponseException
                | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException e) {
            e.printStackTrace();
            return null;
        }
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
            manager.getMultiUserChat(mucJid).getMembers();
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
