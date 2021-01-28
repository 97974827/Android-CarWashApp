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


public class SelfFragment extends Fragment {

    RealActivity activity;

    RecyclerView recyclerView; // 리스트
    CustomRecyclerViewAdapter customRecyclerViewAdapter;

    int deviceType;
    String deviceAddr;
    String connect;

    ArrayList<RealItem> selfItemsList;
    int selfCount = Integer.parseInt(LoginActivity.vo.getSelfCount());
    RealItem[] selfItems = new RealItem[selfCount];

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
        View rootView = inflater.inflate(R.layout.fragment_self, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        selfItemsList = new ArrayList<>(); // 리스트 데이터 초기화
        customRecyclerViewAdapter = new CustomRecyclerViewAdapter(getActivity(), selfItemsList);
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
                case 0:
                    for (int i = 0; i < selfCount; i++) {
                        if (connect.equals("1")) {
                            selfItems[i] = new RealItem(Integer.toString(i + 1), "연결됨", R.drawable.real_active, deviceType);
                        } else {
                            selfItems[i] = new RealItem(Integer.toString(i + 1), "연결안됨", R.drawable.real_diable, deviceType);
                        }
                        selfItemsList.add(selfItems[i]);
                        customRecyclerViewAdapter.addItem(selfItems[i]);
                        recyclerView.invalidate();
                        Collections.sort(selfItemsList, cmpAsc);
                    }

                    for (int index = 0; index < selfCount; index++) {
                        selfItemsList.remove(index);
                        customRecyclerViewAdapter.removeItem(index);
                        recyclerView.invalidate();
                    }
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
////    public void onResume() {
////        super.onResume();
////        Log.d("메소드실행", "onResume()");
////        realRecyclerAdapter.notifyDataSetChanged();
////        recyclerView.invalidate();
////    }

//    // eventbus 받는 메소드
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void testEvent(RealActivity.DataEvent event){
////        Log.d("deviceAddr", event.deviceAddr);
////        Log.e("connect", event.connect);
//
//        eventBus = event;
//
//        int selfCount = Integer.parseInt(LoginActivity.vo.getSelfCount());
//
////        Log.d("***deviceType", Integer.toString(eventBus.deviceType));
////        Log.d("***deviceAddr", eventBus.deviceAddr);
////        Log.e("***connect", eventBus.connect);
//
////        realAdapter.deleteItem(RealAdapter.selfItems);
//
////        for (int i = 1; i <= selfCount; i++) {
////            if (eventBus.connect.equals("1")) {
////                realRecyclerAdapter.addItem(new RealItem(Integer.toString(i) + "번", "연결됨", R.drawable.real_active));
////            } else {
////                realRecyclerAdapter.addItem(new RealItem(Integer.toString(i) + "번", "연결안됨", R.drawable.real_diable));
////            }
////        }
//
//    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        try{
//            EventBus.getDefault().unregister(this);
//        }catch (Exception e){}
//    }

    // 실시간 모니터링
    /*public void getDeviceState(){
        String url = TapViewPagerAdapter.url + "get_device_state";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) { // 여기에 응답이 떨어짐
//                        if (progressDialog != null) {
//                            progressDialog.dismiss(); // 종료
//                        }

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray result = jsonObject.getJSONArray("result");

                            for (int i=0; i < result.length(); i++) {
                                JSONObject job= result.getJSONObject(i);  // JSONObject 추출

                                deviceType = job.getInt("device_type");
                                deviceAddr = job.getString("device_addr");
                                connect = job.getString("connect");

//                                Log.d("장비종류", Integer.toString(deviceType));
//                                Log.d("장비번호", deviceAddr);
//                                Log.d("통신상태", connect);

                                switch (deviceType){
                                    case 0:
                                        for (int j=0; j<selfCount; j++) {
                                            if (connect.equals("1")) {
                                                selfItems[j] = new RealItem(Integer.toString(j+1) + "번" , "연결됨", R.drawable.real_active);
                                            } else {
                                                selfItems[j] = new RealItem(Integer.toString(j+1) + "번", "연결안됨", R.drawable.real_diable);
                                            }
                                            realRecyclerAdapter.addItem(selfItems[j]);
                                            selfItemsList.add(selfItems[j]);
                                        }

                                        // UI 변경
                                        realRecyclerAdapter.notifyDataSetChanged();
                                        recyclerView.invalidate();

                                        for (int index=0; index<selfCount; index++) {
                                            Log.d("값 삭제 : " , Integer.toString(index));
                                            selfItemsList.remove(index);
                                            realRecyclerAdapter.removeItem(index);
                                        }
                                        break;
//                                    case 3:
//                                        for (int j=0; j<chargerCount; j++) {
//                                            if (connect.equals("1")) {
//                                                chargerItems[j] = new RealItem(Integer.toString(j+1) + "번" , "연결됨", R.drawable.real_active);
//                                            } else {
//                                                chargerItems[j] = new RealItem(Integer.toString(j+1) + "번", "연결안됨", R.drawable.real_diable);
//                                            }
//                                            realRecyclerAdapter.addItem(chargerItems[j]);
//                                            chargerItemsList.add(chargerItems[j]);
//                                        }
//
//                                        // UI 변경
//                                        realRecyclerAdapter.notifyDataSetChanged();
//                                        recyclerView.invalidate();
//
//                                        for (int index=0; index<chargerCount; index++) {
//                                            Log.d("값 삭제 : " , Integer.toString(index));
//                                            chargerItemsList.remove(index);
//                                            realRecyclerAdapter.removeItem(index);
//                                        }
//                                        break;

                                }
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){

                    }
                }
        ){
            // 요청 파라미터 추가
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };

        request.setShouldCache(false); // 이전 결과가 있더라도 새로 요청해서 응답을 보여주게 됨
        TapViewPagerAdapter.requestQueue.add(request); // 큐에 넣어줌

    }*/


}
