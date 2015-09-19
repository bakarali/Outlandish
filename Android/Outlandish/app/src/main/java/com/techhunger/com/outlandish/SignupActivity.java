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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SignupActivity extends AppCompatActivity {


    String uid = null;
    String message  = null;


  //  private static final String urlDomain = "192.168.1.8";
   private static final String urlDomain = "http://www.techhunger.com";
    //private static final String urlDomain = "http://outlandish-01.cloudapp.net";
    private ProgressDialog pDialog;
    public static final String PREFS_NAME = "UserData";


    // String inputPwd = inputPasswordET.getText().toString();
    // String inputConfPwd = inputConfirmPasswordET.getText().toString();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        final EditText inputNameET = (EditText) findViewById(R.id.name);
        final EditText inputMobileNoET =(EditText) findViewById(R.id.phone_no);
        final EditText inputEmailIdET = (EditText)findViewById(R.id.emailid);
        final EditText inputPasswordET = (EditText)findViewById(R.id.password);
        final EditText inputConfirmPasswordET = (EditText)findViewById(R.id.confirmpassword);


        TextView linktologin = (TextView) findViewById(R.id.logintxt);
        linktologin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
//
                    }
                }
        );

        Button btnSignup = (Button) findViewById(R.id.btnsignup);
        btnSignup.setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String inputName = inputNameET.getText().toString().trim();
                        String inputMobileNo = inputMobileNoET.getText().toString().trim();
                        String inputEmailid = inputEmailIdET.getText().toString().trim();
                        String inputPwd = inputPasswordET.getText().toString().trim();
                        String inputConfPwd = inputConfirmPasswordET.getText().toString().trim();
                        if (inputName.matches("")||inputMobileNo.matches("")||inputEmailid.matches("")||inputPwd.matches("")||inputConfPwd.matches("")) {
                            Toast.makeText(SignupActivity.this, "all field mandatory ", Toast.LENGTH_LONG).show();
                        } else {
                            if (inputPwd.equals(inputConfPwd)) {
                                // password and confirm passwords equal.go to next step
                                new doSignup().execute();
                            } else {
                                //passwords not matching.please try again
                                Toast.makeText(SignupActivity.this, "Password doesn't match ", Toast.LENGTH_LONG).show();

                            }

                        }



//
                    }
                }
        );
        Button btngotologin = (Button) findViewById(R.id.gotologin);
        btngotologin.setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
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
        getMenuInflater().inflate(R.menu.menu_signup, menu);
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

    private class doSignup extends AsyncTask<Void, Void, Void> {


        EditText inputNameET = (EditText) findViewById(R.id.name);
        EditText inputMobileNoET =(EditText) findViewById(R.id.phone_no);
        EditText inputEmailIdET = (EditText)findViewById(R.id.emailid);
        EditText inputPasswordET = (EditText)findViewById(R.id.password);


        String inputName = inputNameET.getText().toString().trim();

        String inputMobileNo = inputMobileNoET.getText().toString().trim();
        String inputEmailId = inputEmailIdET.getText().toString().trim();

        String inputPassword = inputPasswordET.getText().toString().trim();


        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            // Showing progress dialog
            pDialog = new ProgressDialog(SignupActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            String url_user_signup = null;
            try {
                url_user_signup = urlDomain+"/user.php?action=signup&name="+ URLEncoder.encode(inputName, "utf-8")+"&email_id="+inputEmailId+"&password="+inputPassword+"&mobile_number="+URLEncoder.encode(inputMobileNo, "utf-8")+"";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response


            String jsonStr = sh.makeServiceCall(url_user_signup, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {

                try {
                    JSONObject jobj = new JSONObject(jsonStr);
                    if(jobj.getString("status").equals("OK")){
                        JSONObject responseObj = jobj.getJSONObject("response");
                        if(responseObj.getString("uid")!=null){
                            uid = responseObj.getString("uid");
                        }
                    }else{
                         message = jobj.getString("message");
                    }








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
                    editor.putString("name", inputName);
                    editor.putString("uid", uid);
                    editor.commit();
//                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//                boolean silent = settings.getBoolean("silentMode", false);
//                setSilent(silent);
                    // Toast.makeText(SignupActivity.this, "Sign up success", Toast.LENGTH_LONG).show();
                    Intent mapMasterIntent =  new Intent(SignupActivity.this, MapsMasterActivity.class);
                    startActivity(mapMasterIntent);
                    finish();

                }

                else

                {
                    if(message!=null) {
                        Toast.makeText(SignupActivity.this, message, Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(SignupActivity.this, "Please try again.", Toast.LENGTH_LONG).show();
                    }
                }
            }


        }



    }
