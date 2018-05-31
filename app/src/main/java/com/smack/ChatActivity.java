package com.smack;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.smack.adapter.ChatAdapter;
import com.smack.entity.XmppMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private List<XmppMessage> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle("好友昵称");
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
}
