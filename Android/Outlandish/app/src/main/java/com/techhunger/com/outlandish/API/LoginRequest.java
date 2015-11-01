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
}
