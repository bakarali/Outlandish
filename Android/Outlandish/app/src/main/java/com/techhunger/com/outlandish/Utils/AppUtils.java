package com.techhunger.com.outlandish.Utils;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by bakar on 31/10/15.
 */
public class AppUtils {
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void getCurrentLocation(Activity activity){

    }
}
