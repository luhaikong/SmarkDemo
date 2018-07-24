package com.smack;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.smack.adapter.ChatAdapter;
import com.smack.adapter.RoomMemberAdapter;
import com.smack.xmpp.XmppConnectionFlag;
import com.smack.xmpp.XmppConnectionManager;
import com.smack.xmppentity.RoomHosted;
import com.smack.xmppentity.RoomMember;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/24.
 */

public class MultiUserListActivity extends BaseSmackPushActivity {

    public final static String TAG = MultiUserChatActivity.class.getSimpleName();
    private Context mContext;
    private Toolbar toolbar;

    private RecyclerView mRecyclerView;
    private List<RoomMember> mList = new ArrayList<>();
    private RoomMemberAdapter mAdapter;
    private RoomHosted roomHosted;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiuserlist);
        mContext = this;
        roomHosted = getIntent().getSerializableExtra(RoomHosted.OBJ)==null?
                new RoomHosted(): (RoomHosted) getIntent().getSerializableExtra(RoomHosted.OBJ);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(roomHosted.getName());
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        initRecyclerView();

        getMembers();
    }

    private void initRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,LinearLayoutManager.VERTICAL));
        mAdapter = new RoomMemberAdapter(this,mList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void getMembers(){
        XmppConnectionManager.newInstance().getMembers(new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case XmppConnectionFlag.KEY_FRIENDS_SUCCESS:
                        Bundle bundle = msg.getData();
                        List<RoomMember> li = (List<RoomMember>) bundle.getSerializable(XmppConnectionFlag.KEY_FRIENDS_SUCCESS_PARAMS);
                        if (li!=null){
                            mList.addAll(li);
                            mAdapter.notifyDataSetChanged();
                        }
                        break;
                    default:
                        break;
                }
            }
        },roomHosted.getJid());
    }


}
