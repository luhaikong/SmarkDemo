package com.smack;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.smack.adapter.ChatAdapter;
import com.smack.entity.XmppMessage;
import com.smack.xmpp.OutGoMsgListener;
import com.smack.xmpp.XmppConnectionManager;
import com.smack.xmppentity.GroupFriend;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, OutGoMsgListener {

    public final static String TAG = ChatActivity.class.getSimpleName();
    private Context mContext;
    private Toolbar toolbar;

    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private List<XmppMessage> mList = new ArrayList<>();
    private GroupFriend.ItemFriend itemFriend;

    private Button btn_send;
    private EditText et_content;

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
        for (int i=0;i<50;i++){
            XmppMessage me = new XmppMessage();
            me.setCreateTime(System.currentTimeMillis());
            me.setBody("内容"+i);
            if (i%2==0){
                me.setDirect(XmppMessage.DIRECT_IN);
            } else {
                me.setDirect(XmppMessage.DIRECT_OUT);
            }
            mList.add(me);
        }
        mAdapter = new ChatAdapter(this,mList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send:
                String str = et_content.getText().toString();
                XmppConnectionManager.newInstance().sendMessageSin(this,itemFriend.getUser(),str);
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
    }

    @Override
    public void onOutGoFail(String msg) {
        Log.d(TAG,msg);
    }
}
