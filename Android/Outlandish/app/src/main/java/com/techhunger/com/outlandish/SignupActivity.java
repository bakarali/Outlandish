package com.techhunger.com.outlandish;

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
import com.techhunger.com.outlandish.Accessors.SignupResponse;
import com.techhunger.com.outlandish.Utils.AppUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SignupActivity extends AppCompatActivity {

    private ProgressBar progress;
    String inputName = null;
    String message  = null;

    public static final String PREFS_NAME = "UserData";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);

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
                        inputName = inputNameET.getText().toString().trim();
                        String inputMobileNo = inputMobileNoET.getText().toString().trim();
                        String inputEmailid = inputEmailIdET.getText().toString().trim();
                        String inputPwd = inputPasswordET.getText().toString().trim();
                        String inputConfPwd = inputConfirmPasswordET.getText().toString().trim();
                        if (inputName.matches("")||inputMobileNo.matches("")||inputEmailid.matches("")||inputPwd.matches("")||inputConfPwd.matches("")) {
                            Toast.makeText(SignupActivity.this, "all field mandatory ", Toast.LENGTH_LONG).show();
                        } else {
                            if (inputPwd.equals(inputConfPwd)) {
                                try {
                                    makeSignup(URLEncoder.encode(inputMobileNo, "utf-8"), inputPwd, URLEncoder.encode(inputName, "utf-8") , inputEmailid);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(SignupActivity.this, "Password doesn't match ", Toast.LENGTH_LONG).show();

                            }

                        }
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
                    }
                }
        );


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void makeSignup(String inputMobileNo, String inputPassword, String inputName, String inputEmail){
        AppUtils.hideSoftKeyboard(SignupActivity.this);
        progress.setVisibility(View.VISIBLE);

        APIManager apiManager = new APIManager(getApplicationContext());
        apiManager.doSignUp(successResponse, errorListener, inputMobileNo, inputPassword, inputName, inputEmail);
    }

    private Response.Listener<SignupResponse> successResponse = new Response.Listener<SignupResponse>(){

        @Override
        public void onResponse(SignupResponse responseSignup) {
            if(responseSignup != null){
                if(responseSignup.getStatus().equals("OK")){
                    if(responseSignup.getResponse() != null) {
                        if (responseSignup.getResponse().getUid() != null) {

                            //storing
                            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                            editor.putString("name", inputName);
                            editor.putString("uid", responseSignup.getResponse().getUid());
                            editor.commit();
                            Intent mapMasterIntent =  new Intent(SignupActivity.this, MapsMasterActivity.class);
                            startActivity(mapMasterIntent);
                            finish();

                        } else {
                            message = responseSignup.getMessage();
                        }
                    }else{
                        Toast.makeText(SignupActivity.this, message, Toast.LENGTH_LONG).show();
                    }

                }else{
                    message = responseSignup.getMessage();
                    Toast.makeText(SignupActivity.this, message, Toast.LENGTH_LONG).show();
                }
                progress.setVisibility(View.GONE);
            }
        }
    };

    private Response.ErrorListener errorListener = new Response.ErrorListener(){

        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(SignupActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            progress.setVisibility(View.GONE);
        }
    };

}
