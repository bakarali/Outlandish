package com.techhunger.com.outlandish.API;

import com.android.volley.Response;
import com.techhunger.com.outlandish.Accessors.StartSharingResponse;

/**
 * Created by bakar on 8/11/15.
 */
public class StartSharingRequest extends THRequest {
    public StartSharingRequest(Response.Listener<StartSharingResponse> listener, Response.ErrorListener errorListener, String startLoc, String endLoc, String uid) {

        super(APIConfig.HOST+APIConfig.API_STARTSHARE.replace("<START_LOC>", startLoc).replace("<END_LOC>", endLoc).replace("<UID>",uid),StartSharingResponse.class,null,listener,errorListener);
    }
}
