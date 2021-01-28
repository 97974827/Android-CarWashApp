package com.gls.carwashapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gls.carwashapp.common.CustomViewPagerAdapter;
import com.gls.carwashapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


// 선택한 장비 모니터링 액티비티
public class SelectRealActivity extends AppCompatActivity {
    Toolbar toolBar;
    TextView lblDeviceName;

    int deviceType;
    String deviceAddr;

    TableLayout tblDevice;
    TextView trTime; // 남은시간 / 당일충전액
    TextView trCash; // 현금사용액 / 카드 발급수
    TextView tdTime; // 값
    TextView tdCash; // 값

    TextView trCard;  // 카드사용액
    TextView trSales; // 당일매출
    TextView tdCard;  // 값
    TextView tdSales; // 값

    ProgressDialog progressDialog;
    Handler handler = new Handler();

    public static boolean isSelectState = false; // OFF

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_real);

        // 인텐트 객체 데이터 불러오기
        Intent intent = getIntent();
        deviceType = intent.getExtras().getInt("type");
        deviceAddr = intent.getExtras().getString("addr");
        bindView();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                isSelectState = true;
                while (isSelectState) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            selectDeviceState();
                        }
                    });

                    try {
                        Thread.sleep(100);
                    } catch(Exception e) {}
                }
            }

        });
        thread.setDaemon(true);
        thread.start();
    }

    // finish() 호출 시 호출 - 메모리에서 지움
    @Override
    public void onDestroy(){
        super.onDestroy();
        isSelectState = false;
        RealActivity.isState = true;
    }

    @Override
    public void onStop(){
        super.onStop();
        isSelectState = false;
        RealActivity.isState = true;
    }

    // 사용자가 액티비티로 돌아옴
    @Override
    public void onPause(){
        super.onPause();
        isSelectState = false;
        RealActivity.isState = true;
    }

    // 뒤로가기
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void bindView(){
        toolBar = (Toolbar) findViewById(R.id.bar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기버튼, 디폴트 true만 해도 생김
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기존 타이틀 제거

        lblDeviceName = (TextView) findViewById(R.id.lbl_device_name);
        tblDevice = (TableLayout) findViewById(R.id.tbl_device);
        trTime = (TextView) findViewById(R.id.tr_time);
        trCash = (TextView) findViewById(R.id.tr_cash);

        tdTime = (TextView) findViewById(R.id.td_time);
        tdCash = (TextView) findViewById(R.id.td_text_cash);

        trCard = (TextView) findViewById(R.id.tr_card);
        trSales = (TextView) findViewById(R.id.tr_sales);

        tdCard = (TextView) findViewById(R.id.td_text_card);
        tdSales = (TextView) findViewById(R.id.td_sales);

        String tempStr = "";
        switch (deviceType) {
            case 0:
                tempStr = deviceAddr + "번 셀프 세차기";
                break;
            case 1:
                tempStr = deviceAddr + "번 진공 청소기";
                break;
            case 2:
                tempStr = deviceAddr + "번 매트 청소기";
                break;
            case 3:
                tempStr = deviceAddr + "번 카드 충전기";
                trTime.setText("당일 충전");
                trCash.setText("발급 수");
                tdTime.setText("0원");
                tdCash.setText("0장");
                trCard.setVisibility(View.GONE);
                trSales.setVisibility(View.GONE);
                tdCard.setVisibility(View.GONE);
                tdSales.setVisibility(View.GONE);
                break;
            case 6:
                tempStr = deviceAddr + "번 터치 충전기";
                trTime.setText("당일 충전");
                trCash.setText("발급 수");
                tdTime.setText("0원");
                tdCash.setText("0장");
                trCard.setVisibility(View.GONE);
                trSales.setVisibility(View.GONE);
                tdCard.setVisibility(View.GONE);
                tdSales.setVisibility(View.GONE);
                break;
            case 9:
                tempStr = deviceAddr + "번 리더기";
                trTime.setText("사용 시간");
                break;
        }
        lblDeviceName.setText(tempStr);
    }

    public void selectDeviceState(){
        String url = CustomViewPagerAdapter.url + "get_device_state";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) { // 여기에 응답이 떨어짐

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray result = jsonObject.getJSONArray("result");

                            int device_type = 0;
                            String device_addr = "";
                            String useCash = "";
                            String useCard = "";
                            String remainTime = "";
                            String sales = "";
                            String connect = "";
                            String charge = "";
                            int issueCount = 0;

                            // 데이터 파싱
                            for (int i=0; i < result.length(); i++) {
                                JSONObject job= result.getJSONObject(i);  // JSONObject 추출

                                device_type = job.getInt("device_type");
                                device_addr = job.getString("device_addr").substring(1,2);
//                                Log.d("device_type", Integer.toString(device_type));
//                                Log.d("device_addr", device_addr);

                                if (deviceType == device_type && (deviceAddr.equals(device_addr))) {
                                    switch (deviceType) {
                                        case 0:
                                        case 1:
                                        case 2:
                                        case 9:
                                            useCash = job.getString("use_cash");
                                            useCard = job.getString("use_card");
                                            remainTime = job.getString("remain_time");
                                            sales = job.getString("sales");

                                            tdTime.setText(String.format("%,d", Integer.parseInt(remainTime)) + "초");
                                            tdCash.setText(String.format("%,d", Integer.parseInt(useCash)) + "원");
                                            tdCard.setText(String.format("%,d", Integer.parseInt(useCard)) + "원");
                                            tdSales.setText(String.format("%,d", Integer.parseInt(sales)) + "원");

                                            if (Integer.parseInt(remainTime) > 0 && Integer.parseInt(remainTime) > 10){
                                                tdTime.setTextColor(Color.BLUE);
                                            } else if (Integer.parseInt(remainTime) > 0 && Integer.parseInt(remainTime) <= 10){
                                                tdTime.setTextColor(Color.RED);
                                            }
                                            break;
                                        case 3:
                                        case 6:
                                            charge = job.getString("charge");
                                            issueCount = job.getInt("count");
                                            tdTime.setText(String.format("%,d", Integer.parseInt(charge)) + "원");
                                            tdCash.setText(String.format("%,d", issueCount) + "장");
                                            break;
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
        CustomViewPagerAdapter.requestQueue.add(request); // 큐에 넣어줌

    }


}