package com.smack.xmpp;

import com.smack.xmppwrap.xmppentity.ItemFriend;

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

    private List<ItemFriend> rosterList;

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

    public List<ItemFriend> getFriendList(XMPPTCPConnection connection){
        Set<RosterEntry> set = getUnfiledEntries(connection);
        rosterList = new ArrayList<>();
        ItemFriend itemFriend = new ItemFriend();
        itemFriend.setName("我的好友");
        itemFriend.setGroup(true);
        rosterList.add(itemFriend);
        if (set!=null&&set.size()>0){
            for (RosterEntry entry:set){
                ItemFriend friend = new ItemFriend();
                friend.setName(entry.getName());
                friend.setUser(entry.getUser());
                rosterList.add(friend);
            }
        }

        Collection<RosterGroup> collection = getRosterGroupList(connection);
        if (collection!=null&&collection.size()>0){
            for (RosterGroup group : collection) {
                ItemFriend gFriend = new ItemFriend();
                gFriend.setName(group.getName());
                gFriend.setGroup(true);
                rosterList.add(gFriend);
                List<RosterEntry> entries = group.getEntries();
                for (RosterEntry entry : entries) {
                    ItemFriend friend = new ItemFriend();
                    friend.setUser(entry.getUser());
                    friend.setName(entry.getName());
                    rosterList.add(friend);
                }
            }
        }

        return rosterList;
    }
}
