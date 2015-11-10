package com.techhunger.com.outlandish.API;

import com.android.volley.Response;
import com.techhunger.com.outlandish.Accessors.StopShareResponse;

import java.util.Map;

/**
 * Created by bakar on 8/11/15.
 */
public class StopShareRequest extends THRequest {
    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url           URL of the request to make
     * @param clazz         Relevant class object, for Gson's reflection
     * @param headers       Map of request headers
     * @param listener
     * @param errorListener
     */
    public StopShareRequest(Response.Listener<StopShareResponse> listener, Response.ErrorListener errorListener, String urlCode) {

        super(APIConfig.HOST+APIConfig.API_STOPSHARE.replace("<URL_CODE>",urlCode),StopShareResponse.class,null,listener,errorListener);
    }
}
