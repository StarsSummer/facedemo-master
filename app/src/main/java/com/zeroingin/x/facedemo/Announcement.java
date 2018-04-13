package com.zeroingin.x.facedemo;

/**
 * Created by 111 on 2018/4/8.
 */

public class Announcement {
    private String headtext;
    private String detailtext;

    public String getHeadtext() {
        return headtext;
    }
    public String getDetailtext() {
        return detailtext;
    }

    public Announcement(String head, String detail){
        this.headtext = head;
        this.detailtext = detail;
    }
}
