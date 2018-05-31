package com.smack.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smack.R;
import com.smack.entity.XmppMessage;
import com.smack.utils.TimeUtil;

import java.util.List;

/**
 * Created by MyPC on 2018/5/30.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VY_TXT_LEFT = 100;
    private static final int VY_TXT_RIGHT = 200;

    private Context cxt;
    private List<XmppMessage> xmppMessages;

    public ChatAdapter(Context cxt, List<XmppMessage> xmppMessages) {
        this.cxt = cxt;
        this.xmppMessages = xmppMessages;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType==VY_TXT_LEFT){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_chat_left_text, parent, false);
            return new VHChatTextViewLeft(view);
        } else{
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_chat_right_text, parent, false);
            return new VHChatTextViewRight(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        XmppMessage message = xmppMessages.get(position);
        if (holder instanceof VHChatTextViewLeft){
            VHChatTextViewLeft viewLeft = (VHChatTextViewLeft) holder;
            viewLeft.tv_time_receive.setText(TimeUtil.long2yyMMddhhmmss(message.getCreateTime()));
            viewLeft.tv_content.setText(message.getBody());
        } else {
            VHChatTextViewRight viewRight = (VHChatTextViewRight) holder;
            viewRight.tv_time_receive.setText(TimeUtil.long2yyMMddhhmmss(message.getCreateTime()));
            viewRight.tv_content.setText(message.getBody());
        }
    }

    @Override
    public int getItemCount() {
        return xmppMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        int viewtype = VY_TXT_LEFT;
        if (xmppMessages.get(position).getDirect()== XmppMessage.DIRECT_IN){
            viewtype = VY_TXT_LEFT;
        } else {
            viewtype = VY_TXT_RIGHT;
        }
        return viewtype;
    }

    private class VHChatTextViewLeft extends RecyclerView.ViewHolder {

        ImageView iv_friend_avatar;
        TextView tv_time_receive;
        TextView tv_content;
        ImageView iv_fail;
        ProgressBar pb_progress;

        private VHChatTextViewLeft(View itemView) {
            super(itemView);
            iv_friend_avatar = (ImageView) itemView.findViewById(R.id.iv_friend_avatar);
            iv_fail = (ImageView) itemView.findViewById(R.id.iv_fail);
            tv_time_receive = (TextView) itemView.findViewById(R.id.tv_time_receive);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            pb_progress = (ProgressBar) itemView.findViewById(R.id.pb_progress);
        }
    }

    private class VHChatTextViewRight extends RecyclerView.ViewHolder {

        ImageView iv_myself_avatar;
        TextView tv_time_receive;
        TextView tv_content;
        ImageView iv_fail;
        ProgressBar pb_progress;

        private VHChatTextViewRight(View itemView) {
            super(itemView);
            iv_myself_avatar = (ImageView) itemView.findViewById(R.id.iv_myself_avatar);
            iv_fail = (ImageView) itemView.findViewById(R.id.iv_fail);
            tv_time_receive = (TextView) itemView.findViewById(R.id.tv_time_receive);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            pb_progress = (ProgressBar) itemView.findViewById(R.id.pb_progress);
        }
    }

}
