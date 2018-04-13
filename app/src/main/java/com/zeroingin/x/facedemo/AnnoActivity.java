package com.zeroingin.x.facedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 111 on 2018/4/6.
 */

public class AnnoActivity extends Activity {
    private String role;
    private String name;
    private Button button;

    private List<Announcement> annolist = new ArrayList<Announcement>();

    @Override
    protected void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anno);

        //样式测试
        annolist.add(new Announcement("公告测试","这是一条测试公告！！！这是一条测试公告！！！这是一条测试公告！！！这是一条测试公告！！！这是一条测试公告！！！"));
        ArrayAdapter adapter = new AnnoAdapter(AnnoActivity.this,R.layout.item_anno,annolist);
        ListView listView = (ListView) findViewById(R.id.list_announce);
        listView.setAdapter(adapter);

        Intent roleintent = getIntent();
        role = roleintent.getStringExtra("role");
        name = roleintent.getStringExtra("name");
        button = (Button) findViewById(R.id.btnbroad);
        if(role.equals("家长"))
            button.setVisibility(View.INVISIBLE);
            button.setClickable(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"发布成功",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
