package com.smack;

import android.app.Application;
import android.content.Intent;

import com.smack.service.SmackPushService;
import com.smack.xmpp.XmppConnectionManager;
import com.smack.xmpp.XmppUserConfig;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author MyPC
 * @date 2018/5/30
 */

public class SmackApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initXmppUserConfig();
        initSmackPushService();
    }

    private void initXmppUserConfig(){
        Map<String,String> attr = new HashMap<>(2);
//        attr.put(XmppUserConfig.TAG,"Android开发");
//        attr.put(XmppUserConfig.ALIAS,"吴政通");
        attr.put("name","android开发者-吴政通");
        attr.put("email","1031359299@qq.com");
        XmppUserConfig config = new XmppUserConfig.Builder()
                .setOfUserName("wuzhengtong")
                .setOfPassword("123456")
                .setAttr(attr)
                .create();
        XmppConnectionManager.newInstance().setOfXmppUserConfig(config);
    }

    private void initSmackPushService(){
        Intent intent = new Intent(getApplicationContext(), SmackPushService.class);
        startService(intent);
    }
}
