package com.smack.xmppentity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Smack 中 RoomInfo 类对应的自建类
 * @author MyPC
 * @date 2018/6/22
 */

public class RoomMucInfo implements Serializable {

    private String room;
    private String description;
    private String name;
    private String subject;
    private int occupantsCount;
    private boolean membersOnly;
    private boolean moderated;
    private boolean nonanonymous;
    private boolean passwordProtected;
    private boolean persistent;
    private int maxhistoryfetch;
    private List<String> contactJid;
    private String lang;
    private String ldapgroup;
    private Boolean subjectmod;
    private String logs;
    private String pubsub;

    public String getRoom() {
        return room == null ? "" : room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject == null ? "" : subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getOccupantsCount() {
        return occupantsCount;
    }

    public void setOccupantsCount(int occupantsCount) {
        this.occupantsCount = occupantsCount;
    }

    public boolean isMembersOnly() {
        return membersOnly;
    }

    public void setMembersOnly(boolean membersOnly) {
        this.membersOnly = membersOnly;
    }

    public boolean isModerated() {
        return moderated;
    }

    public void setModerated(boolean moderated) {
        this.moderated = moderated;
    }

    public boolean isNonanonymous() {
        return nonanonymous;
    }

    public void setNonanonymous(boolean nonanonymous) {
        this.nonanonymous = nonanonymous;
    }

    public boolean isPasswordProtected() {
        return passwordProtected;
    }

    public void setPasswordProtected(boolean passwordProtected) {
        this.passwordProtected = passwordProtected;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public int getMaxhistoryfetch() {
        return maxhistoryfetch;
    }

    public void setMaxhistoryfetch(int maxhistoryfetch) {
        this.maxhistoryfetch = maxhistoryfetch;
    }

    public List<String> getContactJid() {
        if (contactJid == null) {
            return new ArrayList<>();
        }
        return contactJid;
    }

    public void setContactJid(List<String> contactJid) {
        this.contactJid = contactJid;
    }

    public String getLang() {
        return lang == null ? "" : lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getLdapgroup() {
        return ldapgroup == null ? "" : ldapgroup;
    }

    public void setLdapgroup(String ldapgroup) {
        this.ldapgroup = ldapgroup;
    }

    public Boolean getSubjectmod() {
        return subjectmod;
    }

    public void setSubjectmod(Boolean subjectmod) {
        this.subjectmod = subjectmod;
    }

    public String getLogs() {
        return logs == null ? "" : logs;
    }

    public void setLogs(String logs) {
        this.logs = logs;
    }

    public String getPubsub() {
        return pubsub == null ? "" : pubsub;
    }

    public void setPubsub(String pubsub) {
        this.pubsub = pubsub;
    }

    @Override
    public String toString() {
        return "RoomMucInfo{" +
                "room='" + room + '\'' +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", subject='" + subject + '\'' +
                ", occupantsCount=" + occupantsCount +
                ", membersOnly=" + membersOnly +
                ", moderated=" + moderated +
                ", nonanonymous=" + nonanonymous +
                ", passwordProtected=" + passwordProtected +
                ", persistent=" + persistent +
                ", maxhistoryfetch=" + maxhistoryfetch +
                ", contactJid=" + contactJid +
                ", lang='" + lang + '\'' +
                ", ldapgroup='" + ldapgroup + '\'' +
                ", subjectmod=" + subjectmod +
                ", logs='" + logs + '\'' +
                ", pubsub='" + pubsub + '\'' +
                '}';
    }
}
