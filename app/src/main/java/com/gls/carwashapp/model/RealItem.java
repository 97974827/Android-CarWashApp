package com.gls.carwashapp.model;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class RealItem implements Serializable {


    String deviceAddr;
    String connect;
    int resId;
    int deviceType;

    public RealItem(){

    }

    public RealItem(String deviceAddr, String connect) {
        this.deviceAddr = deviceAddr;
        this.connect = connect;
    }

    public RealItem(String deviceAddr, String connect, int resId, int deviceType) {
        this.deviceAddr = deviceAddr;
        this.connect = connect;
        this.resId = resId;
        this.deviceType = deviceType;
    }

    @Override
    public String toString() {
        return "RealItem{" +
                "deviceType=" + deviceType +
                ", deviceAddr='" + deviceAddr + '\'' +
                ", connect='" + connect + '\'' +
                ", resId=" + resId +
                '}';
    }

    public String getDeviceAddr() {
        return deviceAddr;
    }

    public void setDeviceAddr(String deviceAddr) {
        this.deviceAddr = deviceAddr;
    }

    public String getConnect() {
        return connect;
    }

    public void setConnect(String connect) {
        this.connect = connect;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getDeviceType(){
        return deviceType;
    }
}