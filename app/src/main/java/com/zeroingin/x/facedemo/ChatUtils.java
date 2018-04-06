package com.zeroingin.x.facedemo;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 111 on 2018/4/5.
 */

public class ChatUtils {
    private Context context;
    private SharedPreferences sharedPreferences;

    private static final String KEY_SHARED_PREF = "ANDROID_CHAT";
    private static final int KEY_MODE_PRIVATE = 0;
    private static final String KEY_SESSION_ID = "sessionId" , FLAG_MESSAGE ="message";

    public ChatUtils(Context context){
        this.context = context;
        sharedPreferences = this.context.getSharedPreferences(KEY_SHARED_PREF,KEY_MODE_PRIVATE);
    }

    public void storeSessionId(String sessionId){
        SharedPreferences.Editor editor =sharedPreferences.edit();
        editor.putString(KEY_SESSION_ID,sessionId);
        editor.commit();
    }

    public String getSessionId(){
        return sharedPreferences.getString(KEY_SESSION_ID,null);
    }

    public String getSendMessageJSON(String message){
        String json = null;

        try{
            JSONObject jobj = new JSONObject();
            jobj.put("flag",FLAG_MESSAGE);
            jobj.put("sessionId",getSessionId());
            jobj.put("message",message);

            json =  jobj.toString();
        }catch (JSONException e){
            e.printStackTrace();
        }

        return json;
    }


}
