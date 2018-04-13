package com.zeroingin.x.facedemo;

/**
 * Created by 111 on 2018/4/13.
 */

public class UserFace {
    private String id;
    private String name;
    private String role;
    private byte[] feature;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public byte[] getFeature() {
        return feature;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setFeature(byte[] feature) {
        this.feature = feature;
    }
}
