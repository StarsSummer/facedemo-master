package com.zeroingin.x.facedemo;

/**
 * Created by 111 on 2018/4/5.
 */

public class Message {
    private String fromName,message;
    private boolean isSelf;

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public Message(){

    }

    public Message(String fromName,String message,boolean isSelf){
        this.fromName = fromName;
        this.message = message;
        this.isSelf = isSelf;
    }


}
