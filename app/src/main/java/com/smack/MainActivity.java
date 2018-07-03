package com.smack;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.smack.adapter.RosterAdapter;
import com.smack.service.SmackPushService;
import com.smack.xmpp.XmppConnectionFlag;
import com.smack.xmpp.XmppConnectionManager;
import com.smack.xmppentity.GroupFriend;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MyPC
 */
public class MainActivity extends BaseSmackPushActivity {

    private Toolbar toolbar;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView mRecyclerView;
    private RosterAdapter mAdapter;
    private List<GroupFriend> mList = new ArrayList<>();
    private Context mContext;

    private SmackPushService.SmackPushBinder pushBinder;
    private ServiceConnection pushConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            pushBinder = (SmackPushService.SmackPushBinder) service;
            SmackPushService pushService = pushBinder.getService();
            pushService.setSmackPushCallBack(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
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
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);

        initRecyclerView();

        requestData(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindSmackPushService();
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

    private void requestData(boolean isFresh){
        XmppConnectionManager.newInstance().getFriendList(new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case XmppConnectionFlag.KEY_FRIENDS_SUCCESS:
                        if (refreshLayout.isRefreshing()){
                            refreshLayout.setRefreshing(false);
                        }

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
    }

    private void bindSmackPushService(){
        Intent intent = new Intent(this, SmackPushService.class);
        bindService(intent,pushConn,BIND_AUTO_CREATE);
    }

    private void initRecyclerView(){
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,LinearLayoutManager.VERTICAL));
        mAdapter = new RosterAdapter(mContext,mList);
        mRecyclerView.setAdapter(mAdapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData(true);
            }
        });
    }

    private void showChatActivity(GroupFriend.ItemFriend friend){
        Intent intent = new Intent(mContext,ChatActivity.class);
        intent.putExtra(GroupFriend.ItemFriend.OBJ,friend);
        startActivity(intent);
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
    public void connectionClosedOnError(Exception e) {

    }
}
