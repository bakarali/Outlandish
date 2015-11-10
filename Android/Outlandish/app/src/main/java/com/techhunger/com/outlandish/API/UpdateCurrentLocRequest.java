package com.techhunger.com.outlandish.API;

import com.android.volley.Response;
import com.techhunger.com.outlandish.Accessors.CommonResponse;

/**
 * Created by bakar on 8/11/15.
 */
public class UpdateCurrentLocRequest extends THRequest {

    public UpdateCurrentLocRequest(Response.Listener<CommonResponse> listener, Response.ErrorListener errorListener, String currentLoc, String urlCode) {

        super(APIConfig.HOST+APIConfig.API_UPDATECURRENTLOC.replace("<CURRENT_LOC>", currentLoc).replace("<URL_CODE>", urlCode),CommonResponse.class,null,listener,errorListener);
    }
}
