package com.gls.carwashapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gls.carwashapp.common.CustomViewPagerAdapter;
import com.gls.carwashapp.common.PosConfigSingleTon;
import com.gls.carwashapp.model.Config;
import com.gls.carwashapp.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// 메인 - 로그인 화면 액티비티
public class LoginActivity extends AppCompatActivity {

    private EditText textId; // 아이디
    private EditText textPw; // 비밀번호

    Button btnLogin;
    Button btnExit;

    public static Config vo = PosConfigSingleTon.getInstance();
    public Config config;

    // 이 메서드에 초기화 작업을 해주기
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // 화면에 무엇을 보여줄지 결정하는 메서드

        bindView(); // 디자인 필드 바인딩
        config = getPosConfig();

        // 처음에는 SharedPreferences에 아무런 정보도 없으므로 값을 저장할 키들을 생성한다.
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);

        String loginId = auto.getString("id",null); // getString의 첫 번째 인자는 저장될 키, 두 번쨰 인자는 값입니다.
        String loginPw = auto.getString("pw",null); // 첨엔 값이 없으므로 키값은 원하는 것으로 하시고 값을 null을 줍니다.

        textId.setText(loginId);
        textPw.setText(loginPw);

    }

    // 종료호출 메서드
    @Override
    protected void onStop() {
        super.onStop();

        // Activity 가 종료되기 전에 저장한다
        // SharedPreferences 에 설정값(특별히 기억해야할 사용자 값)을 저장하기
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = auto.edit(); // 저장하려면 editor가 필요

        // 사용자 입력값
        String id = textId.getText().toString();
        String pw = textPw.getText().toString();

        // 데이터 저장
        editor.putString("id", id);
        editor.putString("pw", pw);

        editor.commit(); // 파일에 최종 반영함 꼭!!!!!!
    }

    // 뷰(안드로이드 화면 구성요소) 속성들 정의 메서드
    public void bindView(){
        textId = (EditText) findViewById(R.id.text_id);
        textPw = (EditText) findViewById(R.id.text_pw);

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 로그인 검증
                if (isLogin()) {
                    Intent intent = new Intent(getApplicationContext(), RealActivity.class);
                    intent.putExtra("config", config);
                    Toast.makeText(getApplicationContext(), textId.getText().toString() + "님 환영합니다.", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                } else {
                    showMessage();
                }
            }
        });

        // RequestQueue 생성
        if (CustomViewPagerAdapter.requestQueue == null) {
            CustomViewPagerAdapter.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
    }

    private void showMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내");
        builder.setMessage("\n아이디나 패스워드를 확인해주세요");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.create(); // 최종적으로 알림대화상자 만듬
        dialog.show();
    }

    // shop 정보 가져오기
    public Config getPosConfig(){
        String url = CustomViewPagerAdapter.url + "get_pos_config";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) { // 여기에 응답이 떨어짐

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject job = jsonObject.getJSONObject("pos_config"); // 한번더뺴기

                            // 장비갯수, id,pw 가져오기
                            vo.setSelfCount(job.getString("self_count"));
                            vo.setAirCount(job.getString("air_count"));
                            vo.setMateCount(job.getString("mate_count"));
                            vo.setChargerCount(job.getString("charger_count"));
                            vo.setTouchCount(job.getString("touch_count"));
                            vo.setReaderCount(job.getString("reader_count"));
                            vo.setShopId(job.getString("shop_id"));
                            vo.setShopPw(job.getString("shop_pw"));
                            vo.setShopName(job.getString("shop_name"));
                            vo.setManagerNo(job.getInt("manager_no"));
                            Log.d("Config", vo.toString());

                        } catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(getApplicationContext(),
                            "모바일 데이터를 이용해주세요.",Toast.LENGTH_SHORT).show();
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
        return vo;
    }

    // 로그인 검증
    public boolean isLogin(/*PosConfigVO vo*/){
        if (textId.getText().toString().equals("admin")) {
            if (textPw.getText().toString().equals("admin1234")) {
                return true;
            }
        }
        return false;
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 101){
//            String name = data.getStringExtra("name");
//            //Toast.makeText(getApplicationContext(), "메뉴화면으로부터 응답 : " + name, Toast.LENGTH_LONG).show();
//        }
//    }

}