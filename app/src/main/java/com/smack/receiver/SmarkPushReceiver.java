package com.smack.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 *
 * @author MyPC
 * @date 2018/6/4
 */

public class SmarkPushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String content = "";
        switch (intent.getAction()){
            case IntentReceiver.IntentEnum.CONNECTION:
                content = "云网推送连接成功";
                break;
            case IntentReceiver.IntentEnum.REGISTRATION:
                content = "云网推送注册成功";
                break;
            case IntentReceiver.IntentEnum.AUTHENTICATED:
                content = "云网推送认证成功";
                break;
            case IntentReceiver.IntentEnum.MESSAGE_RECEIVED:
                content = intent.getStringExtra(IntentReceiver.EXTRA);
                break;
            default:
                break;
        }
        Log.d("SmarkPushReceiver",content);
    }

}
