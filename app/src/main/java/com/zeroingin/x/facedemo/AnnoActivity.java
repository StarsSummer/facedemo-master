package com.zeroingin.x.facedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.*;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 111 on 2018/4/6.
 */

public class AnnoActivity extends Activity {
    private String role;
    private String name;
    private Button button;
    private ListView listView;
    private Socket socket;
    private ArrayAdapter adapter;
    private String response;
    private List<Announcement> annolist = new ArrayList<Announcement>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static final int LOAD_BULLETIN=1;
    public String STIME = "2000-01-01T00:00:00";
    private Date lasttime;
    private byte[] anno = new byte[256];

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg){
            if(msg.what==LOAD_BULLETIN){
                listView.setAdapter(adapter);
            }
        }
    };

    @Override
    protected void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anno);


        Intent roleintent = getIntent();
        role = roleintent.getStringExtra("role");
        name = roleintent.getStringExtra("name");
        button = (Button) findViewById(R.id.btnbroad);

        //样式测试
        annolist.add(new Announcement("公告测试","这是一条测试公告！！！这是一条测试公告！！！这是一条测试公告！！！这是一条测试公告！！！这是一条测试公告！！！",STIME,name));
        annolist.add(new Announcement("公告测试","这是一条测试公告！！！这是一条测试公告！！！这是一条测试公告！！！这是一条测试公告！！！这是一条测试公告！！！",STIME,name));

        adapter = new AnnoAdapter(AnnoActivity.this,R.layout.item_anno,annolist);
        listView = (ListView) findViewById(R.id.list_announce);
        listView.setAdapter(adapter);


    }

    @Override
    protected void onStart(){
        super.onStart();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    socket = new Socket("10.206.11.83", 9999);
                    JSONObject obj = new JSONObject();
                    obj.put("opt","bul");
                    anno = obj.toString().getBytes();
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(anno,0,anno.length);
                    outputStream.flush();
                    InputStream inputStream = socket.getInputStream();
                    byte[] buffer = new byte[65536];
                    int len;
                    while ((len = inputStream.read(buffer))!=-1){
                        response = new String(buffer,0,len);
                    }
                    Addbultolist(response);
                    socket.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = LOAD_BULLETIN;
                handler.sendMessage(message);
            }
        }).start();

        if(role.equals("家长"))
            button.setVisibility(View.INVISIBLE);
        button.setClickable(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnnoActivity.this,SendAnnoActivity.class);
                intent.putExtra("name",name);
                startActivity(intent);
            }
        });
    }

    private void Addbultolist(String Data){
        annolist.clear();
        try{
            Date date = new Date();
            lasttime = sdf.parse(STIME);
            JSONArray jsonArray=new JSONArray(Data);
            for(int j=0;j<jsonArray.length();j++) {
                JSONObject jsonObject = jsonArray.getJSONObject(j);
                String time = jsonObject.getString("time");
                date = sdf.parse(time);
                String publisher = jsonObject.getString("publisher");
                String title = jsonObject.getString("title");
                String detail = jsonObject.getString("content");
                if(date.after(lasttime))
                    annolist.add(new Announcement(title,detail,time,publisher));
            }
            lasttime = date;
        }catch (Exception e){}
    }
}
