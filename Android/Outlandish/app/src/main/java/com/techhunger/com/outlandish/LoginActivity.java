package com.techhunger.com.outlandish;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.techhunger.com.outlandish.API.APIManager;
import com.techhunger.com.outlandish.Accessors.LoginResponse;
import com.techhunger.com.outlandish.Utils.AppUtils;

public class LoginActivity extends AppCompatActivity {

    String uid = null;
    String name = null;
    String message  = null;
    private ProgressBar progress;

   //private static final String urlDomain = "192.168.1.8";
  private static final String urlDomain = "http://www.techhunger.com";
    //private static final String urlDomain = "http://outlandish-01.cloudapp.net";
    private ProgressDialog pDialog;
    public static final String PREFS_NAME = "UserData";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        final EditText inputMobileNoET = (EditText) findViewById(R.id.phone_no);
        final EditText inputPasswordET =(EditText) findViewById(R.id.password);

        TextView linktosignup = (TextView) findViewById(R.id.signtxt);
        linktosignup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                        startActivity(intent);
                        finish();
//
                    }
                }
        );
//        Button btntst = (Button)findViewById(R.id.test);
//        btntst.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        pDialog = new ProgressDialog(LoginActivity.this);
//                        pDialog.setMessage("Please wait...");
//                        pDialog.setCancelable(false);
//                        pDialog.show();
//                        EditText inputMobileNoET =(EditText) findViewById(R.id.phone_no);
//                        EditText inputPasswordET = (EditText)findViewById(R.id.password);
//
//
//
//                        String inputMobileNo = inputMobileNoET.getText().toString().trim();
//                        String inputPassword = inputPasswordET.getText().toString().trim();
//                       RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
//                        String url =urlDomain+"/user.php?action=login&mobile_number="+inputMobileNo+"&password="+inputPassword;
//                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                                new Response.Listener<String>() {
//                                    @Override
//                                    public void onResponse(String response) {
//                                        // Display the first 500 characters of the response string.
//                                        if (pDialog.isShowing())
//                                            pDialog.dismiss();
//                                        Toast.makeText(LoginActivity.this, "its work", Toast.LENGTH_LONG).show();
//                                    }
//                                }, new Response.ErrorListener() {
//
//
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                if (pDialog.isShowing())
//                                    pDialog.dismiss();
//                                Toast.makeText(LoginActivity.this, "its not work", Toast.LENGTH_LONG).show();
//                            }
//                        });
//// Add the request to the RequestQueue.
//                        queue.add(stringRequest);
//
//                    }
//
//                }
//        );

        Button btnlogin = (Button)findViewById(R.id.btnlogin);
        btnlogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String inputMobileNo = inputMobileNoET.getText().toString().trim();
                        String inputPwd = inputPasswordET.getText().toString().trim();
                        if (inputMobileNo.matches("") || inputPwd.matches("")) {
                            Toast.makeText(LoginActivity.this, "Enter phone no. and password", Toast.LENGTH_LONG).show();
                        } else {
                         //   new dologin().execute();
                            makeLogin();
                        }
                    }

                }
        );
        Button btngotosignup = (Button) findViewById(R.id.gotosignup);
        btngotosignup.setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                        startActivity(intent);
                        finish();

//
                    }
                }
        );
    }



    private Response.Listener<LoginResponse> successResponse = new Response.Listener<LoginResponse>(){

        @Override
        public void onResponse(LoginResponse responseLogin) {
            if(responseLogin != null){
                if(responseLogin.getStatus().equals("OK")){
                        if(responseLogin.getResponse() != null) {
                            if (responseLogin.getResponse().getUid() != null) {
                                //storing
                                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("name", responseLogin.getResponse().getName());
                                editor.putString("uid", responseLogin.getResponse().getUid());
                                editor.commit();
                                Intent mapMasterIntent = new Intent(LoginActivity.this, MapsMasterActivity.class);
                                startActivity(mapMasterIntent);
                            } else {
                                message = responseLogin.getMessage();
                            }
                        }else{
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                        }

            }else{
                    message = responseLogin.getMessage();
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                }
                progress.setVisibility(View.GONE);
        }
    }
    };


    private Response.ErrorListener errorListener = new Response.ErrorListener(){

        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            progress.setVisibility(View.GONE);
        }
    };


    public void makeLogin(){
        AppUtils.hideSoftKeyboard(LoginActivity.this);
        progress.setVisibility(View.VISIBLE);
        EditText inputMobileNoET =(EditText) findViewById(R.id.phone_no);
        EditText inputPasswordET = (EditText)findViewById(R.id.password);



        String inputMobileNo = inputMobileNoET.getText().toString().trim();
        String inputPassword = inputPasswordET.getText().toString().trim();



        APIManager apiManager = new APIManager(getApplicationContext());
        apiManager.doLogin(successResponse,errorListener,inputMobileNo,inputPassword);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
//    private class dologin extends AsyncTask<Void, Void, Void> {
//
//
//
//        EditText inputMobileNoET =(EditText) findViewById(R.id.phone_no);
//        EditText inputPasswordET = (EditText)findViewById(R.id.password);
//
//
//
//        String inputMobileNo = inputMobileNoET.getText().toString().trim();
//        String inputPassword = inputPasswordET.getText().toString().trim();
//
//
//        @Override
//        protected void onPreExecute() {
//
//            super.onPreExecute();
//
//            // Showing progress dialog
//            pDialog = new ProgressDialog(LoginActivity.this);
//            pDialog.setMessage("Please wait...");
//            pDialog.setCancelable(false);
//            pDialog.show();
//
//        }
//
//
//        @Override
//        protected Void doInBackground(Void... arg0) {
//
//            //String url_user_login =  urlDomain+"/user.php?action=login&mobile_number="+inputMobileNo+"&password="+inputPassword;
//
//            // Making a request to url and getting response
//
//
//            String jsonStr = sh.makeServiceCall(url_user_login, ServiceHandler.GET);
//
//            Log.d("Response: ", "> " + jsonStr);
//
//                if (jsonStr != null) {
//                try {
//
//                    JSONObject jobj = new JSONObject(jsonStr);
//                    if(jobj.getString("status").equals("OK")){
//                        JSONObject responseObj = jobj.getJSONObject("response");
//                        if(responseObj.getString("uid")!=null){
//                            uid = responseObj.getString("uid");
//                            name = responseObj.getString("name");
//                        }
//                    }else{
//                        message = jobj.getString("message");
//                    }
//
//
//                    //JSONObject currentLo = responseObj.getJSONObject("url_code");
//
//
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                Log.e("ServiceHandler", "Couldn't get any data from the url");
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            // Dismiss the progress dialog
//            if (pDialog.isShowing())
//                pDialog.dismiss();
//            /**
//             * Updating parsed JSON data into ListView
//             * */
//
//            if(uid!=null)
//
//            {
//                //storing
//                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
//                editor.putString("name", name);
//                editor.putString("uid", uid);
//                editor.commit();
////                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
////                boolean silent = settings.getBoolean("silentMode", false);
////                setSilent(silent);
//                // Toast.makeText(SignupActivity.this, "Sign up success", Toast.LENGTH_LONG).show();
//                Intent mapMasterIntent =  new Intent(LoginActivity.this, MapsMasterActivity.class);
//                startActivity(mapMasterIntent);
//                finish();
//
//            }
//
//            else
//
//            {
//                if(message!=null) {
//                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
//                }else {
//                    Toast.makeText(LoginActivity.this, "Please try again.", Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//
//
//
//    }
}
