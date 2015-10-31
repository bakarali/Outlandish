package com.techhunger.com.outlandish.API;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.techhunger.com.outlandish.Accessors.LoginResponse;

/**
 * Created by bakar on 31/10/15.
 */
public class APIManager {
    private RequestQueue mRequestQueue;

    public APIManager(Context context) {
        this.mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public void doLogin(Response.Listener<LoginResponse> listener, Response.ErrorListener errorListener,String mobileNumber, String password){
        mRequestQueue.add(new LoginRequest(listener,errorListener,mobileNumber,password));
    }

}
