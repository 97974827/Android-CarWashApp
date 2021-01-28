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

//    // Parcel 객체에서 읽기
//    public PosConfigVO(Parcel in) {
//        selfCount = in.readString();
//        airCount = in.readString();
//        mateCount = in.readString();
//        chargerCount = in.readString();
//        touchCount = in.readString();
//        readerCount = in.readString();
//        shopId = in.readString();
//        shopPw = in.readString();
//        shopName = in.readString();
//        managerNo = in.readInt();
//    }
//
//    // CREATOR 상수 정의
//    public static final Creator<PosConfigVO> CREATOR = new Creator<PosConfigVO>() {
//        @Override
//        public PosConfigVO createFromParcel(Parcel in) {
//            return new PosConfigVO(in); // 생성자 호출해 Parcel 객체읽기
//        }
//
//        @Override
//        public PosConfigVO[] newArray(int size) {
//            return new PosConfigVO[size];
//        }
//    };
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    // Parcel 객체로 쓰기
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeString(selfCount);
//        parcel.writeString(airCount);
//        parcel.writeString(mateCount);
//        parcel.writeString(chargerCount);
//        parcel.writeString(touchCount);
//        parcel.writeString(readerCount);
//        parcel.writeString(shopId);
//        parcel.writeString(shopPw);
//        parcel.writeString(shopName);
//
//        if (managerNo == null) {
//            parcel.writeByte((byte) 0);
//        } else {
//            parcel.writeByte((byte) 1);
//            parcel.writeInt(managerNo);
//        }
//    }

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
