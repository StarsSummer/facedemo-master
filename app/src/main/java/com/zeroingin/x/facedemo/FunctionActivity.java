package com.zeroingin.x.facedemo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by 111 on 2018/4/5.
 */

public class FunctionActivity extends Activity implements View.OnClickListener{
    private String role;
    private String name;

    private Button btn_kids;
    private Button btn_anno;
    private Button btn_veri;
    private Button btn_self;
    private Button btn_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        Intent roleintent = getIntent();
        role = roleintent.getStringExtra("role");
        name = roleintent.getStringExtra("name");
        Toast.makeText(this,role,Toast.LENGTH_LONG).show();
        changefunc(role);
        View v = this.findViewById(R.id.Button_kids);
        v.setOnClickListener(this);
        v = this.findViewById(R.id.Button_anno);
        v.setOnClickListener(this);
        v = this.findViewById(R.id.Button_veri);
        v.setOnClickListener(this);
        v = this.findViewById(R.id.Button_self);
        v.setOnClickListener(this);
        v = this.findViewById(R.id.Button_chat);
        v.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Button_chat:
                Intent chatintent = new Intent(FunctionActivity.this,ChatActivity.class);
                chatintent.putExtra("name",name);
                startActivity(chatintent);
                break;
            case  R.id.Button_veri:
                Intent camintent = new Intent(FunctionActivity.this,DetecterActivity.class);
                startActivity(camintent);
                break;
            case  R.id.Button_anno:
                Intent annintent = new Intent(FunctionActivity.this,AnnoActivity.class);
                annintent.putExtra("role",role);
                annintent.putExtra("name",name);
                startActivity(annintent);
                break;
            case R.id.Button_self:
                Intent selintent = new Intent(FunctionActivity.this,SelfActivity.class);
                selintent.putExtra("role",role);
                selintent.putExtra("name",name);
                startActivity(selintent);
                break;
            default:
                break;
        }
    }

    protected void changefunc(String role){
        this.role = role;
        btn_kids = (Button) findViewById(R.id.Button_kids);
        btn_anno = (Button) findViewById(R.id.Button_anno);
        btn_veri = (Button) findViewById(R.id.Button_veri);
        if(role.equals("教师")){
            btn_kids.setText("查看接送情况");
            btn_anno.setText("发布公告");
            btn_veri.setText("刷脸验证身份");
        }
        if(role.equals("家长")){
            btn_kids.setText("确认接送情况");
            btn_anno.setText("查看公告");
            btn_veri.setText("增加临时接送人");
        }
    }
}
