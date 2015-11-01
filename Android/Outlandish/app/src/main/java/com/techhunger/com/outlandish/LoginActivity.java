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
import com.techhunger.com.outlandish.Accessors.LoginResponse;
import com.techhunger.com.outlandish.Utils.AppUtils;

public class LoginActivity extends AppCompatActivity {

    String message  = null;
    private ProgressBar progress;

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
                    }
                }
        );
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
                            makeLogin(inputMobileNo, inputPwd);
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
                                finish();
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


    public void makeLogin(String inputMobileNo, String inputPassword){
        AppUtils.hideSoftKeyboard(LoginActivity.this);
        progress.setVisibility(View.VISIBLE);

        APIManager apiManager = new APIManager(getApplicationContext());
        apiManager.doLogin(successResponse,errorListener,inputMobileNo,inputPassword);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
