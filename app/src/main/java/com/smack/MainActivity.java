package com.smack;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.smack.adapter.RosterAdapter;
import com.smack.service.SmackPushCallBack;
import com.smack.service.SmackPushService;
import com.smack.xmpp.XmppConnectionFlag;
import com.smack.xmpp.XmppConnectionManager;
import com.smack.xmpp.XmppUserConfig;
import com.smack.xmppentity.GroupFriend;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SmackPushCallBack {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RosterAdapter mAdapter;
    private List<GroupFriend> mList = new ArrayList<>();
    private Context mContext;
    private SmackPushService.SmackPushBinder pushBinder;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String body = bundle.getString("body");
            Toast.makeText(mContext,body,Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        setTitle("好友列表");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        initRecyclerView();

        XmppConnectionManager.newInstance().getFriendList(new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case XmppConnectionFlag.KEY_FRIENDS_SUCCESS:
                        Bundle bundle = msg.getData();
                        mList = (List<GroupFriend>) bundle.getSerializable(XmppConnectionFlag.KEY_FRIENDS_SUCCESS_PARAMS);
                        mAdapter = new RosterAdapter(mContext,mList);
                        mAdapter.setOnItemOnClickListener(new RosterAdapter.OnItemOnClickListener() {
                            @Override
                            public void onClick(GroupFriend.ItemFriend friend) {
                                showChatActivity(friend);
                            }
                        });

                        mRecyclerView.setAdapter(mAdapter);
                        break;
                    default:
                        break;
                }
            }
        });

        bindSmackPushService();
    }

    @Override
    protected void onDestroy() {
        unbindService(this);
        super.onDestroy();
    }

    private void bindSmackPushService(){
        Intent intent = new Intent(mContext, SmackPushService.class);
        bindService(intent,this,BIND_AUTO_CREATE);
    }

    private void initRecyclerView(){
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,LinearLayoutManager.VERTICAL));
    }

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
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("body",content);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @Override
    public void logout(XmppUserConfig config) {

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        pushBinder = (SmackPushService.SmackPushBinder) service;
        SmackPushService pushService = pushBinder.getService();
        pushService.addChatListener(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    private void showChatActivity(GroupFriend.ItemFriend friend){
        Intent intent = new Intent(mContext,ChatActivity.class);
        intent.putExtra(GroupFriend.ItemFriend.OBJ,friend);
        startActivity(intent);
    }
}
