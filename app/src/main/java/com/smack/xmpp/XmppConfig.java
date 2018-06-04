package com.smack.xmpp;

/**
 *
 * @author MyPC
 * @date 2018/6/4
 */

public class XmppConfig {

    private String ofUserName;
    private String ofPassword;

    public String getOfUserName() {
        return ofUserName == null ? "" : ofUserName;
    }

    public void setOfUserName(String ofUserName) {
        this.ofUserName = ofUserName;
    }

    public String getOfPassword() {
        return ofPassword == null ? "" : ofPassword;
    }

    public void setOfPassword(String ofPassword) {
        this.ofPassword = ofPassword;
    }

    private XmppConfig(Builder builder){
        builder.create();
    }

    public static class Builder{

        XmppConfig config;

        public Builder(XmppConfig config) {
            this.config = config;
        }

        private XmppConfig setOfUserName(String userName){
            config.setOfUserName(userName);
            return config;
        }

        private XmppConfig setOfPassword(String passWord){
            config.setOfPassword(passWord);
            return config;
        }

        private XmppConfig create(){
            return config;
        }
    }
}
