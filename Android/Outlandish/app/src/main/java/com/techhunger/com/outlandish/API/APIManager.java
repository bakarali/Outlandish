package com.techhunger.com.outlandish.API;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.techhunger.com.outlandish.Accessors.CommonResponse;
import com.techhunger.com.outlandish.Accessors.ForceUpdateResponse;
import com.techhunger.com.outlandish.Accessors.LoginResponse;
import com.techhunger.com.outlandish.Accessors.SignupResponse;
import com.techhunger.com.outlandish.Accessors.StartSharingResponse;
import com.techhunger.com.outlandish.Accessors.StopShareResponse;

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

    public void doSignUp(Response.Listener<SignupResponse> listener, Response.ErrorListener errorListener,String mobileNumber, String password, String name, String email){
        mRequestQueue.add(new SignupRequest(listener,errorListener,mobileNumber,password, name, email));
    }

    public void doForceUpdateCheck(Response.Listener<ForceUpdateResponse> listener, Response.ErrorListener errorListener){
        mRequestQueue.add(new ForceUpdateRequest(listener,errorListener));
    }

    public void doStopShare(Response.Listener<StopShareResponse> listener, Response.ErrorListener errorListener, String urlCode){
        mRequestQueue.add(new StopShareRequest(listener,errorListener, urlCode));
    }

    public void doStartSharing(Response.Listener<StartSharingResponse> listener, Response.ErrorListener errorListener, String startLoc, String endLoc, String uid ){
        mRequestQueue.add(new StartSharingRequest(listener,errorListener, startLoc, endLoc, uid));
    }

    public void doUpdateCurrentLoc(Response.Listener<CommonResponse> listener, Response.ErrorListener errorListener, String currentLoc, String urlCode){
        mRequestQueue.add(new UpdateCurrentLocRequest(listener,errorListener, currentLoc, urlCode));
    }

    public void doUpdateEndLoc(Response.Listener<CommonResponse> listener, Response.ErrorListener errorListener, String endLoc, String urlCode){
        mRequestQueue.add(new UpdateEndLocRequest(listener,errorListener, endLoc, urlCode));
    }

}
