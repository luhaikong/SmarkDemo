package com.smack.xmpp;

import android.os.Handler;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;

/**
 * 接收消息监听器
 * @author MyPC
 * @date 2018/6/1
 */

public interface InComeMsgListener {

    void processMessage(Chat chat, Message message, Handler handler);
}
