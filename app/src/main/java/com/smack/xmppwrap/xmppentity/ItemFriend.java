package com.smack.xmppwrap.xmppentity;

import java.io.Serializable;

/**
 * Created by MyPC on 2018/5/31.
 */

public class ItemFriend implements Serializable {

    private String name;
    private String status;
    private String user;
    private String type_name;
    private int type_ordinal;

    private boolean isGroup;//是否是用户组
    private boolean isExpan;//是否展开组

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status == null ? "" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser() {
        return user == null ? "" : user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getType_name() {
        return type_name == null ? "" : type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public int getType_ordinal() {
        return type_ordinal;
    }

    public void setType_ordinal(int type_ordinal) {
        this.type_ordinal = type_ordinal;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public boolean isExpan() {
        return isExpan;
    }

    public void setExpan(boolean expan) {
        isExpan = expan;
    }

    @Override
    public String toString() {
        return "ItemFriend{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", user='" + user + '\'' +
                ", type_name='" + type_name + '\'' +
                ", type_ordinal=" + type_ordinal +
                ", isGroup=" + isGroup +
                ", isExpan=" + isExpan +
                '}';
    }
}
