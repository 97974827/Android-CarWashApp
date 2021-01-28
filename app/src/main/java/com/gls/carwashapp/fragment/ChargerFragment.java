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


public class ChargerFragment extends Fragment {

    RealActivity activity;

    RecyclerView recyclerView;
    CustomRecyclerViewAdapter customRecyclerViewAdapter;

    int deviceType;
    String deviceAddr;
    String connect;

    ArrayList<RealItem> chargerItemsList;// = new ArrayList<>();
    int chargerCount = Integer.parseInt(LoginActivity.vo.getChargerCount());
    RealItem[] chargerItems = new RealItem[chargerCount];

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

        activity = (RealActivity) getActivity();
        View rootView = inflater.inflate(R.layout.fragment_charger, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        chargerItemsList = new ArrayList<>();
        customRecyclerViewAdapter = new CustomRecyclerViewAdapter(getActivity(), chargerItemsList);
        recyclerView.setAdapter(customRecyclerViewAdapter);
        customRecyclerViewAdapter.setOnItemClickListener(new CustomRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                RealItem item = customRecyclerViewAdapter.getItem(position);
                RealActivity.isState = false; // 스레드 중지

                Intent intent = new Intent(getActivity(), SelectRealActivity.class);
                intent.putExtra("type", item.getDeviceType());
                intent.putExtra("addr", item.getDeviceAddr());
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void changeUI() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            deviceType = bundle.getInt("type");
            deviceAddr = bundle.getString("addr");
            connect = bundle.getString("connect");
        }

        try {
            switch (deviceType) {
                case 3:
                    for (int j=0; j < chargerCount; j++) {
                        if (connect.equals("1")) {
                            chargerItems[j] = new RealItem(Integer.toString(j+1) , "연결됨", R.drawable.real_active, deviceType);
                        } else {
                            chargerItems[j] = new RealItem(Integer.toString(j+1), "연결안됨", R.drawable.real_diable, deviceType);
                        }
                        chargerItemsList.add(chargerItems[j]);
                        customRecyclerViewAdapter.addItem(chargerItems[j]);
                        recyclerView.invalidate();
                        Collections.sort(chargerItemsList, cmpAsc);
                    }

                    for (int index=0; index < chargerCount; index++) {
                        chargerItemsList.remove(index);
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
