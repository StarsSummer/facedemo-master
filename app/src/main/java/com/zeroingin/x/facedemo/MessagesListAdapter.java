package com.zeroingin.x.facedemo;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 111 on 2018/4/5.
 */

public class MessagesListAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Message> data;
    private static final int MSGTYPE1 = 1;
    private static final int MSGTYPE2 = 2;

    public MessagesListAdapter(Context context){
        this.context = context;
    }

    public void setData(ArrayList<Message> data){
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getNumber();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        switch (viewType){
            case MSGTYPE1:
                View view = LayoutInflater.from(context).inflate(R.layout.list_item_message_left,parent,false);
                holder = new OtherViewHolder(view);
                break;
            case MSGTYPE2:
                View viewo = LayoutInflater.from(context).inflate(R.layout.list_item_message_right,parent,false);
                holder = new SelfViewHolder(viewo);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewtype = getItemViewType(position);
        switch (viewtype){
            case MSGTYPE1:
                OtherViewHolder otherViewHolder = (OtherViewHolder) holder;
                otherViewHolder.nametext.setText(data.get(position).getName());
                otherViewHolder.msgtext.setText(data.get(position).getData());
                break;
            case MSGTYPE2:
                SelfViewHolder selfViewHolder = (SelfViewHolder) holder;
                selfViewHolder.nametext.setText(data.get(position).getName());
                selfViewHolder.msgtext.setText(data.get(position).getData());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return data != null && data.size() > 0 ? data.size() : 0 ;
    }

    class SelfViewHolder extends RecyclerView.ViewHolder{
        private TextView nametext;
        private TextView msgtext;
        public SelfViewHolder(View msgview){
            super(msgview);
            nametext = (TextView) msgview.findViewById(R.id.lblMsgFrom);
            msgtext = (TextView) msgview.findViewById(R.id.txtMsg);
        }
    }

    class OtherViewHolder extends RecyclerView.ViewHolder{
        private TextView nametext;
        private TextView msgtext;
        public OtherViewHolder(View msgview){
            super(msgview);
            nametext = (TextView) msgview.findViewById(R.id.lblMsgFrom);
            msgtext = (TextView) msgview.findViewById(R.id.txtMsg);
        }
    }
}
