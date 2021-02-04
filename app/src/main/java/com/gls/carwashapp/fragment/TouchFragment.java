package com.gls.carwashapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gls.carwashapp.R;
import com.gls.carwashapp.activity.LoginActivity;
import com.gls.carwashapp.activity.RealActivity;
import com.gls.carwashapp.activity.SelectRealActivity;
import com.gls.carwashapp.common.CustomRecyclerViewAdapter;
import com.gls.carwashapp.model.RealItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class TouchFragment extends Fragment {

    RealActivity activity;

    RecyclerView recyclerView; // 리스트
    CustomRecyclerViewAdapter customRecyclerViewAdapter;

    int deviceType;
    String deviceAddr;
    String connect;

    ArrayList<RealItem> touchItemsList;
    int touchCount;
    RealItem[] touchItems;

    Comparator<RealItem> cmpAsc = new Comparator<RealItem>() {
        @Override
        public int compare(RealItem o1, RealItem o2) {
        if (Integer.parseInt(o1.getDeviceAddr()) > Integer.parseInt(o2.getDeviceAddr())){
            return 1;
        } else if (Integer.parseInt(o1.getDeviceAddr()) < Integer.parseInt(o2.getDeviceAddr())){
            return -1;
        } else {
            return 0;
        }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState==null) {
        } else {
            deviceType = savedInstanceState.getInt("type");
            deviceAddr = savedInstanceState.getString("addr");
            connect = savedInstanceState.getString("connect");
        }
        try {
            touchCount = Integer.parseInt(LoginActivity.vo.getTouchCount());
            touchItems = new RealItem[touchCount];
        } catch(Exception e) {e.printStackTrace();}

        activity = (RealActivity) getActivity();
        View rootView = inflater.inflate(R.layout.fragment_touch, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        touchItemsList = new ArrayList<>(); // 리스트 데이터 초기화
        customRecyclerViewAdapter = new CustomRecyclerViewAdapter(getActivity(), touchItemsList);
        recyclerView.setAdapter(customRecyclerViewAdapter);

        customRecyclerViewAdapter.setOnItemClickListener(new CustomRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                RealItem item = customRecyclerViewAdapter.getItem(position); // 선택된 아이템 객체
                RealActivity.isState = false; // 스레드 중지

                // 프레그먼트에서 getActivity() 호출시 다른 액티비티로 데이터 전달 가능
                Intent intent = new Intent(getActivity(), SelectRealActivity.class);
                intent.putExtra("type", item.getDeviceType());
                intent.putExtra("addr", item.getDeviceAddr());
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void changeUI() {
        // 액티비티 - 프래그먼트 데이터 받아오기
        Bundle bundle = getArguments();
        if (bundle != null) {
            deviceType = bundle.getInt("type");
            deviceAddr = bundle.getString("addr");
            connect = bundle.getString("connect");
        }

        try {
            switch (deviceType) {
                case 6:
                    for (int i = 0; i < touchCount; i++) {
                        if (connect.equals("1")) {
                            touchItems[i] = new RealItem(Integer.toString(i + 1), "연결됨", R.drawable.real_active, deviceType);
                        } else {
                            touchItems[i] = new RealItem(Integer.toString(i + 1), "연결안됨", R.drawable.real_diable, deviceType);
                        }
                        touchItemsList.add(touchItems[i]);
                        customRecyclerViewAdapter.addItem(touchItems[i]);
                        recyclerView.invalidate();
                        Collections.sort(touchItemsList, cmpAsc);
                    }

                    for (int index = 0; index < touchCount; index++) {
                        touchItemsList.remove(index);
                        customRecyclerViewAdapter.removeItem(index);
                        recyclerView.invalidate();
                    }
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
