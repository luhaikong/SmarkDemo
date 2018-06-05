package com.smack;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.smack.adapter.RosterAdapter;
import com.smack.xmpp.XmppConnectionFlag;
import com.smack.xmpp.XmppConnectionManager;
import com.smack.xmppentity.ItemFriend;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RosterAdapter mAdapter;
    private List<ItemFriend> mList = new ArrayList<>();
    private Context mContext;

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
                        List<ItemFriend> list = (List<ItemFriend>) bundle.getSerializable(XmppConnectionFlag.KEY_FRIENDS_SUCCESS_PARAMS);
                        mList.clear();
                        mList.addAll(list);
                        mAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private void initRecyclerView(){
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,LinearLayoutManager.VERTICAL));
        mAdapter = new RosterAdapter(this,mList);
        mAdapter.setOnItemOnClickListener(new RosterAdapter.OnItemOnClickListener() {
            @Override
            public void onClick(ItemFriend friend) {
                XmppConnectionManager.newInstance().sendMessageSin(new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        switch (msg.what){
                            case XmppConnectionFlag.KEY_SENDMESSAGESIN_SUCCESS:
                                Toast.makeText(mContext,"sendMessageSin",Toast.LENGTH_LONG).show();
                                break;
                            default:
                                break;
                        }
                    }
                },friend.getUser());
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
    }
}
