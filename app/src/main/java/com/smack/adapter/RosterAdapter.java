package com.smack.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smack.R;
import com.smack.xmppwrap.xmppentity.ItemFriend;

import java.util.List;

/**
 * Created by MyPC on 2018/5/30.
 */

public class RosterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VH_GROUP = 100;
    private static final int VH_ITEM = 200;

    private Context cxt;
    private List<ItemFriend> rosterEntries;

    public RosterAdapter(Context cxt, List<ItemFriend> rosterEntries) {
        this.cxt = cxt;
        this.rosterEntries = rosterEntries;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType==VH_GROUP){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_roster_group, parent, false);
            return new VHGroup(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_roster_item, parent, false);
            return new VHItem(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemFriend entry = rosterEntries.get(position);
        if (holder instanceof VHGroup){
            VHGroup vHGroup = (VHGroup) holder;
            vHGroup.tv_content.setText(entry.getName());
        } else if (holder instanceof VHItem){
            VHItem vHItem = (VHItem) holder;
            vHItem.tv_NickNameToRemark.setText(entry.getName()+"["+entry.getUser()+"]");
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = VH_GROUP;
        if (!rosterEntries.get(position).isGroup()){
            viewType = VH_ITEM;
        }
        return viewType;
    }

    @Override
    public int getItemCount() {
        return rosterEntries.size();
    }

    private class VHGroup extends RecyclerView.ViewHolder{

        ImageView iv_expand;
        TextView tv_content;
        TextView tv_count_online;

        public VHGroup(View itemView) {
            super(itemView);
            iv_expand = (ImageView) itemView.findViewById(R.id.iv_expand);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            tv_count_online = (TextView) itemView.findViewById(R.id.tv_count_online);
        }
    }

    private class VHItem extends RecyclerView.ViewHolder{

        ImageView iv_userAvatar;
        TextView tv_NickNameToRemark;
        TextView tv_presence;

        public VHItem(View itemView) {
            super(itemView);
            iv_userAvatar = (ImageView) itemView.findViewById(R.id.iv_userAvatar);
            tv_NickNameToRemark = (TextView) itemView.findViewById(R.id.tv_NickNameToRemark);
            tv_presence = (TextView) itemView.findViewById(R.id.tv_presence);
        }
    }
}
