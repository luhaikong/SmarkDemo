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

public class HostedRoomFragment extends BaseSmackPushFragment {

    public static HostedRoomFragment newInstance(Bundle bundle){
        HostedRoomFragment fragment = new HostedRoomFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public interface INextInterface{
        void toChatRoom(RoomHosted roomHosted);
    }

    private INextInterface iNextInterface;

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView mRecyclerView;
    private RoomAdapter mAdapter;
    private List<RoomHosted> mList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.title_hostedRoom));
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_room,container,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);

        initRecyclerView();

        requestData(true);

        return view;
    }

    private void requestData(boolean b) {
        XmppConnectionManager.newInstance().getHostedRooms(new Handler(){
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
                                if (iNextInterface!=null){
                                    iNextInterface.toChatRoom(roomHosted);
                                }
                            }

                            @Override
                            public void onLongClick(RoomHosted roomHosted) {

                            }
                        });
                        mRecyclerView.setAdapter(mAdapter);
                        break;
                    case XmppConnectionFlag.KEY_FRIENDS_FAIL:
                        if (refreshLayout.isRefreshing()){
                            refreshLayout.setRefreshing(false);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
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


    private void showDetail(RoomHosted roomHosted){
        XmppConnectionManager.newInstance().getRoomInfo(new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case XmppConnectionFlag.KEY_FRIENDS_SUCCESS:
                        Bundle bundle = msg.getData();
                        RoomMucInfo info = (RoomMucInfo) bundle.getSerializable(XmppConnectionFlag.KEY_FRIENDS_SUCCESS_PARAMS);
                        showAlertDialog("结果",info.toString());
                        break;
                    default:
                        break;
                }
            }
        },roomHosted.getJid());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        iNextInterface = (INextInterface) context;
    }
}
