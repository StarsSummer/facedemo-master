package com.zeroingin.x.facedemo;

import java.util.Date;

/**
 * Created by 111 on 2018/4/8.
 */

public class Pickup {
    private String date;
    private String arrive;
    private int lora;

    public Pickup(String date ,String arrive,int lora){
        this.date = date;
        this.arrive = arrive;
        this.lora = lora;
    }

    public String getDate() {
        return date;
    }

    public String getArrive() {
        return arrive;
    }

    public int getLora() {
        return lora;
    }

}
