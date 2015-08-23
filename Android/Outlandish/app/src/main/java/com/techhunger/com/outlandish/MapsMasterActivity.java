package com.techhunger.com.outlandish;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.techhunger.com.outlandish.commonclasses.ServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MapsMasterActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private  static final int ZOOM = 15;
    private static final String urlDomain = "192.168.1.8";
    private static final String url_user_start_loc =  "http://"+urlDomain+"/user_start_loc.php?start_loc=2.2322&end_loc=null&uid=25";


    String currentLoc = null;
     Handler handler = new Handler();
    Runnable runnable =null;


    LocationManager mLocationManager;
    private ProgressDialog pDialog;

    private static final int START_AFTER_SECONDS = 20;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_master);
        setUpMapIfNeeded();
        mMap.setMyLocationEnabled(true);


        onClickButtonListener();
    }
    public void onClickButtonListener(){

        Button button2 = (Button) findViewById(R.id.button);
        button2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // new GetUserStartLocation().execute();
                        //    startService(new Intent(MapsMasterActivity.this, SendCurrentLoc.class));
                        //   stopService(new Intent(MapsMasterActivity.this, SendCurrentLoc.class));

                        callAsynchronousTask();
                    }
                }
        );

        Button stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // new GetUserStartLocation().execute();
                        //    startService(new Intent(MapsMasterActivity.this, SendCurrentLoc.class));
                        //   stopService(new Intent(MapsMasterActivity.this, SendCurrentLoc.class));

                        if (runnable != null) {
                            handler.removeCallbacks(runnable);
                            handler.removeCallbacksAndMessages(null);
                        }
                    }
                }
        );


    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    public void callAsynchronousTask() {



         runnable = new Runnable() {

            @Override
            public void run() {
                try{
                    //do your code here

                    new UpdateCurrentLocation().execute();



                    //also call the same runnable
                    handler.postDelayed(this, 10000);

                }
                catch (Exception e) {
                    // TODO: handle exception
                    System.out.println(e);
                }
                finally{
                    //also call the same runnable
                  //  handler.postDelayed(this, 10000);
                }
            }
        };
        handler.postDelayed(runnable, 0);

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
       // Location myLocation = locationManager.getLastKnownLocation(provider);
        Location myLocation = getLastKnownLocation();


        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        double latitude = myLocation.getLatitude();
        double longitude = myLocation.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        LatLng myCoordinates = new LatLng(latitude, longitude);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(myCoordinates, 12);
        mMap.animateCamera(yourLocation);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(myCoordinates)      // Sets the center of the map to LatLng (refer to previous snippet)
                .zoom(ZOOM)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }



    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetUserStartLocation extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MapsMasterActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response


            String jsonStr = sh.makeServiceCall(url_user_start_loc, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try{
                JSONObject jobj = new JSONObject(jsonStr);
                JSONObject responseObj = jobj.getJSONObject("response");
                //JSONObject currentLo = responseObj.getJSONObject("url_code");
                currentLoc  =  responseObj.getString("url_code");
                }catch (JSONException e){
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

            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                        sendIntent.setData(Uri.parse("sms:"));
                      sendIntent.putExtra("sms_body", currentLoc);
            startActivity(sendIntent);
        }

    }


    private class UpdateCurrentLocation extends AsyncTask<Void, Void, Void> {

        JSONObject jobj = null;
        String responseStr = null;
        @Override
        protected Void doInBackground(Void... arg0) {


            Location myLocation = getLastKnownLocation();




            double latitude = myLocation.getLatitude();
            double longitude = myLocation.getLongitude();
            String finalLoc = String.valueOf(latitude)+","+String.valueOf(longitude);

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response

            String url_current_start_loc =  "http://"+urlDomain+"/current_loc.php?action=send_current_loc&current_loc="+finalLoc+"&url_code=abcurl";

            String jsonStr = sh.makeServiceCall(url_current_start_loc, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try{
                     jobj = new JSONObject(jsonStr);
                     responseStr = jobj.getString("status");


                }catch (JSONException e){
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
            if(responseStr.equals("OK")){

                Toast.makeText(MapsMasterActivity.this, "Saved", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(MapsMasterActivity.this, "Not Saved", Toast.LENGTH_LONG).show();
            }

        }

    }



}
