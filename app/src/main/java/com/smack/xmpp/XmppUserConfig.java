package com.smack.xmpp;

import java.util.Map;

/**
 *
 * @author MyPC
 * @date 2018/6/4
 */

public class XmppUserConfig {

    public final static String TAG = "Tag";
    public final static String ALIAS = "Alias";

    private String ofUserName;//用户名
    private String ofPassword;//密码
    private Map<String, String> attr;//一些用户资料

    public String getOfUserName() {
        return ofUserName == null ? "" : ofUserName;
    }

    private void setOfUserName(String ofUserName) {
        this.ofUserName = ofUserName;
    }

    public String getOfPassword() {
        return ofPassword == null ? "" : ofPassword;
    }

    private void setOfPassword(String ofPassword) {
        this.ofPassword = ofPassword;
    }

    public Map<String, String> getAttr() {
        return attr;
    }

    private void setAttr(Map<String, String> attr) {
        this.attr = attr;
    }

    public static class Builder{

        private String ofUserName;
        private String ofPassword;
        private Map<String, String> attr;

        public Builder setOfUserName(String userName){
            this.ofUserName = userName;
            return this;
        }

        public Builder setOfPassword(String passWord){
            this.ofPassword = passWord;
            return this;
        }

        public Builder setAttr(Map<String, String> map){
            this.attr = map;
            return this;
        }

        public XmppUserConfig create(){
            XmppUserConfig config = new XmppUserConfig();
            config.setOfUserName(ofUserName);
            config.setOfPassword(ofPassword);
            config.setAttr(attr);
            return config;
        }
    }
}
