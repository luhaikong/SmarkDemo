package com.smack.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smack.R;
import com.smack.xmppentity.RoomHosted;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MyPC
 * @date 2018/6/21
 */

public class RoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context cxt;
    private List<RoomHosted> mList;

    private OnItemOnClickListener onItemOnClickListener;

    public interface OnItemOnClickListener{
        void onClick(RoomHosted roomHosted);
    }

    /**
     *
     * @param onItemOnClickListener
     */
    public void setOnItemOnClickListener(OnItemOnClickListener onItemOnClickListener) {
        this.onItemOnClickListener = onItemOnClickListener;
    }

    public RoomAdapter(Context cxt, List<RoomHosted> mList) {
        this.cxt = cxt;
        this.mList = mList==null?new ArrayList<RoomHosted>():mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_hostedroom, parent, false);
        return new VHHostedRoom(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VHHostedRoom vhHostedRoom = (VHHostedRoom) holder;
        final RoomHosted roomHosted = mList.get(position);
        vhHostedRoom.tv_roomName.setText(roomHosted.getName().concat("[").concat(roomHosted.getJid()).concat("]"));
        vhHostedRoom.tv_roomCount.setText(String.valueOf(roomHosted.getCount()));
        if (!roomHosted.getJid().contains("@")){
            vhHostedRoom.iv_roomAvatar.setBackgroundResource(R.mipmap.ic_launcher);
        } else {
            vhHostedRoom.iv_roomAvatar.setBackgroundResource(R.mipmap.ic_launcher_round);
        }
        vhHostedRoom.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemOnClickListener!=null){
                    onItemOnClickListener.onClick(roomHosted);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class VHHostedRoom extends RecyclerView.ViewHolder{

        ImageView iv_roomAvatar;
        TextView tv_roomName;
        TextView tv_roomCount;

        public VHHostedRoom(View itemView) {
            super(itemView);
            iv_roomAvatar = (ImageView) itemView.findViewById(R.id.iv_roomAvatar);
            tv_roomName = (TextView) itemView.findViewById(R.id.tv_roomName);
            tv_roomCount = (TextView) itemView.findViewById(R.id.tv_roomCount);
        }
    }


}
