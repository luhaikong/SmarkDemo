package com.smack.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.smack.R;
import com.smack.xmpp.XmppRoomConfig;
import com.smack.xmppentity.RoomHosted;

/**
 * Created by Administrator on 2018/6/29.
 */

public class DialogEditRoom extends DialogFragment {

    private EditText et_roomProject;

    private String str_roomProject;

    private RoomHosted mRoomHosted;

    private IonClickListener ionClickListener;
    public interface IonClickListener{
        void onPositiveClick(String mucJid, String subject);

        void onNegativeClick(DialogInterface dialog, int which, String tag);
    }

    public DialogEditRoom setIonClickListener(IonClickListener ionClickListener) {
        this.ionClickListener = ionClickListener;
        return this;
    }

    public static DialogEditRoom newInstance(Bundle bundle){
        DialogEditRoom fragment = new DialogEditRoom();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRoomHosted = getArguments().getSerializable(RoomHosted.OBJ)==null
                ?new RoomHosted(): (RoomHosted) getArguments().getSerializable(RoomHosted.OBJ);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_editroom, null);
        et_roomProject = (EditText) view.findViewById(R.id.et_roomSubject);
        builder.setView(view)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        str_roomProject = et_roomProject.getText().toString();
                        if (str_roomProject.isEmpty()){
                            showToast("请输入房间新主题");
                            return;
                        }
                        if (ionClickListener!=null){
                            ionClickListener.onPositiveClick(mRoomHosted.getJid(),str_roomProject);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (ionClickListener!=null){
                            ionClickListener.onNegativeClick(dialog,which,"DialogEditRoom");
                        }
                    }
                });
        return builder.create();
    }

    public void showToast(String msg){
        Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
    }

}
