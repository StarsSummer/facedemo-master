package com.zeroingin.x.facedemo;

import android.os.Handler;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 111 on 2018/4/5.
 */

public class ChatActivity extends AppCompatActivity {
    private RecyclerView rv;
    private Button btnSend;
    private EditText inputMsg;
    private Socket socket;
    private MessagesListAdapter adapter;
    private ArrayList<Message> listMessages;
    private String name = null;

    @Override
    protected void onCreate(Bundle savedInstacneState) {
        super.onCreate(savedInstacneState);
        setContentView(R.layout.activity_chat);
        btnSend = (Button) findViewById(R.id.btnSend);
        inputMsg = (EditText) findViewById(R.id.inputMsg);
        rv = (RecyclerView) findViewById(R.id.list_view_messages);
        listMessages = new ArrayList<>();
        adapter = new MessagesListAdapter(this);
        final android.os.Handler handler = new MyHandler();

        Intent i = getIntent();
        name = i.getStringExtra("name");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("10.206.11.77", 32100);
                    InputStream inputStream = socket.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        String data = new String(buffer, 0, len);
                        android.os.Message message = android.os.Message.obtain();
                        message.what = 1;
                        message.obj = data;
                        handler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String data = inputMsg.getText().toString();
                inputMsg.setText("");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OutputStream outputStream = socket.getOutputStream();
                            outputStream.write((socket.getLocalPort() + "//" + data + "//" + name).getBytes("utf-8"));
                            outputStream.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(android.os.Message msg){
            super.handleMessage(msg);
            if(msg.what == 1){
                int localPort = socket.getLocalPort();
                String[] split = ((String) msg.obj).split("//");
                if(split[0].equals(localPort + "")){
                    Message message = new Message(split[1],"我",2);
                    listMessages.add(message);
                }else {
                    Message message = new Message(split[1],split[2].toString(),1);
                    listMessages.add(message);
                }

                adapter.setData(listMessages);
                rv.setAdapter(adapter);
                LinearLayoutManager manager = new LinearLayoutManager(ChatActivity.this,LinearLayoutManager.VERTICAL,false);
                rv.setLayoutManager(manager);
            }
        }
    }
}
