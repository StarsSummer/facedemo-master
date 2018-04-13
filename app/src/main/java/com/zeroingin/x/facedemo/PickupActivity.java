package com.zeroingin.x.facedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

    private List<Pickup> picklist = new ArrayList<Pickup>();

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
            picklist.add(new Pickup(time,"您的孩子已抵达学校",0));
            ArrayAdapter adapter = new PickupAdapter(PickupActivity.this,R.layout.item_child_arrive,picklist);
            ListView listView = (ListView) findViewById(R.id.list_child_arrive);
            listView.setAdapter(adapter);
        }

    }
}
