package com.techhunger.com.outlandish.Accessors;

/**
 * Created by bakar on 31/10/15.
 */
public class LoginResponse {
   private String status;
   private String message;
   private Response response;

    public Response getResponse() {
        return response;
    }

    public void setReponse(Response response) {
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

