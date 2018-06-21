package com.smack;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.smack.adapter.ChatAdapter;
import com.smack.entity.XmppMessage;
import com.smack.service.SmackPushService;
import com.smack.xmpp.OutGoMsgListener;
import com.smack.xmpp.XmppConnectionManager;
import com.smack.xmppentity.GroupFriend;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MyPC
 */
public class ChatActivity extends BaseSmackPushActivity implements View.OnClickListener, OutGoMsgListener {

    public final static String TAG = ChatActivity.class.getSimpleName();
    private Context mContext;
    private Toolbar toolbar;

    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private List<XmppMessage> mList = new ArrayList<>();
    private GroupFriend.ItemFriend itemFriend;

    private Button btn_send;
    private EditText et_content;

    private SmackPushService.SmackPushBinder pushBinder;
    private ServiceConnection pushConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            pushBinder = (SmackPushService.SmackPushBinder) service;
            SmackPushService pushService = pushBinder.getService();
            pushService.addChatListener(ChatActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 2:
                    String bodyRight = msg.getData().getString("body");
                    XmppMessage meRight = new XmppMessage();
                    meRight.setCreateTime(System.currentTimeMillis());
                    meRight.setBody(bodyRight);
                    meRight.setDirect(XmppMessage.DIRECT_OUT);
                    mList.add(meRight);
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
                    break;
                case 1:
                    String bodyLeft = msg.getData().getString("body");
                    XmppMessage meLeft = new XmppMessage();
                    meLeft.setCreateTime(System.currentTimeMillis());
                    meLeft.setBody(bodyLeft);
                    meLeft.setDirect(XmppMessage.DIRECT_IN);
                    mList.add(meLeft);
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mContext = this;
        itemFriend = getIntent().getSerializableExtra(GroupFriend.ItemFriend.OBJ)==null?
                new GroupFriend.ItemFriend(): (GroupFriend.ItemFriend) getIntent().getSerializableExtra(GroupFriend.ItemFriend.OBJ);
        setTitle(itemFriend.getName());
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
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        et_content = (EditText) findViewById(R.id.et_content);

        initRecyclerView();
    }

    private void initRecyclerView(){
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new ChatAdapter(this,mList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
    }

    private void bindSmackPushService(){
        Intent intent = new Intent(this, SmackPushService.class);
        bindService(intent,pushConn,BIND_AUTO_CREATE);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send:
                String str = et_content.getText().toString();
                XmppConnectionManager.newInstance().sendMessageSin(this,itemFriend.getUser(),str);
                et_content.setText("");
                break;
            default:
                break;
        }
    }

    @Override
    public void onOutGoing() {
        Log.d(TAG,OutGoMsgListener.OUTGOING);
    }

    @Override
    public void onOutGoSuccess(String content) {
        Log.d(TAG,content);
        Message msg = new Message();
        msg.what = 2;
        Bundle bd = new Bundle();
        bd.putSerializable("body",content);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    @Override
    public void onOutGoFail(String msg) {
        Log.d(TAG,msg);
    }

    @Override
    public void chatCreated(String content, boolean createdLocally) {
        Message msg = new Message();
        msg.what = 1;
        Bundle bd = new Bundle();
        bd.putString("body",content);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    @Override
    public void connectionClosedOnError(Exception e) {

    }
}
