package com.smack;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.smack.fragment.ChatRoomFragment;
import com.smack.fragment.FriendFragment;
import com.smack.fragment.HostedRoomFragment;
import com.smack.fragment.MessageFragment;
import com.smack.service.SmackPushService;
import com.smack.xmppentity.RoomHosted;

/**
 *
 * @author MyPC
 * @date 2018/6/21
 */

public class MainActivity2 extends BaseSmackPushActivity implements HostedRoomFragment.INextInterface
        ,ChatRoomFragment.IJoinInterface {

    private Bundle mBundle;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_msg:
                    replaceFragment(mBundle,getString(R.string.title_msg),null);
                    return true;
                case R.id.navigation_friends:
                    replaceFragment(mBundle,getString(R.string.title_friends),null);
                    return true;
                case R.id.navigation_chatRoom:
                    replaceFragment(mBundle,getString(R.string.title_hostedRoom),null);
                    return true;
                default:
                    break;
            }
            return false;
        }

    };

    private Context mContext;
    private SmackPushService.SmackPushBinder pushBinder;
    private ServiceConnection pushConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            pushBinder = (SmackPushService.SmackPushBinder) service;
            SmackPushService pushService = pushBinder.getService();
            pushService.addChatListener(MainActivity2.this);
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
            FragmentManager mManager = getSupportFragmentManager();
            switch (msg.what){
                case 1:
                    String body1 = bundle.getString("body");
                    FriendFragment fragment1 = (FriendFragment) mManager.findFragmentByTag("FriendFragment");
                    if (fragment1!=null){
                        fragment1.showToast(body1);
                    }
                    break;
                case 2:
                    String body2 = bundle.getString("body");
                    ChatRoomFragment fragment2 = (ChatRoomFragment) mManager.findFragmentByTag("ChatRoomFragment");
                    if (fragment2!=null){
                        fragment2.showToast(body2);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mBundle = savedInstanceState;
        mContext = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initBottomNavigation();
        initFragment(savedInstanceState,"消息");
    }

    @Override
    public void setTitle(CharSequence title) {
        toolbar.setTitle(title);
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

    private void bindSmackPushService(){
        Intent intent = new Intent(this, SmackPushService.class);
        bindService(intent,pushConn,BIND_AUTO_CREATE);
    }

    private void initBottomNavigation(){
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked}
        };
        int[] colors = new int[]{getResources().getColor(android.R.color.tab_indicator_text),
                getResources().getColor(R.color.colorAccent)
        };
        ColorStateList csl = new ColorStateList(states, colors);
        navigation.setItemTextColor(csl);
        navigation.setItemIconTintList(csl);
    }

    private void initFragment(Bundle savedInstanceState,String title){
        FragmentManager mManager = getSupportFragmentManager();
        FragmentTransaction mTransaction = mManager.beginTransaction();
        switch (title){
            case "消息":
                mTransaction.add(R.id.content
                        , savedInstanceState==null? MessageFragment
                                .newInstance(null):mManager.findFragmentByTag("MessageFragment")
                        , "MessageFragment");
                break;
            case "联系人":
                mTransaction.add(R.id.content
                        , savedInstanceState==null? FriendFragment
                                .newInstance(null):mManager.findFragmentByTag("FriendFragment")
                        , "FriendFragment");
                break;
            case "聊天室":
                mTransaction.add(R.id.content
                        , savedInstanceState==null? HostedRoomFragment
                                .newInstance(null):mManager.findFragmentByTag("HostedRoomFragment")
                        , "HostedRoomFragment");
                break;
            default:
                break;
        }
        mTransaction.commit();
    }

    private void replaceFragment(Bundle savedInstanceState,String title,Bundle data){
        FragmentManager mManager = getSupportFragmentManager();
        FragmentTransaction mTransaction = mManager.beginTransaction();
        switch (title){
            case "消息":
                mTransaction.replace(R.id.content
                        , savedInstanceState==null? MessageFragment
                                .newInstance(data):mManager.findFragmentByTag("MessageFragment")
                        , "MessageFragment");
                break;
            case "联系人":
                mTransaction.replace(R.id.content
                        , savedInstanceState==null? FriendFragment
                                .newInstance(data):mManager.findFragmentByTag("FriendFragment")
                        , "FriendFragment");
                break;
            case "聊天室":
                mTransaction.add(R.id.content
                        , savedInstanceState==null? ChatRoomFragment
                                .newInstance(data):mManager.findFragmentByTag("ChatRoomFragment")
                        , "ChatRoomFragment");
                mTransaction.addToBackStack("ChatRoomFragment");
                break;
            case "聊天服务":
                mTransaction.replace(R.id.content
                        , savedInstanceState==null? HostedRoomFragment
                                .newInstance(data):mManager.findFragmentByTag("HostedRoomFragment")
                        , "HostedRoomFragment");
                break;
            default:
                break;
        }
        mTransaction.commit();
    }

    @Override
    public void chatCreated(String content, boolean createdLocally) {
        Message message = new Message();
        message.what = 1;
        Bundle bundle = new Bundle();
        bundle.putString("body",content);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @Override
    public void processMessage(String content, boolean createdLocally) {
        super.processMessage(content,createdLocally);
        Message message = new Message();
        message.what = 2;
        Bundle bundle = new Bundle();
        bundle.putString("body",content);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @Override
    public void toChatRoom(RoomHosted roomHosted) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(RoomHosted.OBJ,roomHosted);
        replaceFragment(mBundle,getString(R.string.title_chatRoom),bundle);
    }

    @Override
    public void joinChatRoom(RoomHosted roomHosted) {
        SmackPushService service = pushBinder.getService();
        service.addChatListener(this,roomHosted.getJid());

        Intent intent = new Intent(mContext,MultiUserChatActivity.class);
        intent.putExtra(RoomHosted.OBJ,roomHosted);
        startActivity(intent);
    }
}
