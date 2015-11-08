package com.techhunger.com.outlandish.Accessors;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bakar on 8/11/15.
 */
public class ForceUpdateResponse {
    private String status;

    private String message;

    @SerializedName("update_required") private String updateRequired;

    public double getMinVersion() {
        return minVersion;
    }

    public void setMinVersion(double minVersion) {
        this.minVersion = minVersion;
    }

    @SerializedName("min_version")  private double minVersion;

    public String getUpdateRequired() {
        return updateRequired;
    }

    public void setUpdateRequired(String updateRequired) {
        this.updateRequired = updateRequired;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }




}
