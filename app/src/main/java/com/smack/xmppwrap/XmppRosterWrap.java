package com.smack.xmppwrap;

import com.smack.xmpp.XmppRosterManager;
import com.smack.xmppwrap.xmppentity.ItemFriend;

import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by MyPC on 2018/5/31.
 */

public class XmppRosterWrap {

    private XmppRosterManager xmppRosterManager;
    private List<Object> rosterList;

    public XmppRosterWrap(XmppRosterManager xmppRosterManager,ScheduledExecutorService executorService) {
        this.xmppRosterManager = xmppRosterManager;
    }

    public List<Object> getFriendList(XMPPTCPConnection connection){
        Set<RosterEntry> set = xmppRosterManager.getUnfiledEntries(connection);
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

        Collection<RosterGroup> collection = xmppRosterManager.getRosterGroupList(connection);
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
