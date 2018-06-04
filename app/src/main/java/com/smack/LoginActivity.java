package com.smack;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.smack.xmpp.XmppConnectionFlag;
import com.smack.xmpp.XmppConnectionManager;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText et_userAccount;
    private EditText et_userPassword;
    private Button btn_login,btn_init,btn_loginOut;
    private Context mContext;

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
    }

    private void showMainActivity(){
        Intent intent = new Intent(mContext,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                et_userAccount.setText("test");
                et_userPassword.setText("123456");
                String userAccount = et_userAccount.getText().toString().trim();
                String userPwd = et_userPassword.getText().toString().trim();
                XmppConnectionManager.newInstance().initConnectionAndLogin(new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        switch (msg.what){
                            case XmppConnectionFlag.KEY_CONNECTED:
                                Toast.makeText(mContext,"connected",Toast.LENGTH_LONG).show();
                                break;
                            case XmppConnectionFlag.KEY_AUTHENTICATED:
                                Toast.makeText(mContext,"authenticated",Toast.LENGTH_LONG).show();
                                showMainActivity();
                                break;
                            default:
                                break;
                        }
                    }
                }, userAccount, userPwd, getApplicationContext());
                break;
            case R.id.btn_loginOut:
                XmppConnectionManager.newInstance().logout(null);
                break;
            default:
                break;
        }
    }
}
