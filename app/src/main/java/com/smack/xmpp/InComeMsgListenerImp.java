package com.smack.xmpp;

import android.os.Bundle;
import android.os.Handler;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

/**
 * 注入了handler的监听器实现类
 * @author MyPC
 * @date 2018/6/1
 */

public class InComeMsgListenerImp implements InComeMsgListener {

    private XMPPTCPConnection connection;
    private android.os.Message inMessage;

    public InComeMsgListenerImp(XMPPTCPConnection connection) {
        this.connection = connection;
    }

    @Override
    public void processMessage(Chat chat, Message message, Handler handler) {
        if (handler!=null){
            inMessage = new android.os.Message();
            inMessage.what = XmppConnectionFlag.KEY_CHATCREATED_SUCCESS;
            Bundle bundle = new Bundle();
            bundle.putString(XmppConnectionFlag.KEY_CHATCREATED_SUCCESS_PARAMS,message.getBody());
            inMessage.setData(bundle);
            handler.sendMessage(inMessage);
        }
    }
}
