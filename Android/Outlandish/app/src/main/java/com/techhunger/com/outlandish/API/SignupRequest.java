package com.techhunger.com.outlandish.API;

import com.android.volley.Response;
import com.techhunger.com.outlandish.Accessors.SignupResponse;

/**
 * Created by bakar on 1/11/15.
 */
public class SignupRequest extends THRequest {

    public SignupRequest(Response.Listener<SignupResponse> listener, Response.ErrorListener errorListener, String mobileNumber, String password, String name, String email){
        super(APIConfig.HOST+APIConfig.API_SIGNUP.replace("<MOBILE_NO>",mobileNumber).replace("<PASSWORD>",password).replace("<EMAIL>", email).replace("<NAME>",name), SignupResponse.class, null, listener, errorListener);
    }
}
