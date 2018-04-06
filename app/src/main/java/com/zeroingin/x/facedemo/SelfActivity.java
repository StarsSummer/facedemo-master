package com.zeroingin.x.facedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by 111 on 2018/4/6.
 */

public class SelfActivity extends Activity{
    private String role;
    private String name;
    private TextView textView1;
    private TextView textView2;
    private Button button1;
    private Button button2;

    @Override
    protected void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self);
        Intent roleintent = getIntent();
        role = roleintent.getStringExtra("role");
        name = roleintent.getStringExtra("name");
        changepage();
    }

    private void changepage(){
        textView1 = (TextView) findViewById(R.id.name_parent);
        textView2 = (TextView) findViewById(R.id.name_child);
        button1 = (Button) findViewById(R.id.Button_self2);
        button2 = (Button) findViewById(R.id.Button_self6);
        if(role.equals("教师")){
            textView1.setText(role +"："+name);
            textView2.setText("班级：");
            button1.setText("班级信息");
            button2.setVisibility(View.INVISIBLE);
            button2.setClickable(false);
        }
        if(role.equals("家长")){
            textView1.setText(role +"："+name);
            textView2.setText("孩子：");
            button1.setText("孩子信息");
        }
    }
}
