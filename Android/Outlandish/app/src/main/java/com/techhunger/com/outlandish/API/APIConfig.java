package com.techhunger.com.outlandish.API;

/**
 * Created by bakar on 31/10/15.
 */
public class APIConfig {
    public static final String HOST = "http://www.techhunger.com";
    public static final String API_LOGIN = "/user.php?action=login&mobile_number=<MOBILE_NO>&password=<PASSWORD>";

    public static final String API_SIGNUP = "/user.php?action=signup&name=<NAME>&email_id=<EMAIL>&password=<PASSWORD>&mobile_number=<MOBILE_NO>";

    public static final String API_FORCEUPDATE = "/updatechecker.php";


}

