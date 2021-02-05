package com.gls.carwashapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Config implements Serializable { // 데이터 직렬화 상속 : intent 넘기기위해

    private String selfCount;
    private String airCount;
    private String mateCount;
    private String chargerCount;
    private String touchCount;
    private String readerCount;
    private String shopId;
    private String shopPw;
    private String shopName;
    private int managerNo;

    @Override
    public String toString() {
        return "PosConfigVO{" +
                "selfCount='" + selfCount + '\'' +
                ", airCount='" + airCount + '\'' +
                ", mateCount='" + mateCount + '\'' +
                ", chargerCount='" + chargerCount + '\'' +
                ", touchCount='" + touchCount + '\'' +
                ", readerCount='" + readerCount + '\'' +
                ", shopId='" + shopId + '\'' +
                ", shopPw='" + shopPw + '\'' +
                ", shopName='" + shopName + '\'' +
                ", managerNo=" + managerNo +
                '}';
    }

    public int getManagerNo() {
        return managerNo;
    }

    public void setManagerNo(Integer managerNo) {
        this.managerNo = managerNo;
    }

    public String getSelfCount() {
        return selfCount;
    }

    public void setSelfCount(String selfCount) {
        this.selfCount = selfCount;
    }

    public String getAirCount() {
        return airCount;
    }

    public void setAirCount(String airCount) {
        this.airCount = airCount;
    }

    public String getMateCount() {
        return mateCount;
    }

    public void setMateCount(String mateCount) {
        this.mateCount = mateCount;
    }

    public String getChargerCount() {
        return chargerCount;
    }

    public void setChargerCount(String chargerCount) {
        this.chargerCount = chargerCount;
    }

    public String getTouchCount() {
        return touchCount;
    }

    public void setTouchCount(String touchCount) {
        this.touchCount = touchCount;
    }

    public String getReaderCount() {
        return readerCount;
    }

    public void setReaderCount(String readerCount) {
        this.readerCount = readerCount;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopPw() {
        return shopPw;
    }

    public void setShopPw(String shopPw) {
        this.shopPw = shopPw;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
