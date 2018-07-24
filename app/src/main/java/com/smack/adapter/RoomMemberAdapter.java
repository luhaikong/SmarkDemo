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
import com.smack.xmppentity.RoomMember;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MyPC
 * @date 2018/6/21
 */

public class RoomMemberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context cxt;
    private List<RoomMember> mList;

    private OnItemOnClickListener onItemOnClickListener;

    public interface OnItemOnClickListener{
        void onClick(RoomMember roomMember);

        void onLongClick(RoomMember roomMember);
    }

    /**
     *
     * @param onItemOnClickListener
     */
    public void setOnItemOnClickListener(OnItemOnClickListener onItemOnClickListener) {
        this.onItemOnClickListener = onItemOnClickListener;
    }

    public RoomMemberAdapter(Context cxt, List<RoomMember> mList) {
        this.cxt = cxt;
        this.mList = mList==null?new ArrayList<RoomMember>():mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_roommember, parent, false);
        return new VHRoomMember(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VHRoomMember vhRoomMember = (VHRoomMember) holder;
        final RoomMember roomMember = mList.get(position);
        vhRoomMember.tv_memberJid_Nick.setText(roomMember.getNick().concat("[").concat(roomMember.getJid()).concat("]"));
        vhRoomMember.tv_memberRole.setText(roomMember.getMucAffiliation());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class VHRoomMember extends RecyclerView.ViewHolder{

        ImageView iv_memberAvatar;
        TextView tv_memberJid_Nick;
        TextView tv_memberRole;

        public VHRoomMember(View itemView) {
            super(itemView);
            iv_memberAvatar = (ImageView) itemView.findViewById(R.id.iv_memberAvatar);
            tv_memberJid_Nick = (TextView) itemView.findViewById(R.id.tv_memberJid_Nick);
            tv_memberRole = (TextView) itemView.findViewById(R.id.tv_memberRole);
        }
    }


}
