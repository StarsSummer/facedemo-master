package com.zeroingin.x.facedemo;

/**
 * Created by 111 on 2018/4/5.
 */

public class Message {
    private String data;
    private String name;
    private int number;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Message(){

    }

    public Message(String data,String name,int number){
        this.data = data;
        this.name = name;
        this.number = number;
    }


}
