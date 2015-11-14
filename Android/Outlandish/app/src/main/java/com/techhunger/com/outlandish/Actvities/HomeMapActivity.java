package com.techhunger.com.outlandish.Actvities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techhunger.com.outlandish.API.APIConfig;
import com.techhunger.com.outlandish.API.APIManager;
import com.techhunger.com.outlandish.Accessors.CommonResponse;
import com.techhunger.com.outlandish.Accessors.StartSharingResponse;
import com.techhunger.com.outlandish.Accessors.StopShareResponse;
import com.techhunger.com.outlandish.Adaptor.PlaceAutocompleteAdapter;
import com.techhunger.com.outlandish.R;
import com.techhunger.com.outlandish.SignupActivity;
import com.techhunger.com.outlandish.Utils.AppUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class HomeMapActivity extends FragmentActivity implements LocationListener,  GoogleApiClient.OnConnectionFailedListener  {

    GoogleMap googleMap;
    LocationManager locationManager;
    Location location = null;
    protected GoogleApiClient mGoogleApiClient;
    private AutoCompleteTextView mAutocompleteView;
    private PlaceAutocompleteAdapter mPlaceAutocompleteHolderAdapter;
    Place place;
    Button clearButton;
    FloatingActionButton btnstop = null;
    FloatingActionButton btnshare = null;
    FloatingActionButton btnreshare = null;
    FloatingActionButton btnmylocation = null;
    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
    Handler handler = new Handler();
    Runnable runnable =null;
   // Location location;

    boolean isGPSEnabled, isNetworkEnabled;
    double latitude, longitude;
    String bestProvider = null;
    String url_code = null;
    String endPointLatLong = null;
    Boolean clrbtnpress=false;
    Boolean chkshared = false;
    String uid = null;
    String name = null;
    String finalLoc;


    public static final long MIN_TIME_BW_UPDATES = 0;
    // The minimum distance to change updates in metters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
    public static final String PREFS_NAME = "UserData";
    public static final String LOC_PREFS = "LocationData";

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        setContentView(R.layout.activity_home_map);
        //show error dialog if GoolglePlayServices not available
        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap = supportMapFragment.getMap();


        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            googleMap.setMyLocationEnabled(true);
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            uid = prefs.getString("uid", null);
            name = prefs.getString("name", null);

            onClickButtonListener();
            autoPlaces();

        }else {
            //popup box
            Toast.makeText(HomeMapActivity.this, "Please check your internet connection.", Toast.LENGTH_LONG).show();
        }
       // getLastLocation();

    }

    public void turnOnLocationService(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,  final int id) {
                        dialog.dismiss();
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        //below kept because on resume also being called in created
        getLastLocation();

    }

    @Override
    public void onBackPressed() {
        createDialog();
    }
    private void createDialog(){

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        HomeMapActivity.super.onBackPressed();
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
        final android.app.AlertDialog alert = builder.create();
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

        LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;

        mPlaceAutocompleteHolderAdapter = new PlaceAutocompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, bounds, null);
        mAutocompleteView.setAdapter(mPlaceAutocompleteHolderAdapter);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        // Set up the 'clear text' button that clears the text in the autocomplete view
        clearButton = (Button) findViewById(R.id.button_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutocompleteView.setText("");
                googleMap.clear();
                endPointLatLong = null;
                clrbtnpress = true;
                if (url_code != null) {
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
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mPlaceAutocompleteHolderAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i("", "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            clrbtnpress = false;
            AppUtils.hideSoftKeyboard(HomeMapActivity.this);


            Log.i("", "Called getPlaceById to get Place details for " + item.placeId);
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
                Log.e("", "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.

            place = places.get(0);
            // Format details of the place for display and show it in a TextView.
            endPointLatLong = place.getLatLng().toString();
            googleMap.addMarker(new MarkerOptions()
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

//    private void getLastLocation(){
//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        if(!isGPSEnabled && !isNetworkEnabled) {
//            turnOnLocationService(HomeMapActivity.this);
//            Toast.makeText(HomeMapActivity.this,"Provider needed",Toast.LENGTH_SHORT).show();
//        }else{
//            Criteria criteria = new Criteria();
//            String bestProvider = locationManager.getBestProvider(criteria, true);
//            Location location = locationManager.getLastKnownLocation(bestProvider);
//            if (location != null) {
//                onLocationChanged(location);
//            }
//            locationManager.requestLocationUpdates(bestProvider, 20000, 0, HomeMapActivity.this);
//        }
//    }

        private void getLastLocation(){
            location = getLocation();
            if (location != null) {
                onLocationChanged(location);
            }
            if(bestProvider != null) {
                locationManager.requestLocationUpdates(bestProvider, 20000, 0, HomeMapActivity.this);
            }
        }



    public Location getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext()
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                turnOnLocationService();
            } else {
              //  this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            bestProvider = LocationManager.NETWORK_PROVIDER;
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                bestProvider = LocationManager.GPS_PROVIDER;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_map, menu);
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

//    @Override
//    public void onMapReady(GoogleMap map) {
//        // Add a marker in Sydney, Australia, and move the camera.
////        LatLng sydney = new LatLng(-34, 151);
////        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
////        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//
//    }

    @Override
    public void onLocationChanged(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        Toast.makeText(HomeMapActivity.this, "Latitude:" + latitude + ", Longitude:" + longitude,Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

        Toast.makeText(HomeMapActivity.this, "Please enable the location", Toast.LENGTH_SHORT).show();
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("MapMaster", "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error." + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
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
                                                    Intent intent = new Intent(HomeMapActivity.this, SignupActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }else {
                                                //popup box
                                                Toast.makeText(HomeMapActivity.this, "Please check your internet connection.", Toast.LENGTH_LONG).show();


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
                            Intent intent = new Intent(HomeMapActivity.this, SignupActivity.class);
                            startActivity(intent);
                            finish();
                        }


                    }


                }
        );

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
                    Toast.makeText(HomeMapActivity.this, "End Location updated.", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(HomeMapActivity.this, error.toString(), Toast.LENGTH_LONG).show();
        }
    };

    public void doStopShare(String urlCode){
        APIManager apiManager = new APIManager(getApplicationContext());
        apiManager.doStopShare(successListener, errorListener, urlCode);
    }

    Response.Listener<StopShareResponse> successListener = new Response.Listener<StopShareResponse>(){

        @Override
        public void onResponse(StopShareResponse response) {
            if(response !=null){
                if(response.getStatus().equals("OK")){
                    Toast.makeText(HomeMapActivity.this,response.getMessage(),Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(HomeMapActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };


    private void doStartSharing() {
        if (location != null){
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        finalLoc = String.valueOf(latitude) + "," + String.valueOf(longitude);

        APIManager apiManager = new APIManager(getApplicationContext());
        String endLoc = null;
        if (endPointLatLong != null) {
            try {
                endLoc = URLEncoder.encode(endPointLatLong, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            endLoc = "0";
        }

        apiManager.doStartSharing(successStartShareListener, errorListener, finalLoc, endLoc, uid);
    }
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
                    Toast.makeText(HomeMapActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };



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

    private void doUpdateCurrentLoc(){
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String currentLoc = String.valueOf(latitude) + "," + String.valueOf(longitude);

            APIManager apiManager = new APIManager(getApplicationContext());
            apiManager.doUpdateCurrentLoc(successUpdateCurrentLocListener, errorListener, currentLoc, url_code);
        }
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
}
