package com.smack.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.smack.R;
import com.smack.xmpp.XmppRoomConfig;

/**
 * Created by Administrator on 2018/6/29.
 */

public class DialogCreateRoom extends DialogFragment {

    private EditText et_roomName;
    private EditText et_roomPw;
    private CheckBox cb_roomForever;
    private CheckBox cb_roomPrivate;

    private String str_roomName;
    private String str_roomPw;
    private boolean bl_roomForever;
    private boolean bl_roomPrivate;

    private IonClickListener ionClickListener;
    public interface IonClickListener{
        void onPositiveClick(XmppRoomConfig config);

        void onNegativeClick(DialogInterface dialog, int which, String tag);
    }

    public DialogCreateRoom setIonClickListener(IonClickListener ionClickListener) {
        this.ionClickListener = ionClickListener;
        return this;
    }

    public static DialogCreateRoom newInstance(Bundle bundle){
        DialogCreateRoom fragment = new DialogCreateRoom();
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_createroom, null);
        et_roomName = (EditText) view.findViewById(R.id.et_roomName);
        et_roomPw = (EditText) view.findViewById(R.id.et_roomPw);
        cb_roomForever = (CheckBox) view.findViewById(R.id.cb_roomForever);
        cb_roomPrivate = (CheckBox) view.findViewById(R.id.cb_roomPrivate);
        builder.setView(view)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        str_roomName = et_roomName.getText().toString();
                        str_roomPw = et_roomPw.getText().toString();
                        bl_roomForever = cb_roomForever.isChecked();
                        bl_roomPrivate = cb_roomPrivate.isChecked();
                        if (str_roomName.isEmpty()){
                            showToast("请输入房间名");
                            return;
                        }
                        if (ionClickListener!=null){
                            XmppRoomConfig.Builder builder1 = new XmppRoomConfig.Builder();
                            builder1.setRoomNick(str_roomName);
                            builder1.setRoomPw(str_roomPw);
                            builder1.setPersistentroom(true);
                            builder1.setMembersonly(false);
                            builder1.setAllowinvites(true);
                            builder1.setEnablelogging(true);
                            builder1.setReservednick(true);
                            builder1.setCanchangenick(false);
                            builder1.setRegistration(false);
                            XmppRoomConfig config = builder1.create();
                            ionClickListener.onPositiveClick(config);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (ionClickListener!=null){
                            ionClickListener.onNegativeClick(dialog,which,"DialogCreateRoom");
                        }
                    }
                });
        return builder.create();
    }

    public void showToast(String msg){
        Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
    }

}
