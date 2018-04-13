package com.zeroingin.x.facedemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 111 on 2018/4/8.
 */

public class PickupAdapter extends ArrayAdapter<Pickup> {
    private int pickid;

    public PickupAdapter(Context context, int pickid, List<Pickup> objects){
        super(context,pickid,objects);
        this.pickid = pickid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup group){
        Pickup pick = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(pickid,null);
        TextView timetext = (TextView) view.findViewById(R.id.text_pickuptime);
        TextView detailtext = (TextView) view.findViewById(R.id.text_pickupmes);
        timetext.setText(pick.getDate());
        detailtext.setText(pick.getArrive());
        return view;
    }
}
