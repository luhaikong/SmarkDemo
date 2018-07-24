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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.smack.adapter.ChatAdapter;
import com.smack.entity.XmppMessage;
import com.smack.service.SmackPushService;
import com.smack.xmpp.OutGoMsgListener;
import com.smack.xmpp.XmppConnectionFlag;
import com.smack.xmpp.XmppConnectionManager;
import com.smack.xmppentity.GroupFriend;
import com.smack.xmppentity.RoomHosted;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MyPC
 */
public class MultiUserChatActivity extends BaseSmackPushActivity implements View.OnClickListener, OutGoMsgListener {

    public final static String TAG = MultiUserChatActivity.class.getSimpleName();
    private Context mContext;
    private Toolbar toolbar;

    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private List<XmppMessage> mList = new ArrayList<>();
    private RoomHosted roomHosted;

    private Button btn_send;
    private EditText et_content;

    private SmackPushService.SmackPushBinder pushBinder;
    private ServiceConnection pushConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            pushBinder = (SmackPushService.SmackPushBinder) service;
            SmackPushService pushService = pushBinder.getService();
            pushService.setSmackPushCallBack(MultiUserChatActivity.this);
            pushService.addChatListener(roomHosted.getJid());
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
        roomHosted = getIntent().getSerializableExtra(RoomHosted.OBJ)==null?
                new RoomHosted(): (RoomHosted) getIntent().getSerializableExtra(RoomHosted.OBJ);
        setTitle(roomHosted.getName());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_room_member_add:
                        invitations(roomHosted.getJid(),"yanghuaxiong@rocketmq-03",XmppConnectionManager.newInstance().getOfXmppUserConfig().getAttr().get("name"),"");
                        break;
                    case R.id.menu_person_list:
                        showMemberListActivity();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        et_content = (EditText) findViewById(R.id.et_content);

        initRecyclerView();
    }

    private void showMemberListActivity(){
        Intent intent = new Intent(mContext,MultiUserListActivity.class);
        intent.putExtra(RoomHosted.OBJ,roomHosted);
        startActivity(intent);
    }

    private void invitations(String mucJid, String otherJid, String nickNameMySelf, String password){
        XmppConnectionManager.newInstance().invitations(new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case XmppConnectionFlag.KEY_FRIENDS_SUCCESS:
                        showToast("邀请发送成功！");
                        break;
                    default:
                        break;
                }
            }
        },mucJid,otherJid,nickNameMySelf,password);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_member, menu);
        return true;
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
                XmppConnectionManager.newInstance().sendMessageMuc(this,roomHosted.getJid(),str);
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
        //此处不需要了，processMessage方法会有回调
    }

    @Override
    public void onOutGoFail(String msg) {
        Log.d(TAG,msg);
    }

    @Override
    public void processMessage(String content, boolean createdLocally) {
        super.processMessage(content, createdLocally);
        Message msg = new Message();
        if (createdLocally){
            msg.what = 2;
        } else {
            msg.what = 1;
        }
        Bundle bd = new Bundle();
        bd.putString("body",content);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    @Override
    public void connectionClosedOnError(Exception e) {

    }
}
