package com.techhunger.com.outlandish.Accessors;

/**
 * Created by bakar on 8/11/15.
 */
public class StartSharingResponse {
    private String status;
    private String message;
    private StartSharingResponseObj response;

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

    public StartSharingResponseObj getResponse() {
        return response;
    }

    public void setResponse(StartSharingResponseObj response) {
        this.response = response;
    }
}
