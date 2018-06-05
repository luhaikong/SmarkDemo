package com.smack;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.smack.service.SmackPushCallBack;
import com.smack.service.SmackPushService;
import com.smack.xmpp.XmppUserConfig;

/**
 * @author MyPC
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener
        , SmackPushCallBack {

    private EditText et_userAccount;
    private EditText et_userPassword;
    private Button btn_login,btn_register,btn_loginOut;
    private Context mContext;
    private SmackPushService.SmackPushBinder pushBinder;

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
    }

    @Override
    protected void onDestroy() {
        unbindService(this);
        super.onDestroy();
    }

    private void showMainActivity(){
        Intent intent = new Intent(mContext,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void bindSmackPushService(){
        Intent intent = new Intent(getApplicationContext(), SmackPushService.class);
        bindService(intent,this,BIND_AUTO_CREATE);
    }

    private void stopSmackPushService(){
        Intent intent = new Intent(getApplicationContext(), SmackPushService.class);
        stopService(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                et_userAccount.setText("test");
                et_userPassword.setText("123456");

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

    /**
     * 绑定服务的时候调用
     * @param name
     * @param service
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        pushBinder = (SmackPushService.SmackPushBinder) service;
        SmackPushService pushService = pushBinder.getService();
        pushService.initConnectionAndLogin(this);
    }

    /**
     * 服务解除绑定时候调用
     * @param name
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {

    }


    @Override
    public void connected() {
        Log.d("LoginActivity","----------------connected---------------");
    }

    @Override
    public void registerAccount(boolean success, String msg) {

    }

    @Override
    public void authenticated() {
        Log.d("LoginActivity","----------------authenticated---------------");
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


}
