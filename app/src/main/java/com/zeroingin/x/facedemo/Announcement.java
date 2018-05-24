package com.zeroingin.x.facedemo;

/**
 * Created by 111 on 2018/4/8.
 */

public class Announcement {
    private String headtext;
    private String detailtext;
    private String datetext;
    private String nametext;

    public String getDatetext() {
        return datetext;
    }

    public String getNametext() {
        return nametext;
    }

    public String getHeadtext() {
        return headtext;
    }
    public String getDetailtext() {
        return detailtext;
    }

    public Announcement(String head, String detail,String date,String name){
        this.headtext = head;
        this.detailtext = detail;
        this.datetext = date;
        this.nametext = name;
    }
}
