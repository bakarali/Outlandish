package com.techhunger.com.outlandish.API;

/**
 * Created by bakar on 31/10/15.
 */
public class APIConfig {
    public static final String HOST = "http://www.techhunger.com";
    public static final String API_LOGIN = "/user.php?action=login&mobile_number=<MOBILE_NO>&password=<PASSWORD>";

    public static final String API_SIGNUP = "/user.php?action=signup&name=<NAME>&email_id=<EMAIL>&password=<PASSWORD>&mobile_number=<MOBILE_NO>";

    public static final String API_FORCEUPDATE = "/updatechecker.php";

    public static final String API_STOPSHARE = "/user_start_loc.php?action=stop_share_location&url_code=<URL_CODE>";

    public static final String API_STARTSHARE = "/user_start_loc.php?action=share_location&start_loc=<START_LOC>&end_loc=<END_LOC>&uid=<UID>";

    public static final String API_UPDATECURRENTLOC = "/current_loc.php?action=send_current_loc&current_loc=<CURRENT_LOC>&url_code=<URL_CODE>";

    public static final String API_UPDATEENDLOC = "/user_start_loc.php?action=updateEndLoc&url_code=<URL_CODE>&end_loc=<END_LOC>";


}

