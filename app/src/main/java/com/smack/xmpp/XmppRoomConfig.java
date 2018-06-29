package com.smack.xmpp;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/6/29.
 */

public class XmppRoomConfig implements Serializable {

    public final static String OBJ = "obj";

    public final static String FLAG_PERSISTENTROOM = "muc#roomconfig_persistentroom";// 设置聊天室是永久聊天室
    public final static String FLAG_MEMBERSONLY = "muc#roomconfig_membersonly";// 房间仅对成员开放
    public final static String FLAG_ALLOWINVITES = "muc#roomconfig_allowinvites";// 允许占有者邀请其他人
    public final static String FLAG_WHOIS = "muc#roomconfig_whois";// 能够发现占有者真实 JID 的角色
    public final static String FLAG_ENABLELOGGING = "muc#roomconfig_enablelogging";// 记录房间对话
    public final static String FLAG_RESERVEDNICK = "x-muc#roomconfig_reservednick";// 仅允许注册的昵称登录
    public final static String FLAG_CANCHANGENICK = "x-muc#roomconfig_canchangenick";// 允许使用者修改昵称
    public final static String FLAG_REGISTRATION = "x-muc#roomconfig_registration";// 允许用户注册房间

    private String roomNick;
    private String roomPw;
    private boolean persistentroom;
    private boolean membersonly;
    private boolean allowinvites;
    private String whois;
    private boolean enablelogging;
    private boolean reservednick;
    private boolean canchangenick;
    private boolean registration;

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

    public static class Builder{
        private String roomNick;
        private String roomPw;
        private boolean persistentroom;
        private boolean membersonly;
        private boolean allowinvites;
        private String whois;
        private boolean enablelogging;
        private boolean reservednick;
        private boolean canchangenick;
        private boolean registration;

        public Builder setRoomNick(String roomNick) {
            this.roomNick = roomNick;
            return this;
        }

        public Builder setRoomPw(String roomPw) {
            this.roomPw = roomPw;
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

        public XmppRoomConfig create(){
            XmppRoomConfig config = new XmppRoomConfig();
            config.setRoomNick(roomNick);
            config.setRoomPw(roomPw);
            config.setPersistentroom(persistentroom);
            config.setMembersonly(membersonly);
            config.setAllowinvites(allowinvites);
            config.setEnablelogging(enablelogging);
            config.setReservednick(reservednick);
            config.setCanchangenick(canchangenick);
            config.setRegistration(registration);
            return config;
        }
    }
}
