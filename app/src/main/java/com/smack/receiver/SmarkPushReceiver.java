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

    public final static String BUNDLEEXTRA = "BundleExtra";

    @Override
    public void onReceive(Context context, Intent intent) {
        String content = "";
        switch (intent.getAction()){
            case IntentReceiver.CONNECTION:
                content = "云网推送连接成功";
                break;
            case IntentReceiver.AUTHENTICATED:
                content = "云网推送认证成功";
                break;
            case IntentReceiver.MESSAGE_RECEIVED:
                content = intent.getStringExtra(BUNDLEEXTRA);
                break;
            default:
                break;
        }
        Log.d("SmarkPushReceiver",content);
    }

}
