package com.smack.xmppentity;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author MyPC
 * @date 2018/6/8
 */

public class GroupFriend implements Serializable {

    private boolean isExpan;
    private String name;
    private List<ItemFriend> itemFriends;

    public boolean isExpan() {
        return isExpan;
    }

    public void setExpan(boolean expan) {
        isExpan = expan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ItemFriend> getItemFriends() {
        return itemFriends;
    }

    public void setItemFriends(List<ItemFriend> itemFriends) {
        this.itemFriends = itemFriends;
    }

    public static class ItemFriend implements Serializable {

        private String name;
        private String status;
        private String user;
        private String type_name;
        private int type_ordinal;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getType_name() {
            return type_name;
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
    }
}
