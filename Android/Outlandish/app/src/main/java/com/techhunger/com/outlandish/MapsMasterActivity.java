package com.techhunger.com.outlandish;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techhunger.com.outlandish.commonclasses.PlaceAutocompleteAdapter;
import com.techhunger.com.outlandish.commonclasses.ServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class MapsMasterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
private static final String TAG = "AutoPlace";
protected GoogleApiClient mGoogleApiClient;

private PlaceAutocompleteAdapter mAdapter;
    private Toolbar mToolbar;
private AutoCompleteTextView mAutocompleteView;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private  static final int ZOOM = 15;
      // private static final String urlDomain = "192.168.1.8";
      private static final String urlDomain = "http://www.techhunger.com";
    //private static final String urlDomain = "http://outlandish-01.cloudapp.net";

    Boolean clrbtnpress=false;
    Boolean chkshared = false;
    String url_code = null;
    String uid = null;
    String name = null;
    Handler handler = new Handler();
    Runnable runnable =null;
    String finalLoc;
    FloatingActionButton btnstop = null;
    FloatingActionButton btnshare = null;
    Button clearButton;
    FloatingActionButton btnreshare = null;
    FloatingActionButton btnmylocation = null;

    String endPointLatLong = null;


    LocationManager mLocationManager;
    private ProgressDialog pDialog;
    public static final String PREFS_NAME = "UserData";
    public static final String LOC_PREFS = "LocationData";
    Place place;
    private static final int START_AFTER_SECONDS = 20;


    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_master);
      //  android.support.v7.app.ActionBar actionBar =  getSupportActionBar();
        //actionBar.setDisplayShowHomeEnabled(true);
       // actionBar.setIcon(R.mipmap.ic_launcher4);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                setUpMapIfNeeded();
                mMap.setMyLocationEnabled(true);

            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            uid = prefs.getString("uid", null);
            name = prefs.getString("name", null);

            onClickButtonListener();
               autoPlaces();

        }else {
            //popup box
            Toast.makeText(MapsMasterActivity.this, "Please check your internet connection.", Toast.LENGTH_LONG).show();


        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mapmaster, menu);
        return true;


    }

    public void logOut(){
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().clear().apply();
        Intent intent = new Intent(MapsMasterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                logOut();


        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickButtonListener(){

        btnstop = (FloatingActionButton) findViewById(R.id.btnstop);
        btnstop.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (runnable != null) {
                            handler.removeCallbacks(runnable);
                            handler.removeCallbacksAndMessages(null);
                            btnstop.setVisibility(View.GONE);
                            btnreshare.setVisibility(View.GONE);
                            btnshare.setVisibility(View.VISIBLE);
                            //Toast.makeText(MapsMasterActivity.this, "update location stopped.", Toast.LENGTH_LONG).show();
                            new StopShareLocation().execute();
                            chkshared = false;
                            endPointLatLong=null;

                        }

                    }
                }
        );
         btnshare = (FloatingActionButton) findViewById(R.id.btnshare);
        btnshare.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View v) {

                       ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo netInfo = cm.getActiveNetworkInfo();
                        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                            if (uid != null) {
                                new GetUserStartLocation().execute();
                                chkshared = true;
                            } else {
                                Intent intent = new Intent(MapsMasterActivity.this, SignupActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }else {
                            //popup box
                            Toast.makeText(MapsMasterActivity.this, "Please check your internet connection.", Toast.LENGTH_LONG).show();


                        }

                    }
                }
        );

        btnreshare = (FloatingActionButton) findViewById(R.id.btnreshare);
        btnreshare.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (uid != null) {
                            SharedPreferences prefs = getSharedPreferences(LOC_PREFS, MODE_PRIVATE);
                            url_code = prefs.getString("url_code_from_sp", null);
                            if (url_code != null) {
                                sharingIntent.setType("text/plain");
                                String shareBody = "Check you friend location status here " + urlDomain + "/current_loc.php?action=getLocation&url_code=" + url_code;
                                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Terminus Share Location");
                                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                                startActivityForResult(sharingIntent, 0);
                                chkshared = true;

                            } else {
                                new GetUserStartLocation().execute();
                                chkshared = true;
                            }


                        } else {
                            Intent intent = new Intent(MapsMasterActivity.this, SignupActivity.class);
                            startActivity(intent);
                            finish();
                        }


                    }


                }
        );





}

    @Override
    public void onBackPressed() {
         createDialog();

    }
    private void createDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        MapsMasterActivity.super.onBackPressed();
                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory(Intent.CATEGORY_HOME);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }
    private void autoPlaces(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();



        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places);

        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        mAdapter = new PlaceAutocompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, bounds, null);
        mAutocompleteView.setAdapter(mAdapter);

        // Register a listener that receives callbacks when a suggestion has been selected
            mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        // Set up the 'clear text' button that clears the text in the autocomplete view
         clearButton = (Button) findViewById(R.id.button_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutocompleteView.setText("");
                mMap.clear();
                endPointLatLong=null;
                clrbtnpress = true;
                if(url_code!=null) {
                    new updateEndLoc().execute();
                }
            }
        });

    }




    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
              */
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);


            Log.i(TAG, "Called getPlaceById to get Place details for " + item.placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.

            place = places.get(0);
            // Format details of the place for display and show it in a TextView.
            endPointLatLong = place.getLatLng().toString();
            mMap.addMarker(new MarkerOptions()
                    .position(place.getLatLng())
                    .title(place.getName().toString()));

            if(chkshared){
                new updateEndLoc().execute();

            }

          //  mAutocompleteView.setText(place.getName());


            places.release();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        // Check if we were successful in obtaining the map.
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            setUpMap();
        }
    }


    public void callAsynchronousTask() {



         runnable = new Runnable() {

            @Override
            public void run() {
                try{
                    //do your code here

                    new UpdateCurrentLocation().execute();



                    //also call the same runnable
                    handler.postDelayed(this, 15000);

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
            if (mMap != null && isGPSEnable()) {
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

            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
             mMap.getUiSettings().setMyLocationButtonEnabled(false);
          //   mMap.setMyLocationEnabled(true);

            Criteria criteria = new Criteria();
            String provider = mLocationManager.getBestProvider(criteria, true);
            // Location myLocation = locationManager.getLastKnownLocation(provider);
            Location myLocation = getLastKnownLocation();

            if (myLocation != null) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                final double latitude = myLocation.getLatitude();
                final double longitude = myLocation.getLongitude();

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
                btnmylocation = (FloatingActionButton) findViewById(R.id.btnmyLocation);
                btnmylocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                      btnmylocation = (FloatingActionButton) findViewById(R.id.btnmyLocation);
                }}

                );


            }

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

    private boolean isGPSEnable() {
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }else{
            showGPSDisabledAlertToUser();
            return false;
        }
    }

    private void showGPSDisabledAlertToUser(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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

            Location myLocation = getLastKnownLocation();
            double latitude = myLocation.getLatitude();
            double longitude = myLocation.getLongitude();

            finalLoc = String.valueOf(latitude)+","+String.valueOf(longitude);

            String url_user_start_loc = null;
            try {
                if(endPointLatLong !=null){
                    url_user_start_loc = urlDomain+"/user_start_loc.php?action=share_location&start_loc="+finalLoc+"&end_loc="+ URLEncoder.encode(endPointLatLong, "utf-8") + "&uid=" + uid;
                }else{
                    url_user_start_loc = urlDomain+"/user_start_loc.php?action=share_location&start_loc="+finalLoc+"&end_loc=null&uid="+uid;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


                String jsonStr = sh.makeServiceCall(url_user_start_loc, ServiceHandler.GET);

                Log.d("Response: ", "> " + jsonStr);

                if (jsonStr != null) {
                    try{
                        JSONObject jobj = new JSONObject(jsonStr);
                        JSONObject responseObj = jobj.getJSONObject("response");
                        //JSONObject currentLo = responseObj.getJSONObject("url_code");
                        url_code  =  responseObj.getString("url_code");
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

            if(url_code!=null)

            {
                //storing
                SharedPreferences.Editor editor = getSharedPreferences(LOC_PREFS, MODE_PRIVATE).edit();
                editor.putString("url_code_from_sp", url_code);
                editor.commit();
            }

            callAsynchronousTask();

            btnstop = (FloatingActionButton) findViewById(R.id.btnstop);
            btnreshare = (FloatingActionButton) findViewById(R.id.btnreshare);
            btnshare = (FloatingActionButton) findViewById((R.id.btnshare));
            if(url_code != null) {
                sharingIntent.setType("text/plain");
                String shareBody = "Check you friend location status here "+urlDomain+"/current_loc.php?action=getLocation&url_code="+url_code;
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Terminus Share Location");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                startActivityForResult(sharingIntent, 0);
            }else{
                Toast.makeText(MapsMasterActivity.this, "Please click on share location again.", Toast.LENGTH_LONG).show();
            }
            if (btnstop.getVisibility() == View.GONE && btnreshare.getVisibility() == View.GONE) {

                // Either gone or invisible
                btnstop.setVisibility(View.VISIBLE);
                btnreshare.setVisibility(View.VISIBLE);
            }

            if (btnshare.getVisibility() == View.VISIBLE) {
                // Either gone or invisible
                btnshare.setVisibility(View.GONE);
            }




//            if(url_code != null) {
//                sharingIntent.setType("text/plain");
//                String shareBody = "Check you friend location status here"+url_code;
//                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Outlandish Share Location");
//                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
//                startActivity(Intent.createChooser(sharingIntent, "Share via"));
//                startActivityForResult(sharingIntent, 0);
//            }else{
//                Toast.makeText(MapsMasterActivity.this, "Please click on share location again.", Toast.LENGTH_LONG).show();
//            }

            //show stop button





//            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//                        sendIntent.setData(Uri.parse("sms:"));
//                      sendIntent.putExtra("sms_body", currentLoc);
//            startActivity(sendIntent);
//            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//            sharingIntent.setType("text/plain");
//            String shareBody = "Here is the share content body";
//            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
//            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
//            startActivity(Intent.createChooser(sharingIntent, "Share via"));


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

            String url_current_start_loc =  urlDomain+"/current_loc.php?action=send_current_loc&current_loc="+finalLoc+"&url_code="+url_code;

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

               // Toast.makeText(MapsMasterActivity.this, "Saved", Toast.LENGTH_LONG).show();
            }else{
              //  Toast.makeText(MapsMasterActivity.this, "Not Saved", Toast.LENGTH_LONG).show();
            }

        }

    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error." + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();

    }


    /**
     * Async task class to get json by making HTTP call
     * */
    private class StopShareLocation extends AsyncTask<Void, Void, Void> {
        Boolean isLSStopped = false;
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

            String url_user_stop_loc = null;
            if(url_code!=null) {
                url_user_stop_loc = urlDomain + "/user_start_loc.php?action=stop_share_location&url_code=" + url_code;
            }


            String jsonStr = sh.makeServiceCall(url_user_stop_loc, ServiceHandler.GET);

            if (jsonStr != null) {
                try{
                    JSONObject jobj = new JSONObject(jsonStr);
                   if(jobj.getString("status").equals("OK") && jobj.getString("message").equals("success")){
                       isLSStopped = true;
                   }
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

            if(isLSStopped)
            {
                Toast.makeText(MapsMasterActivity.this, "Stopped.", Toast.LENGTH_LONG).show();
            }
        }

    }
    private class updateEndLoc extends AsyncTask<Void, Void, Void> {
        Boolean isupadteendloc = false;
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

            String updateEndLocURL = null;
            if(url_code!=null) {
                try {
                    if(clrbtnpress==true) {
                        updateEndLocURL = urlDomain + "/user_start_loc.php?action=updateEndLoc&url_code=" + url_code + "&end_loc=null)";

                    }else {
                        updateEndLocURL = urlDomain + "/user_start_loc.php?action=updateEndLoc&url_code=" + url_code + "&end_loc=" + URLEncoder.encode(endPointLatLong, "utf-8");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }


            String jsonStr = sh.makeServiceCall(updateEndLocURL, ServiceHandler.GET);

            if (jsonStr != null) {
                try{
                    JSONObject jobj = new JSONObject(jsonStr);
                    if(jobj.getString("status").equals("OK") && jobj.getString("message").equals("success")){
                        isupadteendloc = true;
                    }
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

            if(isupadteendloc)
            {
                Toast.makeText(MapsMasterActivity.this, "Update end Location.", Toast.LENGTH_LONG).show();

            }

        }

    }


}
