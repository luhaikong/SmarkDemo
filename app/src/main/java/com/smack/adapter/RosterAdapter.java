package com.smack.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smack.R;
import com.smack.xmppentity.GroupFriend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MyPC on 2018/5/30.
 */

public class RosterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    private static final int VH_GROUP = 100;
    private static final int VH_ITEM = 200;

    private Context cxt;
    private List<GroupFriend> rosterEntries;
    private List<Object> items;
    private int index;

    private OnItemOnClickListener onItemOnClickListener;

    @Override
    public void onClick(View v) {
        GroupFriend g = (GroupFriend) items.get(index);
        boolean isExp = g.isExpan();
        g.setExpan(!isExp);
        items.set(index,g);
        if (g.isExpan()){
            for (GroupFriend.ItemFriend item:g.getItemFriends()){
                items.add(item);
            }
        } else {
            items.removeAll(g.getItemFriends());
        }
        notifyDataSetChanged();
    }

    public interface OnItemOnClickListener{
//        void onClick(GroupFriend group);

        void onClick(GroupFriend.ItemFriend friend);
    }

    public void setOnItemOnClickListener(OnItemOnClickListener onItemOnClickListener) {
        this.onItemOnClickListener = onItemOnClickListener;
    }

    public RosterAdapter(Context cxt, List<GroupFriend> rosterEntries) {
        this.cxt = cxt;
        this.rosterEntries = rosterEntries;
        this.items = build(rosterEntries);
    }

    private List<Object> build(List<GroupFriend> rosterEntries){
        List<Object> list = new ArrayList<>();
        if (rosterEntries!=null){
            for (GroupFriend group:rosterEntries){
                list.add(group);
                if (group.isExpan()&&group.getItemFriends()!=null){
                    for (GroupFriend.ItemFriend item:group.getItemFriends()){
                        list.add(item);
                    }
                }
            }
        }
        return list;
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
        if (holder instanceof VHGroup){
            VHGroup vHGroup = (VHGroup) holder;
            GroupFriend g = (GroupFriend) items.get(position);
            vHGroup.tv_content.setText(g.getName());
            index = position;
            vHGroup.itemView.setOnClickListener(this);
        } else if (holder instanceof VHItem){
            VHItem vHItem = (VHItem) holder;
            final GroupFriend.ItemFriend item = (GroupFriend.ItemFriend) items.get(position);
            vHItem.tv_NickNameToRemark.setText(item.getName()+"["+item.getUser()+"]");
            vHItem.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemOnClickListener!=null){
                        onItemOnClickListener.onClick(item);
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = VH_GROUP;
        if (items.get(position) instanceof GroupFriend.ItemFriend){
            viewType = VH_ITEM;
        }
        return viewType;
    }

    @Override
    public int getItemCount() {
        return items.size();
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
