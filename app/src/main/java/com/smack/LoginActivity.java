package com.smack;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.smack.service.SmackPushService;
import com.smack.sp.SharePreferenceMgr;
import com.smack.xmpp.XmppConnectionManager;
import com.smack.xmpp.XmppUserConfig;

import org.jivesoftware.smack.XMPPConnection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MyPC
 */
public class LoginActivity extends BaseSmackPushActivity implements View.OnClickListener {

    private EditText et_userAccount;
    private EditText et_userPassword;
    private Button btn_login,btn_register,btn_loginOut;
    private Context mContext;

    private SmackPushService.SmackPushBinder pushBinder;
    private ServiceConnection pushConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            pushBinder = (SmackPushService.SmackPushBinder) service;
            SmackPushService pushService = pushBinder.getService();
            pushService.initConnectionAndLogin(LoginActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            showMainActivity();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;

        et_userAccount = (EditText) findViewById(R.id.et_userAccount);
        et_userPassword = (EditText) findViewById(R.id.et_userPassword);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        btn_loginOut = (Button) findViewById(R.id.btn_loginOut);
        btn_loginOut.setOnClickListener(this);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);

        initUser();
    }

    private void initUser() {
        String username = (String) SharePreferenceMgr.get(this,SharePreferenceMgr.KEY_OFUSERNAME,"");
        String pw = (String) SharePreferenceMgr.get(this,SharePreferenceMgr.KEY_OFPASSWORD,"");
        if (username!=null&&pw!=null&&!username.isEmpty()&&!pw.isEmpty()){
            et_userAccount.setText(username);
            et_userPassword.setText(pw);
        }
    }

    private void showMainActivity(){
        Intent intent = new Intent(mContext,MainActivity2.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                if (et_userAccount.getText().toString().isEmpty()){
                    showToast("请填写用户名！");
                    return;
                }
                if(et_userPassword.getText().toString().isEmpty()){
                    showToast("请填写密码！");
                    return;
                }
                initXmppUserConfig(et_userAccount.getText().toString(),et_userPassword.getText().toString());

                bindSmackPushService();
                break;
            case R.id.btn_loginOut:
                stopSmackPushService();
                break;
            case R.id.btn_register:

                break;
            default:
                break;
        }
    }

    private void initXmppUserConfig(String username,String pwd){
        Map<String,String> attr = new HashMap<>(2);
        attr.put("name","游客");
        attr.put("email","1031359299@qq.com");
        XmppUserConfig config = new XmppUserConfig.Builder()
                .setOfUserName(username)
                .setOfPassword(pwd)
                .setAttr(attr)
                .create();
        XmppConnectionManager.newInstance().setOfXmppUserConfig(config);
    }

    private void bindSmackPushService(){
        Intent intent = new Intent(this, SmackPushService.class);
        bindService(intent,pushConn,BIND_AUTO_CREATE);
    }

    private void stopSmackPushService(){
        Intent intent = new Intent(this, SmackPushService.class);
        stopService(intent);
    }

    @Override
    protected void onPause() {
        if (pushConn!=null&&pushBinder!=null){
            unbindService(pushConn);
            pushBinder = null;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (pushConn!=null&&pushBinder!=null){
            unbindService(pushConn);
            pushBinder = null;
        }
        super.onDestroy();
    }

    @Override
    public void connected(XMPPConnection connection) {
        super.connected(connection);
        Log.d("LoginActivity","----------------connected---------------");
    }

    @Override
    public void registerAccount(boolean success, String msg) {
        Log.d("LoginActivity","-------------registerAccount------------");
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        super.authenticated(connection, resumed);
        Log.d("LoginActivity","----------------authenticated---------------");
        XmppUserConfig config = XmppConnectionManager.newInstance().getOfXmppUserConfig();
        SharePreferenceMgr.put(mContext,SharePreferenceMgr.KEY_OFUSERNAME,config.getOfUserName());
        SharePreferenceMgr.put(mContext,SharePreferenceMgr.KEY_OFPASSWORD,config.getOfPassword());

        SmackPushService pushService = pushBinder.getService();
        pushService.addChatListener(this);
        handler.sendEmptyMessage(0);
    }

    @Override
    public void chatCreated(String content, boolean createdLocally) {
        Log.d("LoginActivity","----------------chatCreated---------------");
    }

    @Override
    public void logout(XmppUserConfig config) {
        Log.d("LoginActivity","----------------logout---------------");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.d("LoginActivity","----------------connectionClosedOnError---------------");
    }
}
