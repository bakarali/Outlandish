package com.techhunger.com.outlandish.Accessors;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bakar on 8/11/15.
 */
public class StartSharingResponseObj {
    @SerializedName("url_code") private String urlCode;

    public String getUrlCode() {
        return urlCode;
    }

    public void setUrlCode(String urlCode) {
        this.urlCode = urlCode;
    }
}
