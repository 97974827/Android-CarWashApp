package com.gls.carwashapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonthSalesActivity extends AppCompatActivity{// implements DatePickerDialog.OnDateSetListener {

    int year;
    int month;
    int days;

    ImageButton btnBack;

    ProgressDialog progressDialog;
    Handler handler = new Handler(); // 핸들러 객체

    TextView lblDate;  // 년월 바
    TableLayout tableMonth; // 월별표

    // 일, 현금, 카드, 충전
    TextView tdTextDays;
    TextView tdTextCash;
    TextView tdTextCard;
    TextView tdTextCharge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_sales);

        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        days = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, listener, year, month, days);
        try {
            Field[] f = dialog.getClass().getDeclaredFields();
            for (Field dateField : f) {
                if (dateField.getName().equals("mDatePicker")) {
                    dateField.setAccessible(true);

                    DatePicker datePicker = (DatePicker) dateField.get(dialog);

                    Field[] datePickerFields = dateField.getType().getDeclaredFields();

                    for (Field datePickerField : datePickerFields) {
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = new Object();
                            dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.getDatePicker().setCalendarViewShown(false);
        dialog.show();

        lblDate = (TextView) findViewById(R.id.lbl_date);

//        tdTextDays = (TextView) findViewById(R.id.td_text_days);
//        tdTextCash = (TextView) findViewById(R.id.td_text_cash);
//        tdTextCard = (TextView) findViewById(R.id.td_text_card);
//        tdTextCharge = (TextView) findViewById(R.id.td_text_charge);

        tableMonth = (TableLayout) findViewById(R.id.table_days);

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
            getMonthlySales(year, monthOfYear);
        }
    };


    // 테이블 동적 생성하기
    public void tableGrid(int trCt, int tdCt, List<String> days, List<Integer> cash, List<Integer> card, List<Integer> charge) {

        TableRow.LayoutParams rowLayout = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableRow[] row = new TableRow[trCt+1];     // 테이블 ROW 생성
        TextView[][] text = new TextView[trCt+1][tdCt+1];   // 데이터

        for (int tr = 0; tr < trCt+1; tr++) {         // 행
            row[tr] = new TableRow(this);
            row[tr].setWeightSum(1);

            for (int td = 0; td < tdCt+1; td++) {     // 열
                text[tr][td] = new TextView(this);

                if (tr == 0) {
                    text[tr][td].setBackgroundResource(R.color.sales);
                    if (td == 0) {
                        text[tr][td].setText("날짜");
                    } else if (td == 1) {
                        text[tr][td].setText("현금");
                    } else if (td == 2) {
                        text[tr][td].setText("카드");
                    } else if (td == 3) {
                        text[tr][td].setText("충전");
                    }
                } else {
                    text[tr][td].setBackgroundResource(R.drawable.table_border);
                    if (td == 0) {
                        text[tr][td].setText(days.get(tr-1));
                        text[tr][td].setTextColor(Color.BLACK);
                    } else if (td == 1) {
                        text[tr][td].setText(String.format("%,d", cash.get(tr-1)));
                    } else if (td == 2) {
                        text[tr][td].setText(String.format("%,d", card.get(tr-1)));
                    } else if (td == 3) {
                        text[tr][td].setText(String.format("%,d", charge.get(tr-1)));
                    }
                }

                text[tr][td].setTextSize(24);
                text[tr][td].setGravity(Gravity.CENTER);

                row[tr].addView(text[tr][td]);
            } // td for end

            tableMonth.addView(row[tr], rowLayout); // 테이블에 추가

        } // tr for end
    }

    // 월별 매출조회
    public void getMonthlySales(int y, int m) {
        lblDate.setText(y + " 년   " + (m+1) + " 월");
        year = y;
        month = m+1;

        String url = CustomViewPagerAdapter.url + "get_monthly_sales";

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("매출정보를 불러오는 중입니다....");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
        progressDialog.show();

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { // 여기에 응답이 떨어짐
                        progressDialog.dismiss(); // 종료

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray result = jsonObject.getJSONArray("result");

//                            Log.d("month data ??????  ", response);

                            List<String> daysArray = new ArrayList<>();
                            List<Integer> cashArray = new ArrayList<>();
                            List<Integer> cardArray = new ArrayList<>();
                            List<Integer> chargeArray = new ArrayList<>();

                            String days = "";
                            int cash = 0;
                            int card = 0;
                            int charge = 0;

                            for (int i=0; i < result.length(); i++) {
                                JSONObject job = result.getJSONObject(i);  // JSONObject 추출

                                days = job.getString("days");
                                cash = job.getInt("cash");
                                card = job.getInt("card");
                                charge = job.getInt("charge");

                                daysArray.add(days);
                                cashArray.add(cash);
                                cardArray.add(card);
                                chargeArray.add(charge);
                            }

                            tableGrid(33,4, daysArray, cashArray, cardArray, chargeArray);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),
                                "데이터 수집장치가 연결되어 있지 않습니다.",Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            // 요청 파라미터 추가
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("year", Integer.toString(year));
                params.put("month", Integer.toString(month));
//                Log.d("year", Integer.toString(year));
//                Log.d("month", Integer.toString(month));
                return params;
            }
        };

        request.setShouldCache(false); // 이전 결과가 있더라도 새로 요청해서 응답을 보여주게 됨
        CustomViewPagerAdapter.requestQueue.add(request); // 큐에 넣어줌
    }


}
