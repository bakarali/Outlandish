package com.techhunger.com.outlandish.API;

import com.android.volley.Response;
import com.techhunger.com.outlandish.Accessors.CommonResponse;

/**
 * Created by bakar on 10/11/15.
 */
public class UpdateEndLocRequest extends THRequest {

    public UpdateEndLocRequest(Response.Listener<CommonResponse> listener, Response.ErrorListener errorListener, String endLoc, String urlCode) {

        super(APIConfig.HOST+APIConfig.API_UPDATEENDLOC.replace("<END_LOC>", endLoc).replace("<URL_CODE>", urlCode),CommonResponse.class,null,listener,errorListener);
    }
}
