package com.smack.xmppentity;

import java.io.Serializable;

/**
 *
 * @author MyPC
 * @date 2018/6/21
 */

public class RoomHosted implements Serializable {

    private String jid;

    private String name;

    private int count;

    public String getJid() {
        return jid == null ? "" : jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "RoomHosted{" +
                "jid='" + jid + '\'' +
                ", name='" + name + '\'' +
                ", count=" + count +
                '}';
    }
}
