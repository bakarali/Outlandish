package com.techhunger.com.outlandish;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
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
import com.techhunger.com.outlandish.API.APIConfig;
import com.techhunger.com.outlandish.API.APIManager;
import com.techhunger.com.outlandish.Accessors.CommonResponse;
import com.techhunger.com.outlandish.Accessors.StartSharingResponse;
import com.techhunger.com.outlandish.Accessors.StopShareResponse;
import com.techhunger.com.outlandish.Adaptor.PlaceAutocompleteAdapter;
import com.techhunger.com.outlandish.Utils.AppUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MapsMasterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
private static final String TAG = "AutoPlace";
protected GoogleApiClient mGoogleApiClient;

private PlaceAutocompleteAdapter mAdapter;
    private Toolbar mToolbar;
private AutoCompleteTextView mAutocompleteView;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private  static final int ZOOM = 15;



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
    public static final String PREFS_NAME = "UserData";
    public static final String LOC_PREFS = "LocationData";
    Place place;


    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_maps_master);
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
                            //new StopShareLocation().execute();
                            if(url_code != null) {
                                doStopShare(url_code);
                            }
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
                                //new GetUserStartLocation().execute();
                                doStartSharing();
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
                                String shareBody = "Check you friend location status here " + APIConfig.HOST + "/current_loc.php?action=getLocation&url_code=" + url_code;
                                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Terminus Share Location");
                                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                                startActivityForResult(sharingIntent, 0);
                                chkshared = true;

                            } else {
                               // new GetUserStartLocation().execute();
                                doStartSharing();
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

    public void doStopShare(String urlCode){
        APIManager apiManager = new APIManager(getApplicationContext());
        apiManager.doStopShare(successListener, errorListener, urlCode);
    }

    Response.Listener<StopShareResponse> successListener = new Response.Listener<StopShareResponse>(){

        @Override
        public void onResponse(StopShareResponse response) {
            if(response !=null){
                if(response.getStatus().equals("OK")){
                    Toast.makeText(MapsMasterActivity.this,response.getMessage(),Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(MapsMasterActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(MapsMasterActivity.this, error.toString(), Toast.LENGTH_LONG).show();
        }
    };

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
                        if (url_code != null) {
                            doStopShare(url_code);
                        }
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
                    //new updateEndLoc().execute();
                    doUpdateEndLoc();
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
            clrbtnpress = false;
            AppUtils.hideSoftKeyboard(MapsMasterActivity.this);


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
                //new updateEndLoc().execute();
                doUpdateEndLoc();

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
                    doUpdateCurrentLoc();
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

              mMap.setMyLocationEnabled(true);

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
                                                     }
                                                 }

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



    private void doStartSharing(){
        Location myLocation = getLastKnownLocation();
        double latitude = myLocation.getLatitude();
        double longitude = myLocation.getLongitude();
        finalLoc = String.valueOf(latitude)+","+String.valueOf(longitude);

        APIManager apiManager = new APIManager(getApplicationContext());
        String endLoc = null;
        if(endPointLatLong != null){
            try {
                endLoc = URLEncoder.encode(endPointLatLong, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else{
            endLoc = "0";
        }

        apiManager.doStartSharing(successStartShareListener, errorListener, finalLoc, endLoc, uid);
    }


    Response.Listener<StartSharingResponse> successStartShareListener = new Response.Listener<StartSharingResponse>(){

        @Override
        public void onResponse(StartSharingResponse response) {
            if(response !=null){
                if(response.getStatus().equals("OK")){
                    if(response.getResponse().getUrlCode() != null){
                        url_code = response.getResponse().getUrlCode();
                        //storing
                        SharedPreferences.Editor editor = getSharedPreferences(LOC_PREFS, MODE_PRIVATE).edit();
                        editor.putString("url_code_from_sp", url_code);
                        editor.commit();

                        callAsynchronousTask();

                        btnstop = (FloatingActionButton) findViewById(R.id.btnstop);
                        btnreshare = (FloatingActionButton) findViewById(R.id.btnreshare);
                        btnshare = (FloatingActionButton) findViewById((R.id.btnshare));

                            sharingIntent.setType("text/plain");
                            String shareBody = "Check you friend location status here "+APIConfig.HOST+"/current_loc.php?action=getLocation&url_code="+url_code;
                            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Terminus Share Location");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                            startActivity(Intent.createChooser(sharingIntent, "Share via"));
                            startActivityForResult(sharingIntent, 0);

                        if (btnstop.getVisibility() == View.GONE && btnreshare.getVisibility() == View.GONE) {
                            // Either gone or invisible
                            btnstop.setVisibility(View.VISIBLE);
                            btnreshare.setVisibility(View.VISIBLE);
                        }

                        if (btnshare.getVisibility() == View.VISIBLE) {
                            // Either gone or invisible
                            btnshare.setVisibility(View.GONE);
                        }

                    }
                }else
                    Toast.makeText(MapsMasterActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void doUpdateCurrentLoc(){
        Location myLocation = getLastKnownLocation();
        double latitude = myLocation.getLatitude();
        double longitude = myLocation.getLongitude();
        String currentLoc = String.valueOf(latitude)+","+String.valueOf(longitude);

        APIManager apiManager = new APIManager(getApplicationContext());
        apiManager.doUpdateCurrentLoc(successUpdateCurrentLocListener, errorListener, currentLoc, url_code);

    }

    Response.Listener<CommonResponse> successUpdateCurrentLocListener = new Response.Listener<CommonResponse>(){

        @Override
        public void onResponse(CommonResponse response) {
            if(response !=null){
                if(response.getStatus().equals("OK")){
                  //  Toast.makeText(MapsMasterActivity.this,response.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error." + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();

    }


    private void doUpdateEndLoc() {
        String endLoc;
        APIManager apiManager = new APIManager(getApplicationContext());
        try {
            if(url_code != null) {
                if(clrbtnpress) {
                    endLoc = "0";
                }else{
                    endLoc = URLEncoder.encode(endPointLatLong, "utf-8");
                }
                apiManager.doUpdateEndLoc(successUpdateEndLocListener, errorListener, endLoc, url_code);
            }
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }



    }


    Response.Listener<CommonResponse> successUpdateEndLocListener = new Response.Listener<CommonResponse>(){

        @Override
        public void onResponse(CommonResponse response) {
            if(response !=null){
                if(response.getStatus().equals("OK")){
                    Toast.makeText(MapsMasterActivity.this, "End Location updated.", Toast.LENGTH_LONG).show();
                }
            }
        }
    };


}
