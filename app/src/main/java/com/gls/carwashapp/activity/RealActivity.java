package com.gls.carwashapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gls.carwashapp.common.AppHelper;
import com.gls.carwashapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


// 실시간 모니터링 엑티비티
public class RealActivity extends AppCompatActivity {

    ImageButton btnBack; // 종료

    // 실시간
    TextView totalDeviceCount;

    LinearLayout layoutSelf;
    TextView lblSelf;

    LinearLayout layoutAir;
    TextView lblAir;

    LinearLayout layoutMate;
    TextView lblMate;

    LinearLayout layoutCharger;
    TextView lblCharger;

    LinearLayout layoutTouch;
    TextView lblTouch;

    LinearLayout layoutReader;
    TextView lblReader;

    // 매출
    TextView lblTodaySales;
    TextView lblTodayIncome;
    TextView lblTodayCharge;

    Button btnMonthSales;
    Button btnDaySales;

    // 버튼, 텍스트값 초기화
    TextView[] addrList = new TextView[10];
    Integer[] addrArray = {R.id.lbl_addr_1, R.id.lbl_addr_2, R.id.lbl_addr_3, R.id.lbl_addr_4, R.id.lbl_addr_5,
            R.id.lbl_addr_6, R.id.lbl_addr_7, R.id.lbl_addr_8, R.id.lbl_addr_9, R.id.lbl_addr_10};
    Button[] btnSelfList = new Button[10];
    Integer[] selfArray = {R.id.btn_self_1, R.id.btn_self_2, R.id.btn_self_3, R.id.btn_self_4, R.id.btn_self_5,
            R.id.btn_self_6, R.id.btn_self_7, R.id.btn_self_8, R.id.btn_self_9, R.id.btn_self_10};
    Button[] btnAirList = new Button[10];
    Integer[] airArray = {R.id.btn_air_1, R.id.btn_air_2, R.id.btn_air_3, R.id.btn_air_4, R.id.btn_air_5,
            R.id.btn_air_6, R.id.btn_air_7, R.id.btn_air_8, R.id.btn_air_9, R.id.btn_air_10};
    Button[] btnMateList = new Button[10];
    Integer[] mateArray = {R.id.btn_mate_1, R.id.btn_mate_2, R.id.btn_mate_3, R.id.btn_mate_4, R.id.btn_mate_5,
            R.id.btn_mate_6, R.id.btn_mate_7, R.id.btn_mate_8, R.id.btn_mate_9, R.id.btn_mate_10};
    Button[] btnChargerList = new Button[5];
    Integer[] chargerArray = {R.id.btn_charger_1, R.id.btn_charger_2, R.id.btn_charger_3, R.id.btn_charger_4, R.id.btn_charger_5};
    Button[] btnTouchList = new Button[5];
    Integer[] touchArray = {R.id.btn_touch_1, R.id.btn_touch_2, R.id.btn_touch_3, R.id.btn_touch_4, R.id.btn_touch_5};
    Button[] btnReaderList = new Button[10];
    Integer[] readerArray = {R.id.btn_reader_1, R.id.btn_reader_2, R.id.btn_reader_3, R.id.btn_reader_4, R.id.btn_reader_5,
            R.id.btn_reader_6, R.id.btn_reader_7, R.id.btn_reader_8, R.id.btn_reader_9, R.id.btn_reader_10};

    ProgressDialog progressDialog;
    Handler handler = new Handler(); // 핸들러 객체

    public static boolean isState = false; // OFF

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real);
        bindView();
        startThread();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("장비 상태를 불러오는 중입니다....");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
        progressDialog.show();

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
                            getTodaySales();
                        }
                    });

                    try {
                        Thread.sleep(3000);
                    } catch(Exception e) {}
                }
            }

        });
        thread.setDaemon(true);
        thread.start();
    }

    // 액티비티 돌아왔을때 재시작
    @Override
    public void onRestart() {
        super.onRestart();
        SelectRealActivity.isSelectState = false;
        isState = true;
    }

    // 앱 종료 메서드 : back 버튼으로 스레드 중지 안 됬을때 방지
    @Override
    public void onStop() {
        super.onStop();
        isState = false; // 스레드 중지
    }

    public void bindView(){
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isState = false; // 스레드 중지
                moveTaskToBack(true);		// 태스크를 백그라운드로 이동
                finishAndRemoveTask();		     	// 액티비티 종료 + 태스크 리스트에서 지우기
                android.os.Process.killProcess(android.os.Process.myPid());	 // 앱 프로세스 종료
            }
        });

        totalDeviceCount = (TextView) findViewById(R.id.total_device_count);
        for (int i=0; i<addrArray.length; i++){
            addrList[i] = (TextView) findViewById(addrArray[i]);
        }

        layoutSelf = (LinearLayout) findViewById(R.id.layout_self);
        lblSelf = (TextView) findViewById(R.id.lbl_self);
        for (int i=0; i<selfArray.length; i++){
            btnSelfList[i] = (Button) findViewById(selfArray[i]);
        }
        for (int i=0; i<selfArray.length; i++){
            final int cnt = i;
            btnSelfList[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isState = false; // 스레드 중지
                    Intent intent = new Intent(getApplicationContext(), SelectRealActivity.class);
                    intent.putExtra("device_type", 0);
                    intent.putExtra("device_addr", Integer.toString(cnt+1));
                    startActivity(intent);
                }
            });
        }

        layoutAir = (LinearLayout) findViewById(R.id.layout_air);
        lblAir = (TextView) findViewById(R.id.lbl_air);
        for (int i=0; i<airArray.length; i++){
            btnAirList[i] = (Button) findViewById(airArray[i]);
        }
        for (int i=0; i<airArray.length; i++){
            final int cnt = i;
            btnAirList[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isState = false; // 스레드 중지
                    Intent intent = new Intent(getApplicationContext(), SelectRealActivity.class);
                    intent.putExtra("device_type", 1);
                    intent.putExtra("device_addr", Integer.toString(cnt+1));
                    startActivity(intent);
                }
            });
        }

        layoutMate = (LinearLayout) findViewById(R.id.layout_mate);
        lblMate = (TextView) findViewById(R.id.lbl_mate);
        for (int i=0; i<mateArray.length; i++){
            btnMateList[i] = (Button) findViewById(mateArray[i]);
        }
        for (int i=0; i<mateArray.length; i++){
            final int cnt = i;
            btnMateList[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isState = false; // 스레드 중지
                    Intent intent = new Intent(getApplicationContext(), SelectRealActivity.class);
                    intent.putExtra("device_type", 2);
                    intent.putExtra("device_addr", Integer.toString(cnt+1));
                    startActivity(intent);
                }
            });
        }

        layoutCharger = (LinearLayout) findViewById(R.id.layout_charger);
        lblCharger = (TextView) findViewById(R.id.lbl_charger);
        for (int i=0; i<chargerArray.length; i++){
            btnChargerList[i] = (Button) findViewById(chargerArray[i]);
        }
        for (int i=0; i<chargerArray.length; i++){
            final int cnt = i;
            btnChargerList[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isState = false; // 스레드 중지
                    Intent intent = new Intent(getApplicationContext(), SelectRealActivity.class);
                    intent.putExtra("device_type", 3);
                    intent.putExtra("device_addr", Integer.toString(cnt+1));
                    startActivity(intent);
                }
            });
        }

        layoutTouch = (LinearLayout) findViewById(R.id.layout_touch);
        lblTouch = (TextView) findViewById(R.id.lbl_touch);
        for (int i=0; i<touchArray.length; i++){
            btnTouchList[i] = (Button) findViewById(touchArray[i]);
        }
        for (int i=0; i<touchArray.length; i++){
            final int cnt = i;
            btnTouchList[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isState = false; // 스레드 중지
                    Intent intent = new Intent(getApplicationContext(), SelectRealActivity.class);
                    intent.putExtra("device_type", 6);
                    intent.putExtra("device_addr", Integer.toString(cnt+1));
                    startActivity(intent);
                }
            });
        }

        layoutReader = (LinearLayout) findViewById(R.id.layout_reader);
        lblReader = (TextView) findViewById(R.id.lbl_reader);
        for (int i=0; i<readerArray.length; i++){
            btnReaderList[i] = (Button) findViewById(readerArray[i]);
        }
        for (int i=0; i<readerArray.length; i++){
            final int cnt = i;
            btnReaderList[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isState = false; // 스레드 중지
                    Intent intent = new Intent(getApplicationContext(), SelectRealActivity.class);
                    intent.putExtra("device_type", 9);
                    intent.putExtra("device_addr", Integer.toString(cnt+1));
                    startActivity(intent);
                }
            });
        }

        lblTodaySales = (TextView) findViewById(R.id.lbl_today_sales);
        lblTodayIncome = (TextView) findViewById(R.id.lbl_today_income);
        lblTodayCharge = (TextView) findViewById(R.id.lbl_today_charge);

        btnMonthSales = (Button) findViewById(R.id.btn_month_sales);
        btnMonthSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isState = false; // 스레드 중지
                Intent intent = new Intent(getApplicationContext(), MonthSalesActivity.class);
                startActivity(intent);
            }
        });

        btnDaySales = (Button) findViewById(R.id.btn_day_sales);
        btnDaySales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isState = false; // 스레드 중지
                Intent intent = new Intent(getApplicationContext(), DaySalesActivity.class);
                startActivity(intent);
            }
        });
    }

    // 스레드 시작 요청보내기
    public void startThread(){
        String url = AppHelper.url + "start_thread";

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
        AppHelper.requestQueue.add(request);
    }

    // 실시간 모니터링
    public void getDeviceState(){
        String url = AppHelper.url + "get_device_state";

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
                            if (LoginActivity.vo.getManagerNo()==1 || LoginActivity.vo.getManagerNo()==4 || LoginActivity.vo.getManagerNo()==2) { // 길광
                                layoutReader.setVisibility(View.GONE); // 자리도 차지하지말고 표시도 하지말기
                            } else if (LoginActivity.vo.getManagerNo()==3 || LoginActivity.vo.getManagerNo()==5) { // 대진
                                layoutAir.setVisibility(View.GONE);
                                layoutMate.setVisibility(View.GONE);
                            }

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray result = jsonObject.getJSONArray("result");

                            int deviceType = 0;
                            String deviceAddr = "";
                            String connect = "";

                            // 장비갯수 가져오기
                            int selfCount = Integer.parseInt(LoginActivity.vo.getSelfCount());
                            int airCount = Integer.parseInt(LoginActivity.vo.getAirCount());
                            int mateCount = Integer.parseInt(LoginActivity.vo.getMateCount());
                            int chargerCount = Integer.parseInt(LoginActivity.vo.getChargerCount());
                            int touchCount = Integer.parseInt(LoginActivity.vo.getTouchCount());
                            int readerCount = Integer.parseInt(LoginActivity.vo.getReaderCount());

                            int total = selfCount + airCount + mateCount + chargerCount + touchCount + readerCount;
                            String str = "전체 장비 갯수 : " + Integer.toString(total) + " 개";
                            totalDeviceCount.setText(str);

                            // 장비 갯수 비교
                            int tempCount = 0;
                            tempCount = deviceCountCompare(selfCount, airCount);
                            tempCount = deviceCountCompare(tempCount, mateCount);
                            tempCount = deviceCountCompare(tempCount, chargerCount);
                            tempCount = deviceCountCompare(tempCount, touchCount);
                            tempCount = deviceCountCompare(tempCount, readerCount);
                            for (int i=tempCount; i<10; i++) {
                                addrList[i].setVisibility(View.INVISIBLE);
                            }


                            for (int i=0; i < result.length(); i++) {
                                JSONObject job= result.getJSONObject(i);  // JSONObject 추출

                                deviceType = job.getInt("device_type");
                                deviceAddr = job.getString("device_addr");
                                connect = job.getString("connect");

//                                Log.d("=============== 파싱 데이터 ", str);
//                                Log.d("장비종류", Integer.toString(deviceType));
//                                Log.d("장비번호", deviceAddr);
//                                Log.d("통신상태", connect);

                                switch (deviceType){
                                    case 0:
                                        for (int j = 0; j < selfCount; j++) {
                                            btnSelfList[j].setVisibility(View.VISIBLE);
                                            if (connect.equals("1")) {
                                                btnSelfList[j].setText("연결");
                                                btnSelfList[j].setBackgroundResource(R.color.on);
                                            } else {
                                                btnSelfList[j].setText("끊김");
                                                btnSelfList[j].setBackgroundResource(R.color.off);
                                            }
                                        }
                                        break;
                                    case 1:
                                        if (LoginActivity.vo.getManagerNo()==1 || LoginActivity.vo.getManagerNo()==4 || LoginActivity.vo.getManagerNo()==2) {
                                            for (int j = 0; j < airCount; j++) {
                                                btnAirList[j].setVisibility(View.VISIBLE);
                                                if (connect.equals("1")) {
                                                    btnAirList[j].setText("연결");
                                                    btnAirList[j].setBackgroundResource(R.color.on);
                                                } else {
                                                    btnAirList[j].setText("끊김");
                                                    btnAirList[j].setBackgroundResource(R.color.off);
                                                }
                                            }
                                        }
                                        break;
                                    case 2:
                                        if (LoginActivity.vo.getManagerNo()==1 || LoginActivity.vo.getManagerNo()==4 || LoginActivity.vo.getManagerNo()==2) {
                                            for (int j = 0; j < mateCount; j++) {
                                                btnMateList[j].setVisibility(View.VISIBLE);
                                                if (connect.equals("1")) {
                                                    btnMateList[j].setText("연결");
                                                    btnMateList[j].setBackgroundResource(R.color.on);
                                                } else {
                                                    btnMateList[j].setText("끊김");
                                                    btnMateList[j].setBackgroundResource(R.color.off);
                                                }
                                            }
                                        }
                                        break;
                                    case 3:
                                        for (int j = 0; j < chargerCount; j++) {
                                            btnChargerList[j].setVisibility(View.VISIBLE);
                                            if (connect.equals("1")) {
                                                btnChargerList[j].setText("연결");
                                                btnChargerList[j].setBackgroundResource(R.color.on);
                                            } else {
                                                btnChargerList[j].setText("끊김");
                                                btnChargerList[j].setBackgroundResource(R.color.off);
                                            }
                                        }
                                        break;
                                    case 9:
                                        if (LoginActivity.vo.getManagerNo()==3 || LoginActivity.vo.getManagerNo()==5) {
                                            for (int j = 0; j < readerCount; j++) {
                                                btnReaderList[j].setVisibility(View.VISIBLE);
                                                if (connect.equals("1")) {
                                                    btnReaderList[j].setText("연결");
                                                    btnReaderList[j].setBackgroundResource(R.color.on);
                                                } else {
                                                    btnReaderList[j].setText("끊김");
                                                    btnReaderList[j].setBackgroundResource(R.color.off);
                                                }
                                            }
                                        }
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

    // 오늘의 매출
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

    // LAN 실시간 모니터링
    public void getLanDeviceState(){
        String url = AppHelper.url + "get_lan_device_state";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray result = jsonObject.getJSONArray("result");

                            int deviceType = 0;
                            String deviceAddr = "";
                            String connect = "";

                            // 장비갯수 가져오기
                            int touchCount = Integer.parseInt(LoginActivity.vo.getTouchCount());

                            for (int i=0; i < result.length(); i++) {
                                JSONObject job= result.getJSONObject(i);  // JSONObject 추출

                                deviceType = job.getInt("device_type");
                                deviceAddr = job.getString("device_addr");
                                connect = job.getString("connect");

                                switch (deviceType){
                                    case 6:
                                        for (int j = 0; j < touchCount; j++) {
                                            btnTouchList[j].setVisibility(View.VISIBLE);
                                            if (connect.equals("1")) {
                                                btnTouchList[j].setText("연결");
                                                btnTouchList[j].setBackgroundResource(R.color.on);
                                            } else {
                                                btnTouchList[j].setText("끊김");
                                                btnTouchList[j].setBackgroundResource(R.color.off);
                                            }
                                        }
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

}