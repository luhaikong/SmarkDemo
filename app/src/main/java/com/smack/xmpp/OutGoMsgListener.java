package com.smack.xmpp;

/**
 *
 * @author MyPC
 * @date 2018/6/19
 */

public interface OutGoMsgListener {

    String OUTGOING = "正在发送...";

    /**
     * 正在发送中
     */
    void onOutGoing();

    /**
     * 发送成功
     * @param body
     */
    void onOutGoSuccess(String body);

    /**
     * 发送失败
     * @param msg
     */
    void onOutGoFail(String msg);

}
