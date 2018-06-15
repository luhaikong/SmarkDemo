package com.smack.xmpp;

import com.smack.xmppentity.GroupFriend;

import org.jivesoftware.smack.packet.Presence;
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
        Roster roster = Roster.getInstanceFor(connection);
        Set<RosterEntry> set = getUnfiledEntries(connection);
        rosterList = new ArrayList<>();
        GroupFriend groupFriend = new GroupFriend();
        groupFriend.setName("我的好友");
        List<GroupFriend.ItemFriend> list = new ArrayList<>();
        if (set!=null&&set.size()>0){
            int count_online = 0;
            for (RosterEntry entry:set){
                Presence p = roster.getPresence(entry.getUser());
                GroupFriend.ItemFriend friend = new GroupFriend.ItemFriend();
                friend.setName(entry.getName());
                friend.setUser(entry.getUser());
                friend.setType_presence(p.getType().name());
                list.add(friend);

                if (friend.getType_presence().equals(Presence.Type.available.name())){
                    count_online++;
                }
            }
            groupFriend.setCount_online(count_online);
        }
        groupFriend.setItemFriends(list);
        rosterList.add(groupFriend);

        Collection<RosterGroup> collection = getRosterGroupList(connection);
        if (collection!=null&&collection.size()>0){
            for (RosterGroup group : collection) {
                int count_online = 0;
                GroupFriend gFriend = new GroupFriend();
                gFriend.setName(group.getName());
                List<GroupFriend.ItemFriend> listTemp = new ArrayList<>();
                List<RosterEntry> entries = group.getEntries();
                for (RosterEntry entry : entries) {
                    Presence p = roster.getPresence(entry.getUser());
                    GroupFriend.ItemFriend friend = new GroupFriend.ItemFriend();
                    friend.setUser(entry.getUser());
                    friend.setName(entry.getName());
                    friend.setType_presence(p.getType().name());
                    listTemp.add(friend);

                    if (friend.getType_presence().equals(Presence.Type.available.name())){
                        count_online++;
                    }
                }
                gFriend.setCount_online(count_online);
                gFriend.setItemFriends(listTemp);
                rosterList.add(gFriend);
            }
        }

        return rosterList;
    }
}
