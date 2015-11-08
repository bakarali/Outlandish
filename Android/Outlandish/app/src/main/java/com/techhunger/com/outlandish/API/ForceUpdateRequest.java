package com.techhunger.com.outlandish.API;

import com.android.volley.Response;
import com.techhunger.com.outlandish.Accessors.ForceUpdateResponse;

/**
 * Created by bakar on 8/11/15.
 */
public class ForceUpdateRequest extends THRequest{

    public ForceUpdateRequest(Response.Listener<ForceUpdateResponse> listener, Response.ErrorListener errorListener){
        super(APIConfig.HOST+APIConfig.API_FORCEUPDATE, ForceUpdateResponse.class, null, listener, errorListener);
    }

}
