package com.smack.xmpp;

import com.smack.xmppentity.GroupFriend;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by MyPC on 2018/5/30.
 */

public class XmppRosterManager {

    private List<GroupFriend> rosterList;

    private static class Holder {
        private static XmppRosterManager singleton = new XmppRosterManager();
    }

    public static XmppRosterManager newInstance(){
        return Holder.singleton;
    }

    public Collection<RosterGroup> getRosterGroupList(XMPPTCPConnection connection) {
        Roster roster = Roster.getInstanceFor(connection);
        return roster.getGroups();
    }

    public Set<RosterEntry> getUnfiledEntries(XMPPTCPConnection connection) {
        Roster roster = Roster.getInstanceFor(connection);
        return roster.getUnfiledEntries();
    }

    public List<GroupFriend> getFriendList(XMPPTCPConnection connection, String jid){
        Set<RosterEntry> set = getUnfiledEntries(connection);
        rosterList = new ArrayList<>();
        GroupFriend groupFriend = new GroupFriend();
        groupFriend.setName("我的好友");
        List<GroupFriend.ItemFriend> list = new ArrayList<>();
        if (set!=null&&set.size()>0){
            for (RosterEntry entry:set){
                GroupFriend.ItemFriend friend = new GroupFriend.ItemFriend();
                friend.setName(entry.getName());
                friend.setUser(entry.getUser());
                list.add(friend);
            }
        }
        groupFriend.setItemFriends(list);
        rosterList.add(groupFriend);

        Collection<RosterGroup> collection = getRosterGroupList(connection);
        if (collection!=null&&collection.size()>0){
            for (RosterGroup group : collection) {
                GroupFriend gFriend = new GroupFriend();
                gFriend.setName(group.getName());
                List<GroupFriend.ItemFriend> listTemp = new ArrayList<>();
                List<RosterEntry> entries = group.getEntries();
                for (RosterEntry entry : entries) {
                    GroupFriend.ItemFriend friend = new GroupFriend.ItemFriend();
                    friend.setUser(entry.getUser());
                    friend.setName(entry.getName());
                    listTemp.add(friend);
                }
                gFriend.setItemFriends(listTemp);
                rosterList.add(gFriend);
            }
        }

        return rosterList;
    }
}
