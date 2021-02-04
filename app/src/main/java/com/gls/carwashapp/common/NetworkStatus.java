package com.gls.carwashapp.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStatus {

    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 3;

    // getSystemService() 메서드 사용하기위해 context 매개변수 넣음
    public static int getConnectivityStatus(Context context){

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = (NetworkInfo) manager.getActiveNetworkInfo();

        if (networkInfo != null){
            int type = networkInfo.getType(); // 인터넷 연결타입 불러오기
            if (type == ConnectivityManager.TYPE_WIFI){
               return TYPE_WIFI;
            } else if(type == ConnectivityManager.TYPE_MOBILE){
                return TYPE_MOBILE;
            }
        }
        return TYPE_NOT_CONNECTED;
    }
}
