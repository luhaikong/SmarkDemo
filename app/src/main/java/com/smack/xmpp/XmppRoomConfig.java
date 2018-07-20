package com.smack.xmpp;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/6/29.
 */

public class XmppRoomConfig implements Serializable {

    public final static String OBJ = "obj";

    public final static String FLAG_PERSISTENTROOM = "muc#roomconfig_persistentroom";// 设置聊天室是永久聊天室
    public final static String FLAG_MEMBERSONLY = "muc#roomconfig_membersonly";// 房间仅对成员开放
    public final static String FLAG_ALLOWINVITES = "muc#roomconfig_allowinvites";// 允许成员邀请其他人
    public final static String FLAG_WHOIS = "muc#roomconfig_whois";// 能够发现成员真实 JID 的角色(待选值：moderators:审核者、anyone:任何人)
    public final static String FLAG_ENABLELOGGING = "muc#roomconfig_enablelogging";// 记录房间对话
    public final static String FLAG_RESERVEDNICK = "x-muc#roomconfig_reservednick";// 仅允许注册的昵称登录
    public final static String FLAG_CANCHANGENICK = "x-muc#roomconfig_canchangenick";// 允许使用者修改昵称
    public final static String FLAG_REGISTRATION = "x-muc#roomconfig_registration";// 允许用户注册房间
    public final static String FLAG_ADMINS = "muc#roomconfig_roomadmins";// 房间管理员

    public final static String FLAG_OWNERS = "muc#roomconfig_roomowners";// 房间拥有者
    public final static String FLAG_ALLOWPM = "muc#roomconfig_allowpm";// Allowed to Send Private Messages(待选值：anyone:任何人、moderators:审核者、participants:参与者、none:无)
    public final static String FLAG_SECRET = "muc#roomconfig_roomsecret";// 密码
    public final static String FLAG_PWDPROTECTEDROOM = "muc#roomconfig_passwordprotectedroom";// 需要密码才能进入房间
    public final static String FLAG_MODERATED = "muc#roomconfig_moderatedroom";// 房间需要审核
    public final static String FLAG_PUBLIC = "muc#roomconfig_publicroom";// 在目录中列出房间
    public final static String FLAG_BROADCAST = "muc#roomconfig_presencebroadcast";// 广播其存在的角色(待选值：moderator:审核者、participant:参与者、visitor:访客)
    public final static String FLAG_MAXUSERS = "muc#roomconfig_maxusers";// 最大房间成员人数(待选值：10、20、30、40、50、无)
    public final static String FLAG_CHANGESUBJECT = "muc#roomconfig_changesubject";// 允许成员更改主题
    public final static String FLAG_DESC = "muc#roomconfig_roomdesc";// 房间描述
    public final static String FLAG_NAME = "muc#roomconfig_roomname";// 房间名称
    public final static String FLAG_FORM_TYPE = "FORM_TYPE";// 默认值："http://jabber.org/protocol/muc#roomconfig"

    private String roomJidPart;
    private String roomNick;
    private String roomPw;
    private String roomDesc;
    private boolean persistentroom;
    private boolean membersonly;
    private boolean allowinvites;
    private String whois;
    private boolean enablelogging;
    private boolean reservednick;
    private boolean canchangenick;
    private boolean registration;
    private boolean changesubject;

    public String getRoomJidPart() {
        return roomJidPart;
    }

    private void setRoomJidPart(String roomJidPart) {
        this.roomJidPart = roomJidPart;
    }

    public String getRoomNick() {
        return roomNick;
    }

    private void setRoomNick(String roomNick) {
        this.roomNick = roomNick;
    }

    public String getRoomPw() {
        return roomPw;
    }

    private void setRoomPw(String roomPw) {
        this.roomPw = roomPw;
    }

    public String getRoomDesc() {
        return roomDesc;
    }

    private void setRoomDesc(String roomDesc) {
        this.roomDesc = roomDesc;
    }

    public boolean isPersistentroom() {
        return persistentroom;
    }

    private void setPersistentroom(boolean persistentroom) {
        this.persistentroom = persistentroom;
    }

    public boolean isMembersonly() {
        return membersonly;
    }

    private void setMembersonly(boolean membersonly) {
        this.membersonly = membersonly;
    }

    public boolean isAllowinvites() {
        return allowinvites;
    }

    private void setAllowinvites(boolean allowinvites) {
        this.allowinvites = allowinvites;
    }

    public String getWhois() {
        return whois;
    }

    private void setWhois(String whois) {
        this.whois = whois;
    }

    public boolean isEnablelogging() {
        return enablelogging;
    }

    private void setEnablelogging(boolean enablelogging) {
        this.enablelogging = enablelogging;
    }

    public boolean isReservednick() {
        return reservednick;
    }

    private void setReservednick(boolean reservednick) {
        this.reservednick = reservednick;
    }

    public boolean isCanchangenick() {
        return canchangenick;
    }

    private void setCanchangenick(boolean canchangenick) {
        this.canchangenick = canchangenick;
    }

    public boolean isRegistration() {
        return registration;
    }

    private void setRegistration(boolean registration) {
        this.registration = registration;
    }

    public boolean isChangesubject() {
        return changesubject;
    }

    private void setChangesubject(boolean changesubject) {
        this.changesubject = changesubject;
    }

    public static class Builder{
        private String roomJidPart;
        private String roomNick;
        private String roomPw;
        private String roomDesc;
        private boolean persistentroom;
        private boolean membersonly;
        private boolean allowinvites;
        private String whois;
        private boolean enablelogging;
        private boolean reservednick;
        private boolean canchangenick;
        private boolean registration;
        private boolean changesubject;

        public Builder setRoomJidPart(String roomJidPart) {
            this.roomJidPart = roomJidPart;
            return this;
        }

        public Builder setRoomNick(String roomNick) {
            this.roomNick = roomNick;
            return this;
        }

        public Builder setRoomPw(String roomPw) {
            this.roomPw = roomPw;
            return this;
        }

        public Builder setRoomDesc(String roomDesc) {
            this.roomDesc = roomDesc;
            return this;
        }

        public Builder setPersistentroom(boolean persistentroom) {
            this.persistentroom = persistentroom;
            return this;
        }

        public Builder setMembersonly(boolean membersonly) {
            this.membersonly = membersonly;
            return this;
        }

        public Builder setAllowinvites(boolean allowinvites) {
            this.allowinvites = allowinvites;
            return this;
        }

        public Builder setWhois(String whois) {
            this.whois = whois;
            return this;
        }

        public Builder setEnablelogging(boolean enablelogging) {
            this.enablelogging = enablelogging;
            return this;
        }

        public Builder setReservednick(boolean reservednick) {
            this.reservednick = reservednick;
            return this;
        }

        public Builder setCanchangenick(boolean canchangenick) {
            this.canchangenick = canchangenick;
            return this;
        }

        public Builder setRegistration(boolean registration) {
            this.registration = registration;
            return this;
        }

        public Builder setChangesubject(boolean changesubject) {
            this.changesubject = changesubject;
            return this;
        }

        public XmppRoomConfig create(){
            XmppRoomConfig config = new XmppRoomConfig();
            config.setRoomJidPart(roomJidPart);
            config.setRoomNick(roomNick);
            config.setRoomPw(roomPw);
            config.setRoomDesc(roomDesc);
            config.setPersistentroom(persistentroom);
            config.setMembersonly(membersonly);
            config.setAllowinvites(allowinvites);
            config.setEnablelogging(enablelogging);
            config.setReservednick(reservednick);
            config.setCanchangenick(canchangenick);
            config.setRegistration(registration);
            config.setChangesubject(changesubject);
            return config;
        }
    }
}
