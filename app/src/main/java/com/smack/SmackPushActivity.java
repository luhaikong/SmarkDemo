package com.smack;

import android.support.v7.app.AppCompatActivity;

import com.smack.service.SmackPushCallBack;
import com.smack.xmpp.XmppUserConfig;

/**
 *
 * @author MyPC
 * @date 2018/6/20
 */

public abstract class SmackPushActivity extends AppCompatActivity implements SmackPushCallBack {

    @Override
    public void connected() {

    }

    @Override
    public void registerAccount(boolean success, String msg) {

    }

    @Override
    public void authenticated() {

    }

    @Override
    public void chatCreated(String content, boolean createdLocally) {

    }

    @Override
    public void logout(XmppUserConfig config) {

    }

}
