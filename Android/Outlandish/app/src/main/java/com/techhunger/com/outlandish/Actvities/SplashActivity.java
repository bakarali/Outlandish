package com.techhunger.com.outlandish.Actvities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.techhunger.com.outlandish.API.APIManager;
import com.techhunger.com.outlandish.Accessors.ForceUpdateResponse;
import com.techhunger.com.outlandish.R;
import com.techhunger.com.outlandish.UpdateActivity;

public class SplashActivity extends AppCompatActivity {
    private ProgressDialog pDialog;

    // private static final String urlDomain = "192.168.1.8";
    private static final String urlDomain = "http://www.techhunger.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        forceUpdatecheck();
    }

    private void forceUpdatecheck(){

        APIManager apiManager = new APIManager(getApplicationContext());
        apiManager.doForceUpdateCheck(successListener,errorListener);

    }

    private Response.Listener<ForceUpdateResponse> successListener = new Response.Listener<ForceUpdateResponse>(){

        @Override
        public void onResponse(ForceUpdateResponse response) {
            if (response!=null){
                if(response.getStatus().equals("OK") && response.getUpdateRequired().equals("true")){
                    PackageInfo pInfo = null;
                    try {
                        pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    double version = Double.parseDouble(pInfo.versionName);
                    if(version < response.getMinVersion()){
                        Intent intent = new Intent(SplashActivity.this, UpdateActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Intent intent = new Intent(SplashActivity.this, HomeMapActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
            }
        }
    };

    private Response.ErrorListener errorListener = new Response.ErrorListener(){

        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(SplashActivity.this, error.toString(), Toast.LENGTH_LONG).show();

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
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



}


