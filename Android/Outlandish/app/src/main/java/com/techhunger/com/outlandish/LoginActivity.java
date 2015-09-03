package com.techhunger.com.outlandish;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.techhunger.com.outlandish.commonclasses.ServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    String uid = null;
    String name = null;
   //private static final String urlDomain = "192.168.1.8";
  private static final String urlDomain = "http://www.techhunger.com";
    //private static final String urlDomain = "http://outlandish-01.cloudapp.net";
    // private static final String url_user_start_loc =  "http://"+urlDomain+"/user_start_loc.php?start_loc=2.2322&end_loc=null&uid=25";
    private ProgressDialog pDialog;
    public static final String PREFS_NAME = "UserData";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

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

        Button btnlogin = (Button)findViewById(R.id.btnlogin);
        btnlogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String inputMobileNo = inputMobileNoET.getText().toString();
                        String inputPwd = inputPasswordET.getText().toString();
                        if (inputMobileNo.matches("")||inputPwd.matches("")) {
                            Toast.makeText(LoginActivity.this, "Enter phone no. and password", Toast.LENGTH_LONG).show();
                        } else {
                        new dologin().execute();
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
    private class dologin extends AsyncTask<Void, Void, Void> {



        EditText inputMobileNoET =(EditText) findViewById(R.id.phone_no);
        EditText inputPasswordET = (EditText)findViewById(R.id.password);



        String inputMobileNo = inputMobileNoET.getText().toString();
        String inputPassword = inputPasswordET.getText().toString();


        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            // Showing progress dialog
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            String url_user_login =  urlDomain+"/user.php?action=login&mobile_number="+inputMobileNo+"&password="+inputPassword;

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response


            String jsonStr = sh.makeServiceCall(url_user_login, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

                if (jsonStr != null) {
                try {
                    JSONObject jobj = new JSONObject(jsonStr);
                    JSONObject responseObj = jobj.getJSONObject("response");
                    //JSONObject currentLo = responseObj.getJSONObject("url_code");
                    uid = responseObj.getString("uid");
                    name = responseObj.getString("name");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */

            if(uid!=null)

            {
                //storing
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("name", name);
                editor.putString("uid", uid);
                editor.commit();
//                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//                boolean silent = settings.getBoolean("silentMode", false);
//                setSilent(silent);
                // Toast.makeText(SignupActivity.this, "Sign up success", Toast.LENGTH_LONG).show();
                Intent mapMasterIntent =  new Intent(LoginActivity.this, MapsMasterActivity.class);
                startActivity(mapMasterIntent);
                finish();

            }

            else

            {
                Toast.makeText(LoginActivity.this, "Please register.", Toast.LENGTH_LONG).show();
            }
        }



    }
}
