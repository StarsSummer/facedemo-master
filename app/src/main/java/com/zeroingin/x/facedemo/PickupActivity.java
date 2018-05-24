package com.zeroingin.x.facedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by 111 on 2018/4/8.
 */

public class PickupActivity extends Activity{
    private String role;
    private String name;
    private Calendar calendar = Calendar.getInstance();
    public String time;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Pickup> picklist = new ArrayList<Pickup>();
    private ArrayAdapter adapter;

    private Handler mhandler = new Handler(){
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case 777:
                    picklist.add(0,new Pickup(time,"您的孩子下落不明",0));
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);


            }
        };
    };

    @Override
    protected void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup);

        Intent roleintent = getIntent();
        role = roleintent.getStringExtra("role");
        name = roleintent.getStringExtra("name");

        if(role.equals("家长")){
            //样式测试
            time = calendar.get(Calendar.MONTH) + "." + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
            for(int p=0;p<15;p++){
                picklist.add(0,new Pickup(time,"您的孩子已抵达学校",0));
                picklist.add(0,new Pickup(time,"您的孩子已被接走",0));
            }

            adapter = new PickupAdapter(PickupActivity.this,R.layout.item_child_arrive,picklist);
            ListView listView = (ListView) findViewById(R.id.list_child_arrive);
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mhandler.sendEmptyMessageDelayed(777,777);
                }
            });
            listView.setAdapter(adapter);
        }



    }


}
