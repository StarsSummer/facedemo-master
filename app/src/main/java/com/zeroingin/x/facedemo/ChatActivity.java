package com.zeroingin.x.facedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by 111 on 2018/4/5.
 */

public class ChatActivity extends Activity {
    private static final String TAG = ChatActivity.class.getSimpleName();

    private Button btnSend;
    private EditText inputMsg;
    //private WebSocketClient client;
    private MessagesListAdapter adapter;
    private List<Message> listMessages;
    private ListView listViewMessages;
    private ChatUtils utils;

    private String name = null;
    private static final String TAG_SELF = "self", TAG_NEW = "new", TAG_MESSAGE = "message", TAG_EXIT = "exit";

    @Override
    protected void onCreate(Bundle savedInstacneState){
        super.onCreate(savedInstacneState);
        setContentView(R.layout.activity_chat);
        btnSend = (Button) findViewById(R.id.btnSend);
        inputMsg = (EditText) findViewById(R.id.inputMsg);
        listViewMessages = (ListView) findViewById(R.id.list_view_messages);

        utils = new ChatUtils(getApplicationContext());

        Intent i = getIntent();
        name = i.getStringExtra("naem");

//        btnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendMessageToServer(utils.getSendMessageJSON(inputMsg.getText().toString()));
//                inputMsg.setText("");
//            }
//        });

        listMessages = new ArrayList<Message>();
        adapter = new MessagesListAdapter(this,listMessages);
        listViewMessages.setAdapter(adapter);

//        client = new WebSocketClient(URI.create(WsConfig.URL_WEBSOCKET + URLEncoder.encode(name)),new WebSocketClient.Listener(){
//            @Override
//            public void onConnect(){
//
//            }
//
//            @Override
//            public void onMessage(String message){
//                parseMessage(message);
//            }
//
//            @Override
//            public void onMessage(byte[] data){
//                parseMessage(bytesToHex(data));
//            }
//
//            @Override
//            public void onDisconnect(int code,String reason){
//                String message = String.format(Locale.CHINA,"disconnect!",code,reason);
//                showToast(message);
//                utils.storeSessionId(null);
//            }
//
//            @Override
//            public void onError(Exception error){
//                showToast("Error!");
//            }
//        },null);
//        client.connect();
    }

//    private void sendMessageToServer(String message){
//        if(client!=null && client.isConnected()){
//            client.send(message);
//        }
//    }

    private void parseMessage(final String msg){
        try{
            JSONObject jObj = new JSONObject(msg);
            String flag = jObj.getString("flag");
            if(flag.equalsIgnoreCase(TAG_SELF)){
                String sessionId = jObj.getString("sessionId");
                utils.storeSessionId(sessionId);
            }else if(flag.equalsIgnoreCase(TAG_NEW)){
                String name = jObj.getString("name");
                String message = jObj.getString("message");
                String onlineCount = jObj.getString("onlineCount");
                showToast(name+message+".Currently" + onlineCount + " people online");
            }else if (flag.equalsIgnoreCase(TAG_MESSAGE)) {
                // if the flag is 'message', new message received
                String fromName = name;
                String message = jObj.getString("message");
                String sessionId = jObj.getString("sessionId");
                boolean isSelf = true;

                if (!sessionId.equals(utils.getSessionId())) {
                    fromName = jObj.getString("name");
                    isSelf = false;
                }

                Message m = new Message(fromName, message, isSelf);

                appendMessage(m);

            } else if (flag.equalsIgnoreCase(TAG_EXIT)) {
                String name = jObj.getString("name");
                String message = jObj.getString("message");

                showToast(name + message);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
//
//    @Override
//    protected void onDestroy(){
//        super.onDestroy();
//        if(client != null && client.isConnected()){
//            client.disconnect();
//        }
//    }

    private void appendMessage(final Message m){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listMessages.add(m);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void showToast(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
            }
        });
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
