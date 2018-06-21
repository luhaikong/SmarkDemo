package com.smack.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.smack.ChatActivity;
import com.smack.R;
import com.smack.adapter.RosterAdapter;
import com.smack.xmpp.XmppConnectionFlag;
import com.smack.xmpp.XmppConnectionManager;
import com.smack.xmppentity.GroupFriend;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MyPC
 * @date 2018/6/21
 */

public class FriendFragment extends Fragment {

    public static FriendFragment newInstance(Bundle bundle){
        FriendFragment fragment = new FriendFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView mRecyclerView;
    private RosterAdapter mAdapter;
    private List<GroupFriend> mList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.title_friends));
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_friend,container,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);

        initRecyclerView();

        requestData(true);
        return view;
    }

    public void requestData(boolean isFresh){
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
                        mAdapter = new RosterAdapter(getActivity(),mList);
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

    private void initRecyclerView(){
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),LinearLayoutManager.VERTICAL));
        mAdapter = new RosterAdapter(getActivity(),mList);
        mRecyclerView.setAdapter(mAdapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData(true);
            }
        });
    }

    private void showChatActivity(GroupFriend.ItemFriend friend){
        Intent intent = new Intent(getActivity(),ChatActivity.class);
        intent.putExtra(GroupFriend.ItemFriend.OBJ,friend);
        startActivity(intent);
    }

    public void showToast(String msg){
        Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
    }
}
