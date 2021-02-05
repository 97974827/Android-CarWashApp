package com.gls.carwashapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gls.carwashapp.common.CustomViewPagerAdapter;
import com.gls.carwashapp.R;
import com.gls.carwashapp.fragment.AirFragment;
//import com.gls.carwashapp.fragment.ChargerFragment;
import com.gls.carwashapp.fragment.ChargerFragment;
import com.gls.carwashapp.fragment.GarageFragment;
import com.gls.carwashapp.fragment.KioskFragment;
import com.gls.carwashapp.fragment.MateFragment;
//import com.gls.carwashapp.fragment.ReaderFragment;
import com.gls.carwashapp.fragment.ReaderFragment;
import com.gls.carwashapp.fragment.SelfFragment;
import com.gls.carwashapp.fragment.TouchFragment;
import com.gls.carwashapp.model.Config;
import com.gls.carwashapp.common.CustomRecyclerViewAdapter;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


// 실시간 모니터링 엑티비티
public class RealActivity extends AppCompatActivity {

    private static final String TAG = RealActivity.class.getSimpleName();
    ProgressDialog progressDialog;
    Handler handler = new Handler();

    Toolbar toolBar;
    TabLayout tabLayout;
    ViewPager viewPager;

    Config config;
    public static boolean isState = false; // OFF

    int deviceType;
    String deviceAddr;
    String connect;

    CustomViewPagerAdapter adapter; // 뷰페이저 어댑터
    ArrayList<Fragment> fragmentList;

    SelfFragment selfFragment;
    AirFragment airFragment;
    MateFragment mateFragment;
    ChargerFragment chargerFragment;
    ReaderFragment readerFragment;
    TouchFragment touchFragment;
    KioskFragment kioskFragment;
    GarageFragment garageFragment;

    RecyclerView recyclerView; // 리스트
    CustomRecyclerViewAdapter customRecyclerViewAdapter;

    Map<String, Object> resultMap = new LinkedHashMap<>();

//    EventBus selfBus = null;
//    EventBus chargerBus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() 호출됨");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real);

        startThread();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("장비 상태를 불러오는 중입니다....");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
        progressDialog.show();

        bindView(getIntent());
    }

//    // onPause 직전에 호출되는 부분, 강제로 종료되는 상황에 대비하여 액티비티 상태정보 저장
//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        Log.d(TAG, "onSaveInstanceState() 호출됨");
//        outState.putInt("type", deviceType);
//        outState.putString("addr", deviceAddr);
//        outState.putString("connect", connect);
//        Log.d("type", Integer.toString(deviceType));
//        Log.d("addr", deviceAddr);
//        Log.d("connect", connect);
//    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart() 호출됨");
        super.onStart();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                isState = true;
                while (isState) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            getDeviceState();
                            getLanDeviceState();
                        }
                    });
                    try {
                        Thread.sleep(2000);
                    } catch(Exception e) {}
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() 호출됨");
    }

    // 액티비티 돌아왔을때 재시작
    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart() 호출됨");
        SelectRealActivity.isSelectState = false;
        isState = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() 호출됨");
    }

    // 앱 종료 메서드 : back 버튼으로 스레드 중지 안 됬을때 방지
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() 호출됨");
        isState = false; // 스레드 중지
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() 호출됨");
    }

    // 툴바 메뉴가 클릭 되었을때 호출
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch(curId){
            case android.R.id.home: // toolbar의 back키 눌렀을 때 동작
                isState = false; // 스레드 중지
                moveTaskToBack(true);		// 태스크를 백그라운드로 이동
                finishAndRemoveTask();		     	// 액티비티 종료 + 태스크 리스트에서 지우기
                android.os.Process.killProcess(android.os.Process.myPid());	 // 앱 프로세스 종료
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 뷰 바인딩
    public void bindView(Intent intent){
//        config = (Config) intent.getSerializableExtra("config");
        // 툴바 생성
        toolBar = (Toolbar) findViewById(R.id.bar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기버튼, 디폴트 true만 해도 생김
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기존 타이틀 제거

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        fragmentList = new ArrayList<Fragment>(); // 뷰페이저 리스트

        // 프레그먼트 추가
        selfFragment = new SelfFragment();
        fragmentList.add(selfFragment);
        chargerFragment = new ChargerFragment();
        fragmentList.add(chargerFragment);
        touchFragment = new TouchFragment();
        fragmentList.add(touchFragment);
        readerFragment = new ReaderFragment();

        if (LoginActivity.vo.getManagerNo()==1 || LoginActivity.vo.getManagerNo()==4 || LoginActivity.vo.getManagerNo()==2) {
            airFragment = new AirFragment();
            mateFragment = new MateFragment();
            kioskFragment = new KioskFragment();
//            garageFragment = new GarageFragment();
            fragmentList.add(airFragment);
            fragmentList.add(mateFragment);
            fragmentList.add(kioskFragment);
//            fragmentList.add(garageFragment);
        } else if (LoginActivity.vo.getManagerNo()==3 || LoginActivity.vo.getManagerNo()==5) {
            fragmentList.add(readerFragment);
        }

        // 뷰페이저 어댑터 생성
        adapter = new CustomViewPagerAdapter(getSupportFragmentManager(), fragmentList);

        // 탭 메뉴 추가
        tabLayout.addTab(tabLayout.newTab().setText(adapter.getPageTitle(0))); // 셀프
        tabLayout.addTab(tabLayout.newTab().setText(adapter.getPageTitle(3))); // 충전기
        tabLayout.addTab(tabLayout.newTab().setText(adapter.getPageTitle(4))); // 터치

        if (LoginActivity.vo.getManagerNo()==1 || LoginActivity.vo.getManagerNo()==4 || LoginActivity.vo.getManagerNo()==2) {
            tabLayout.addTab(tabLayout.newTab().setText(adapter.getPageTitle(1))); // 진공
            tabLayout.addTab(tabLayout.newTab().setText(adapter.getPageTitle(2))); // 매트
            tabLayout.addTab(tabLayout.newTab().setText(adapter.getPageTitle(5))); // 키오스크
            tabLayout.addTab(tabLayout.newTab().setText(adapter.getPageTitle(7))); // 개러지
//            ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(5).setVisibility(View.GONE); // 탭메뉴 숨기기
        } else if (LoginActivity.vo.getManagerNo()==3 || LoginActivity.vo.getManagerNo()==5) {
            tabLayout.addTab(tabLayout.newTab().setText(adapter.getPageTitle(6))); // 리더기
        }
//        getSupportFragmentManager().beginTransaction().replace(R.id.selfLayout, selfFragment).commit();
        // 뷰 페이저 슬라이드 추가
        viewPager.setAdapter(adapter);
        // 페이지 변화처리
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        // 탭 레이아웃 이벤트 처리
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    // 스레드 시작 요청보내기
    public void startThread(){
        String url = CustomViewPagerAdapter.url + "start_thread";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(getApplicationContext(),
                        "데이터 수집장치가 연결되어 있지 않습니다.",Toast.LENGTH_SHORT).show();
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

        request.setShouldCache(false);
        CustomViewPagerAdapter.requestQueue.add(request);
    }

    // 실시간 모니터링
    public void getDeviceState(){
//        Log.d("getDeviceState", "메인 스레드 재호출");
        String url = CustomViewPagerAdapter.url + "get_device_state";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) { // 여기에 응답이 떨어짐
                        if (progressDialog != null) {
                            progressDialog.dismiss(); // 종료
                        }

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray result = jsonObject.getJSONArray("result");

                            for (int i=0; i < result.length(); i++) {
                                JSONObject job= result.getJSONObject(i);  // JSONObject 추출

                                deviceType = job.getInt("device_type");
                                deviceAddr = job.getString("device_addr");
                                connect = job.getString("connect");

                                // 프래그먼트로 데이터 넘겨줌
                                Bundle bundle = new Bundle();
                                bundle.putInt("type", deviceType);
                                bundle.putString("addr", deviceAddr);
                                bundle.putString("connect", connect);

                                switch (deviceType){
                                    case 0:
                                        selfFragment.setArguments(bundle);
                                        selfFragment.changeUI();
                                        break;
                                    case 1:
                                        airFragment.setArguments(bundle);
                                        airFragment.changeUI();
                                        break;
                                    case 2:
                                        mateFragment.setArguments(bundle);
                                        mateFragment.changeUI();
//                                        EventBus.getDefault().post(new DataEvent(deviceAddr, connect));
                                        break;
                                    case 3:
                                        chargerFragment.setArguments(bundle);
                                        chargerFragment.changeUI();
                                        break;
                                    case 9:
                                        readerFragment.setArguments(bundle);
                                        readerFragment.changeUI();
                                        break;
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
                        Log.d("TAG", "getDeviceState onErrorResponse : " + String.valueOf(error));
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
        CustomViewPagerAdapter.requestQueue.add(request); // 큐에 넣어줌

    }

    // LAN 실시간 모니터링
    public void getLanDeviceState(){
        String url = CustomViewPagerAdapter.url + "get_lan_device_state";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray result = jsonObject.getJSONArray("result");

//                            int deviceType = 0;
//                            String deviceAddr = "";
//                            String connect = "";
                            for (int i=0; i < result.length(); i++) {
                                JSONObject job = result.getJSONObject(i);  // JSONObject 추출

                                deviceType = job.getInt("device_type");
                                deviceAddr = job.getString("device_addr");
                                connect = job.getString("connect");

                                Bundle bundle = new Bundle();
                                bundle.putInt("type", deviceType);
                                bundle.putString("addr", deviceAddr);
                                bundle.putString("connect", connect);

                                switch (deviceType) {
                                    case 6:
                                        touchFragment.setArguments(bundle);
                                        touchFragment.changeUI();
                                        break;
                                    case 7:
                                        break;
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
                    Log.d("TAG", "LanDeviceState ** onErrorResponse : " + String.valueOf(error));
                }
            }){
            // 요청 파라미터 추가
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };

        request.setShouldCache(false); // 이전 결과가 있더라도 새로 요청해서 응답을 보여주게 됨
        CustomViewPagerAdapter.requestQueue.add(request); // 큐에 넣어줌
    }

    /*// 오늘의 매출
    public void getTodaySales(){
        String url = AppHelper.url + "get_today_sales_total";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject result = jsonObject.getJSONObject("result");

//                            "sales": 0,
//                            "income": 0,
//                            "cash_sales": "0",
//                            "card_sales": 0,
//                            "card_charge": 0

                            lblTodaySales.setText(String.format("%,d", result.getInt("sales")) + "원");
                            lblTodayIncome.setText(String.format("%,d", result.getInt("income")) + "원");
                            lblTodayCharge.setText(String.format("%,d", result.getInt("card_charge")) + "원");

                        } catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
//                        Log.d("에러 -> ", error.getMessage());
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
        AppHelper.requestQueue.add(request); // 큐에 넣어줌
    }



    // 장비 갯수 비교
    public int deviceCountCompare(int firstCount, int secondCount){
        if (firstCount > secondCount){
            return firstCount;
        } else if (firstCount < secondCount){
            return secondCount;
        }
        return firstCount; // 같을때
    }


    public static class DataEvent {

        public int deviceType;
        public String deviceAddr;
        public String connect;

        public DataEvent(int deviceType, String deviceAddr, String connect){
            this.deviceType = deviceType;
            this.deviceAddr = deviceAddr;
            this.connect = connect;
        }

        public DataEvent(String deviceAddr, String connect){
            this.deviceAddr = deviceAddr;
            this.connect = connect;
        }
    }*/

}
