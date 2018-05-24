package com.zeroingin.x.facedemo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.*;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.BoringLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.net.Socket;
import java.util.Calendar;

/**
 * Created by 111 on 2018/5/23.
 */

public class SendAnnoActivity extends Activity {
    private String name;
    private Button button;
    private EditText titleedit;
    private EditText detailedit;
    private ProgressBar progressBar;
    private Socket socket;
    int status  = 0;
    private String titletext,detailtext;
    private byte[] anno = new byte[65536];

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anno_send);

        Intent nameintent = getIntent();
        name = nameintent.getStringExtra("name");

        button = (Button) findViewById(R.id.btn_anno_sure);
        titleedit = (EditText) findViewById(R.id.send_anno_title);
        detailedit = (EditText) findViewById(R.id.send_anno_detail);
        progressBar = (ProgressBar) findViewById(R.id.anno_pro);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titletext = titleedit.getText().toString();
                detailtext = detailedit.getText().toString();
                new SendAnnoTask().execute();
            }
        });

    }

    class SendAnnoTask extends AsyncTask<Void,Integer,Boolean>{
        @Override
        protected Boolean doInBackground(Void... params){
            JSONObject obj = new JSONObject();
            int temp;
            try{
                obj.put("opt","ann");
                obj.put("title",titletext);
                obj.put("detail",detailtext);
                anno = obj.toString().getBytes();
                socket = new Socket("10.206.11.83", 9999);
                Log.i("info","Server connected!");
                OutputStream outputStream = socket.getOutputStream();
                int len = anno.length;
                temp = len;
                outputStream.write(anno,0,temp);
                outputStream.flush();
                Log.i("info","SQL sended");
                publishProgress((int)temp/len*100);
            }catch (Exception e){
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values){
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean result){
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(),"发布成功",Toast.LENGTH_SHORT).show();
            Intent back = new Intent(SendAnnoActivity.this,AnnoActivity.class);
            startActivity(back);
        }
    }

}
