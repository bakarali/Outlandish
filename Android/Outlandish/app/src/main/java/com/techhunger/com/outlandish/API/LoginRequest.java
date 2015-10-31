package com.techhunger.com.outlandish.API;
import com.android.volley.Response;
import com.techhunger.com.outlandish.Accessors.LoginResponse;

/**
 * Created by bakar on 31/10/15.
 */
public class LoginRequest extends THRequest {

    public LoginRequest(Response.Listener<LoginResponse> listener, Response.ErrorListener errorListener, String mobileNumber,String password) {

        super(APIConfig.HOST+APIConfig.API_LOGIN.replace("<MOBILE_NO>",mobileNumber).replace("<PASSWORD>",password),LoginResponse.class,null,listener,errorListener);
    }

//    public void doLogin(String url){
//        THRequest loginReq = new THRequest(url, LoginResponse.class, null, new Response.Listener<LoginResponse>(){
//            @Override
//            public void onResponse(LoginResponse login) {
//                Log.d("Hello","aa");
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                if(volleyError != null) Log.e("MainActivity", volleyError.getMessage());
//            }
//        });
//
//        mRequestQueue.add(loginReq);
//  }

}
