package com.smack;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.smack.service.SmackPushCallBack;
import com.smack.xmpp.XmppUserConfig;

import org.jivesoftware.smack.XMPPConnection;

/**
 *
 * @author MyPC
 * @date 2018/6/21
 */

public class BaseSmackPushFragment extends Fragment implements SmackPushCallBack {

    protected void showToast(String msg){
        Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
    }

    protected void showAlertDialog(String title,String content){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
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
    public void connected(XMPPConnection connection) {

    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {

    }

    @Override
    public void connectionClosed() {

    }

    @Override
    public void connectionClosedOnError(Exception e) {

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
}
