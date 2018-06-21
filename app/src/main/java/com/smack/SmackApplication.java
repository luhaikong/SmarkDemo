package com.smack;

import android.app.Application;
import android.content.Intent;

import com.smack.service.SmackPushService;
import com.smack.sp.SharePreferenceMgr;
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
        String username = (String) SharePreferenceMgr.get(this,SharePreferenceMgr.KEY_OFUSERNAME,"");
        String pw = (String) SharePreferenceMgr.get(this,SharePreferenceMgr.KEY_OFPASSWORD,"");
        if (username!=null&&pw!=null&&!username.isEmpty()&&!pw.isEmpty()){
            Map<String,String> attr = new HashMap<>(2);
            attr.put("name","游客");
            attr.put("email","1031359299@qq.com");
            XmppUserConfig config = new XmppUserConfig.Builder()
                    .setOfUserName(username)
                    .setOfPassword(pw)
                    .setAttr(attr)
                    .create();
            XmppConnectionManager.newInstance().setOfXmppUserConfig(config);
        }
    }

    private void initSmackPushService(){
        Intent intent = new Intent(getApplicationContext(), SmackPushService.class);
        startService(intent);
    }

}
