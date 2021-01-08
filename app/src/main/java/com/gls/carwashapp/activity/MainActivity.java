package com.gls.carwashapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.gls.carwashapp.R;

// 메인
public class MainActivity extends AppCompatActivity {



    // 이 메서드에 초기화 작업을 해주기
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    /*
       Volley 라이브러리 사용
       1. Request 객체 생성 ex (문자열 : StringRequest)
       2. 객체안에서 요청을 보내고 응답을 받음 -
       직접하지않음  RequestQueue에 추가하면 알아서 스레드를 생성하여 요청을 보내고 응답을 받음
       사용자는 add라고만 하면 끝남
    */
    // 요청 객체 생성
    /*public void sendRequest(){
        String url = "http://192.168.0.200:5000/get_device_state";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) { // 여기에 응답이 떨어짐
                        List<Map<String, Object>> res = new ArrayList<>(); // 리턴값
                        println("데이터 -> " + response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject();//result.getJSONObject(1);
                            //JSONArray result = new JSONArray(response);
                            JSONArray result = jsonObject.getJSONArray("result");

                        } catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        println("에러 -> " + error.getMessage());
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
        println("요청 보냄.");
    }

    public void println(String data){
        textView.append(data + "\n");
    }
    */

}