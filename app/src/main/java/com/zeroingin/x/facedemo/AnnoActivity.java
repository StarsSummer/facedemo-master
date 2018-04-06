package com.zeroingin.x.facedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by 111 on 2018/4/6.
 */

public class AnnoActivity extends Activity {
    private String role;
    private String name;
    private Button button;

    @Override
    protected void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anno);
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
