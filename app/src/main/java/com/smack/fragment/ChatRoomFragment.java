package com.smack.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.smack.BaseSmackPushFragment;
import com.smack.R;
import com.smack.adapter.RoomAdapter;
import com.smack.xmpp.XmppConnectionFlag;
import com.smack.xmpp.XmppConnectionManager;
import com.smack.xmppentity.RoomHosted;
import com.smack.xmppentity.RoomMucInfo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MyPC
 * @date 2018/6/21
 */

public class ChatRoomFragment extends BaseSmackPushFragment {

    public static ChatRoomFragment newInstance(Bundle bundle){
        ChatRoomFragment fragment = new ChatRoomFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView mRecyclerView;
    private RoomAdapter mAdapter;
    private List<RoomHosted> mList = new ArrayList<>();
    private RoomHosted mRoomHosted;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.title_chatRoom));
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_room,container,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        mRoomHosted = getArguments().getSerializable(RoomHosted.OBJ)==null?new RoomHosted():(RoomHosted) getArguments().getSerializable(RoomHosted.OBJ);

        initRecyclerView();

        requestData(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_roomf, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_room_add:
                showAlertDialog("提示","创建房间！");
//                createChatRoom(mRoomHosted.getJid(),"","");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestData(boolean b) {
        XmppConnectionManager.newInstance().getHostedRooms2(new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case XmppConnectionFlag.KEY_FRIENDS_SUCCESS:
                        if (refreshLayout.isRefreshing()){
                            refreshLayout.setRefreshing(false);
                        }

                        Bundle bundle = msg.getData();
                        mList = (List<RoomHosted>) bundle.getSerializable(XmppConnectionFlag.KEY_FRIENDS_SUCCESS_PARAMS);
                        mAdapter = new RoomAdapter(getActivity(),mList);
                        mAdapter.setOnItemOnClickListener(new RoomAdapter.OnItemOnClickListener() {
                            @Override
                            public void onClick(RoomHosted roomHosted) {

                            }
                        });
                        mRecyclerView.setAdapter(mAdapter);
                        break;
                    default:
                        break;
                }
            }
        },mRoomHosted.getJid());
    }

    private void initRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),LinearLayoutManager.VERTICAL));
        mAdapter = new RoomAdapter(getActivity(),mList);
        mRecyclerView.setAdapter(mAdapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData(true);
            }
        });
    }

    private void createChatRoom(String mucjid, String nickName, String password){
        XmppConnectionManager.newInstance().createChatRoom(new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case XmppConnectionFlag.KEY_FRIENDS_SUCCESS:
                        requestData(true);
                        break;
                    case XmppConnectionFlag.KEY_FRIENDS_FAIL:
                        showToast("创建聊天室失败！");
                        break;
                    default:
                        break;
                }
            }
        },mucjid,nickName,password);
    }

}
