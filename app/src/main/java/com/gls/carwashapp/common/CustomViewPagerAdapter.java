package com.gls.carwashapp.common;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.android.volley.RequestQueue;
import com.gls.carwashapp.activity.LoginActivity;
import com.gls.carwashapp.fragment.AirFragment;
import com.gls.carwashapp.fragment.ChargerFragment;
import com.gls.carwashapp.fragment.GarageFragment;
import com.gls.carwashapp.fragment.KioskFragment;
import com.gls.carwashapp.fragment.MateFragment;
import com.gls.carwashapp.fragment.ReaderFragment;
import com.gls.carwashapp.fragment.SelfFragment;
import com.gls.carwashapp.fragment.TouchFragment;

import java.util.ArrayList;
import java.util.List;

// 뷰페이저 어댑터 & common 객체클래스
public class CustomViewPagerAdapter extends FragmentStatePagerAdapter {

    public static RequestQueue requestQueue; // HTTP 응답데이터 객체 큐
    public static String url = "http://glstech.iptime.org:50000/"; // shop IP
    ArrayList<Fragment> fragmentList; // 프래그먼트를 담아줄 리스트

    public CustomViewPagerAdapter(@NonNull FragmentManager fm, ArrayList<Fragment> fragmentList){
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
//        if (object instanceof ){
//
//        }

//        return POSITION_NONE;
        return super.getItemPosition(object);
    }

    public void addItem(Fragment item){
        fragmentList.add(item);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "셀프세차기";
            case 1:
                return "진공청소기";
            case 2:
                return "매트세척기";
            case 3:
                return "카드충전기";
            case 4:
                return "터치충전기";
            case 5:
                return "키오스크";
            case 6:
                return "리더기";
            case 7:
                return "개러지";
            default:
                return null;
        }
    }
}
