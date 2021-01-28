package com.gls.carwashapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gls.carwashapp.common.CustomViewPagerAdapter;
import com.gls.carwashapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class DaySalesActivity extends AppCompatActivity {

    int year;
    int month;
    int days;

    TextView lblDate;
    TableLayout tableDays;

    ImageButton btnBack;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_sales);

        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        days = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, listener, year, month, days);
        dialog.show();

        lblDate = (TextView) findViewById(R.id.lbl_date);
        tableDays = (TableLayout) findViewById(R.id.table_days);

        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RealActivity.class);
                startActivity(intent);
            }
        });
    }


    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Toast.makeText(getApplicationContext(), year + "년" + (monthOfYear+1) + "월" + dayOfMonth + "일", Toast.LENGTH_SHORT).show();
            getDaySales(year, monthOfYear+1, dayOfMonth);
        }
    };

    // 일별 매출조회
    public void getDaySales(int y, int m, int d){
        String url = CustomViewPagerAdapter.url + "get_days_sales";
        year = y;
        month = m;
        days = d;
        lblDate.setText(year + " 년 " + month + " 월 " + days + " 일 ");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("매출정보를 불러오는 중입니다....");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
        progressDialog.show();

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) { // 여기에 응답이 떨어짐
                        progressDialog.dismiss(); // 종료

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray result = jsonObject.getJSONArray("result");

//                            Log.d("Days data ??????  ", response);

                            String endTime = "";
                            String deviceName = "";
                            int cash = 0;
                            int card = 0;

                            List<String> endTimeArray = new ArrayList<>();
                            List<String> deviceNameArray = new ArrayList<>();
                            List<Integer> cashArray = new ArrayList<>();
                            List<Integer> cardArray = new ArrayList<>();

                            // 종료시간, 장비명, 현금, 카드 내역 추출
                            for (int i=0; i < result.length(); i++) {
                                JSONObject job = result.getJSONObject(i);  // JSONObject 추출

                                endTime = changeTimestamp(Long.parseLong(job.getString("end_time")), "HH:ss");
                                deviceName = job.getString("device_name");
                                cash = job.getInt("cash");
                                card = job.getInt("card");

                                endTimeArray.add(endTime);
                                deviceNameArray.add(deviceName);
                                cashArray.add(cash);
                                cardArray.add(card);
                            }

                            tableGrid(result.length(), 4, endTimeArray, deviceNameArray, cashArray, cardArray);

                        } catch (Exception e){
                            e.printStackTrace();
                        }

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
                params.put("year", Integer.toString(year));
                params.put("month", Integer.toString(month));
                params.put("days", Integer.toString(days));
                Log.d("year", Integer.toString(year));
                Log.d("month", Integer.toString(month));
                Log.d("days", Integer.toString(days));
                return params;
            }
        };

        request.setShouldCache(false); // 이전 결과가 있더라도 새로 요청해서 응답을 보여주게 됨
        CustomViewPagerAdapter.requestQueue.add(request); // 큐에 넣어줌
    }

    // 테이블 동적 생성하기
    public void tableGrid(int trCt, int tdCt, List<String> time, List<String> device, List<Integer> cash, List<Integer> card) {

        TableRow.LayoutParams rowLayout = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableRow[] row = new TableRow[trCt+1];     // 테이블 ROW 생성
        TextView[][] text = new TextView[trCt+1][tdCt+1];   // 데이터

        for (int tr = 0; tr < trCt+1; tr++) {         // 행
            row[tr] = new TableRow(this);
//            row[tr].setBackgroundColor(Color.BLACK);

            for (int td = 0; td < tdCt+1; td++) {     // 열
                text[tr][td] = new TextView(this);

                if (tr == 0) {
                    text[tr][td].setBackgroundResource(R.color.sales);
                    if (td == 0) {
                        text[tr][td].setText("시간");
                    } else if (td == 1) {
                        text[tr][td].setText("장비명");
                    } else if (td == 2) {
                        text[tr][td].setText("현금");
                    } else if (td == 3) {
                        text[tr][td].setText("카드");
                    }
                } else {
                    text[tr][td].setBackgroundResource(R.drawable.table_border);
                    if (td == 0) {
                        text[tr][td].setTextColor(Color.BLACK);
                        text[tr][td].setText(time.get(tr-1));
                    } else if (td == 1) {
                        text[tr][td].setText(device.get(tr-1));
                    } else if (td == 2) {
                        text[tr][td].setText(String.format("%,d", cash.get(tr-1)));
                    } else if (td == 3) {
                        text[tr][td].setText(String.format("%,d", card.get(tr-1)));
                    }
                }

                text[tr][td].setTextSize(24);
                text[tr][td].setGravity(Gravity.CENTER);
                row[tr].addView(text[tr][td]);
            } // td for end

            tableDays.addView(row[tr], rowLayout); // 테이블에 추가

        } // tr for end
    }

    public String changeTimestamp(long timestamp, String fm) {
        Date date = new Date(timestamp * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat(fm); // HH:ss 포매팅

        // GMT(그리니치 표준시간)
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+9"));
        String formattedDate = sdf.format(date);

        return formattedDate;
    }

}