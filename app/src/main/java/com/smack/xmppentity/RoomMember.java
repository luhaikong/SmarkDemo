package com.smack.xmppentity;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/7/24.
 */

public class RoomMember implements Serializable {
    public final static String OBJ="obj";

    private String jid;
    private String nick;
    private String mucAffiliation;
    private String mucRole;

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getMucAffiliation() {
        return mucAffiliation;
    }

    public void setMucAffiliation(String mucAffiliation) {
        this.mucAffiliation = mucAffiliation;
    }

    public String getMucRole() {
        return mucRole;
    }

    public void setMucRole(String mucRole) {
        this.mucRole = mucRole;
    }
}
