package com.smack;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.smack.service.SmackPushCallBack;
import com.smack.xmpp.XmppUserConfig;

import org.jivesoftware.smack.XMPPConnection;

/**
 *
 * @author MyPC
 * @date 2018/6/20
 */

public class BaseSmackPushActivity extends AppCompatActivity implements SmackPushCallBack {

    protected void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    @Override
    public void registerAccount(boolean success, String msg) {

    }

    @Override
    public void chatCreated(String content, boolean createdLocally) {

    }

    @Override
    public void logout(XmppUserConfig config) {

    }

    @Override
    public void processMessage(String content) {

    }

    @Override
    public void subjectUpdated(String subject, String from) {

    }

    @Override
    public void connected(XMPPConnection connection) {

    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {

    }

    @Override
    public void connectionClosed() {

    }

    @Override
    public void reconnectionSuccessful() {

    }

    @Override
    public void reconnectingIn(int seconds) {

    }

    @Override
    public void reconnectionFailed(Exception e) {
        showToast(e.getMessage());
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        showToast(e.getMessage());
    }
}
