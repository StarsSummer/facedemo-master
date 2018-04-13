package com.zeroingin.x.facedemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by 111 on 2018/4/8.
 */

public class AnnoAdapter extends ArrayAdapter<Announcement> {
    private int annoid;

    public AnnoAdapter(Context context, int annoceid, List<Announcement> objects){
        super(context,annoceid,objects);
        annoid = annoceid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup group){
        Announcement anno = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(annoid,null);
        TextView headtext = (TextView) view.findViewById(R.id.anno_head);
        TextView detailtext = (TextView) view.findViewById(R.id.anno_detail);
        headtext.setText(anno.getHeadtext());
        detailtext.setText(anno.getDetailtext());
        return view;
    }

}
