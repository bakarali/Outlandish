package com.techhunger.com.outlandish.Accessors;

/**
 * Created by bakar on 31/10/15.
 */
public class LoginResponse {
   private String status;
   private String message;
   private LoginResponseObj response;

    public LoginResponseObj getResponse() {
        return response;
    }

    public void setReponse(LoginResponseObj response) {
        this.response = response;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

