package com.smack.entity;

import java.io.Serializable;

/**
 * Created by MyPC on 2018/5/30.
 */

public class XmppMessage implements Serializable {

    private Long createTime;

    private int direct;//1:收到消息，0:发送消息
    public final static int DIRECT_IN = 1;
    public final static int DIRECT_OUT = 0;

    private String body;


    public XmppMessage() {
    }

    public XmppMessage(Long createTime, int direct, String body) {
        this.createTime = createTime;
        this.direct = direct;
        this.body = body;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public int getDirect() {
        return direct;
    }

    public void setDirect(int direct) {
        this.direct = direct;
    }

    public String getBody() {
        return body == null ? "" : body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
