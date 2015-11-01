package com.techhunger.com.outlandish.Accessors;

/**
 * Created by bakar on 1/11/15.
 */
public class SignupResponse {
    private String status;
    private String message;
    private SignupResponseObj response;

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

    public SignupResponseObj getResponse() {
        return response;
    }

    public void setResponse(SignupResponseObj response) {
        this.response = response;
    }



}
