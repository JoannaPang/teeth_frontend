package com.example.myapplication.entity;

import java.io.Serializable;

public class BundleData implements Serializable{

    private Object data;

    public BundleData() {
    }

    public Object getData(){
        return data;
    }

    public BundleData(Object data) {
        this.data = data;
    }
}
