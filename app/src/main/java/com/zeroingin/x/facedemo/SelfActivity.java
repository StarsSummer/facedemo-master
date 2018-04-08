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

public class SelfActivity extends Activity implements View.OnClickListener{
    private String role;
    private String name;
    private TextView textView1;
    private TextView textView2;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;

    @Override
    protected void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self);
        Intent roleintent = getIntent();
        role = roleintent.getStringExtra("role");
        name = roleintent.getStringExtra("name");
        changepage();
        View v = this.findViewById(R.id.Button_self1);
        v.setOnClickListener(this);
        v = this.findViewById(R.id.Button_self2);
        v.setOnClickListener(this);
        v = this.findViewById(R.id.Button_self3);
        v.setOnClickListener(this);
        v = this.findViewById(R.id.Button_self4);
        v.setOnClickListener(this);
        v = this.findViewById(R.id.Button_self5);
        v.setOnClickListener(this);
        v = this.findViewById(R.id.Button_self6);
        v.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Button_self1:
                textView1.setText(role +"："+name);
                break;
            case R.id.Button_self2:
                if(role.equals("教师")){
                    textView1.setText("班级信息");
                }
                if(role.equals("家长")){
                    textView1.setText("孩子信息");
                }
                break;
            case R.id.Button_self3:
                System.exit(0);
                break;
            case R.id.Button_self4:
                Toast.makeText(getApplicationContext(),"已是最新版本",Toast.LENGTH_SHORT).show();
            default:
                break;
        }
    }

    private void changepage(){
        textView1 = (TextView) findViewById(R.id.name_parent);
        button2 = (Button) findViewById(R.id.Button_self2);
        button6 = (Button) findViewById(R.id.Button_self6);
        if(role.equals("教师")){
            textView1.setText(role +"："+name);
            button2.setText("班级信息");
            button6.setVisibility(View.INVISIBLE);
            button6.setClickable(false);
        }
        if(role.equals("家长")){
            textView1.setText(role +"："+name);
            button2.setText("孩子信息");
        }
    }
}
