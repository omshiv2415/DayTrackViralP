package com.viral.omshiv.daytrackviralp;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.viral.omshiv.Services.ConnectionDetector;
import com.viral.omshiv.Services.DirectionsJsonParser;
import com.viral.omshiv.Services.FlurryAnalytics;
import com.viral.omshiv.Services.MyService;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RunActivity extends FragmentActivity implements LocationListener,
        SensorEventListener, TextToSpeech.OnInitListener {

    public static final String PREFS_NAME_START = "MyPreferencesFile1";
    public static final String PREFS_NAME = "MyPreferencesFile";
    private static final String FORMAT = "%02d:%02d:%02d";
    final int MODE_BICYCLING = 1;
    final int MODE_WALKING = 2;
    final int Live_Trafic = 3;
    GoogleMap googleMap;
    Boolean isInternetPresent = true;
    ArrayList<LatLng> markerPoints;
    int mMode = 0;
    RadioButton rbBiCycling;
    RadioButton rbWalking;
    RadioButton rbLiveTrafic;
    RadioGroup rgModeRun;
    private TextView StepCount;
    private TextView ElapsedTime;
    private TextView Miles;
    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;
    private Sensor mStepDetectorSensor;
    private TextView CaloriesBurn;
    private TextToSpeech speech;
    Chronometer myChronometer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        //Following line of code awake the screen
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myChronometer = (Chronometer) findViewById(R.id.chrommeter);
        FlurryAgent.setReportLocation(true);
        SharedPreferences setting = getSharedPreferences(PREFS_NAME, 0);

        String stime = setting.getString("StartTime", "");
        String ftime = setting.getString("FinishTime", "");
        // finish = (Button)findViewById(R.id.finish);
        // setting up sensor from the system
        Miles = (TextView) findViewById(R.id.pace_value);
        StepCount = (TextView) findViewById(R.id.step_value);
        CaloriesBurn = (TextView) findViewById(R.id.calories_value);
        ElapsedTime = (TextView) findViewById(R.id.speed_value);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        speech = new TextToSpeech(this, this);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Miles.setText("000");
        StepCount.setText("000");
        CaloriesBurn.setText("000");
        Typeface c = Typeface.createFromAsset(getAssets(), "fonts/digital-7.ttf");
        StepCount.setTypeface(c);
        StepCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);
        ElapsedTime.setTypeface(c);
        ElapsedTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        CaloriesBurn.setTypeface(c);
        CaloriesBurn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        Miles.setTypeface(c);
        Miles.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        myChronometer.setTypeface(c);
        myChronometer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);

        @SuppressLint("AppCompatMethod") ActionBar bar = getActionBar();
        assert bar != null;
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#339933")));
        myChronometer.start();


        //  loginToFacebook();
        rbBiCycling = (RadioButton) findViewById(R.id.rb_bike);

        // Getting reference to rb_walking
        rbWalking = (RadioButton) findViewById(R.id.rb_walk);

        // Getting Reference to rg_modes
        rgModeRun = (RadioGroup) findViewById(R.id.rg_Run);

        // Getting Reference to rg_Live Traffic

        rbLiveTrafic = (RadioButton) findViewById(R.id.rb_Trafic);


        rgModeRun.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 2) {
                    LatLng origin = markerPoints.get(0);
                    LatLng dest = markerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }
            }
        });


        // Initializing
        markerPoints = new ArrayList<LatLng>();

        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting Map for the SupportMapFragment
        googleMap = fm.getMap();

        // Enable MyLocation Button in the Map
          googleMap.setMyLocationEnabled(true);

        // Main_Setting onclick event listener for the map
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {


            @Override
            public void onMapClick(LatLng point) {

                ConnectionDetector cd;
                // creating connection detector class instance
                cd = new ConnectionDetector(getApplicationContext());
                // get Internet status
                isInternetPresent = cd.isConnectingToInternet();
                // check for Internet status
                if (isInternetPresent) {


                    // Already two locations
                    if (markerPoints.size() > 1) {
                        markerPoints.clear();
                        googleMap.clear();
                    }

                    // Adding new item to the ArrayList
                    markerPoints.add(point);


                    // Draws Start and Stop markers on the Google Map
                    drawStartStopMarkers();


                    // Checks, whether start and end locations are captured
                    if (markerPoints.size() >= 2) {
                        LatLng origin = markerPoints.get(0);
                        LatLng dest = markerPoints.get(1);

                        // Getting URL to the Google Directions API
                        String url = getDirectionsUrl(origin, dest);

                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);
                    }

                } else {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    showAlertDialog(RunActivity.this, "No Internet Connection",
                            "You don't have internet connection.", false);


                }


            }


        });


        // getting value from additionalsetting and put it into string

        SimpleDateFormat df = new SimpleDateFormat("hh:mm aa");


        if (stime == "" && ftime == "") {

            stime = "10:00 AM";
            ftime = "11:00 AM";


            //puting inside date and format am and pm in to time


            Date date1 = null;
            try {
                date1 = df.parse(stime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date date2 = null;
            try {
                date2 = df.parse(ftime);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            //findidng a Elapsedtime in milli seconds
            long difference = date2.getTime() - date1.getTime();


            difference = (difference < 0 ? -difference : difference);


            new CountDownTimer(difference, 1000) { // adjust the milli seconds here

                public void onTick(long millisUntilFinished) {

                    ElapsedTime.setText("" + String.format(FORMAT,
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                }

                public void onFinish() {
                    ElapsedTime.setText("done!");
                }
            }.start();

        } else {
            Date date1 = null;
            try {
                date1 = df.parse(stime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date date2 = null;
            try {
                date2 = df.parse(ftime);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            //findidng a Elapsedtime in milli seconds
            long difference = date2.getTime() - date1.getTime();


            difference = (difference < 0 ? -difference : difference);


            new CountDownTimer(difference, 1000) { // adjust the milli seconds here

                public void onTick(long millisUntilFinished) {

                    ElapsedTime.setText("" + String.format(FORMAT,
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                }

                public void onFinish() {
                    ElapsedTime.setText("done!");
                }
            }.start();


        }


        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());


        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
            myChronometer.start();


        } else { // Google Play Services are available

            // Getting reference to the SupportMapFragment of activity_main.xml
            fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);


            // Getting GoogleMap object from the fragment
            googleMap = fm.getMap();

            // Enabling MyLocation Layer of Google Map
            googleMap.setMyLocationEnabled(true);
            googleMap.setBuildingsEnabled(true);
            googleMap.setIndoorEnabled(true);


            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(provider, 20000, 0, this);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap = mapFragment.getMap();

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        RadioGroup rgViews = (RadioGroup) findViewById(R.id.rg_views);

        rgViews.setOnCheckedChangeListener
                (new RadioGroup.OnCheckedChangeListener() {

                     @Override
                     public void onCheckedChanged(RadioGroup group, int checkedId) {


                         if (checkedId == R.id.rb_normal) {
                             googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                         } else if (checkedId == R.id.rb_satellite)

                         {
                             googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                         } else if (checkedId == R.id.rb_terrain)

                         {
                             googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                         }
                     }

                     ;
                 }

                );


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
    }
    // Step 3: Implement SensorEventListener.onSensorChanged  method

    public void onSensorChanged(SensorEvent event) {
        SharedPreferences setting = getSharedPreferences(PREFS_NAME, 0);

        Sensor sensor = event.sensor;
        float[] values = event.values;


        int value = -1;


        DecimalFormat df = new DecimalFormat("#.###");
        String myheight = setting.getString("Height", "");
        String myweight = setting.getString("Weight", "");


        if (myweight == "") {

            myweight = "70";
            double onestep = (0.0004734848484848485);//one step in mile

            // Calorie calculations from equation: (METs x 3.5 x body weight in kg)/200 = calories/minute
            double oneSetpCalPerOneKilo = 1.32352941;
            // if user walk 1 mile and weight is 1 kg he/she will burn 1.32352941cal
            double calTest = Double.parseDouble(myweight);

            double oneStepCalories = (oneSetpCalPerOneKilo * calTest) / (2112);
            // 2112 steps in one mile if steps length is 30 inches
            // if user weight is 70 kilogram and walk 1 mile 70*1.3252941 = 92.40 calories burn
            // in one mile Total steps are 2112
            if (values.length > 0) {
                value = (int) values[0];
            }
            if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {

                StepCount.setText("" + value);
                Miles.setText("" + df.format(onestep * value));
                CaloriesBurn.setText("" + df.format(oneStepCalories * value));

            } else if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {

                StepCount.setText("" + value);
                Miles.setText("" + df.format(onestep * value));
                CaloriesBurn.setText("" + df.format(oneStepCalories * value));
            }

        } else {
            double onestep = (0.0004734848484848485);//one step in mile

            // Calorie calculations from equation: (METs x 3.5 x body weight in kg)/200 = calories/minute
            double oneSetpCalPerOneKilo = 1.32352941;
            // if user walk 1 mile and weight is 1 kg he/she will burn 1.32352941cal
            double calTest = Double.parseDouble(myweight);

            double oneStepCalories = (oneSetpCalPerOneKilo * calTest) / (2112);
            // 2112 steps in one mile if steps length is 30 inches
            // if user weight is 70 kilogram and walk 1 mile 70*1.3252941 = 92.40 calories burn
            // in one mile Total steps are 2112
            if (values.length > 0) {
                value = (int) values[0];
            }
            if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {

                StepCount.setText("" + value);
                Miles.setText("" + df.format(onestep * value));
                CaloriesBurn.setText("" + df.format(oneStepCalories * value));

            } else if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {

                StepCount.setText("" + value);
                Miles.setText("" + df.format(onestep * value));
                CaloriesBurn.setText("" + df.format(oneStepCalories * value));
            }


        }

    }


    // Step 4: Register and Unregister Sensors

    protected void onResume() {

        super.onResume();
        mSensorManager.registerListener(this, mStepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);

    }


    // Drawing Start and Stop locations
    private void drawStartStopMarkers() {


        for (int i = 0; i < markerPoints.size(); i++) {

            // Creating MarkerOptions
            MarkerOptions options = new MarkerOptions();

            // Main_Setting the position of the marker
            options.position(markerPoints.get(i));

            /**
             * For the start location, the color of marker is GREEN and
             * for the end location, the color of marker is RED.
             */
            if (i == 0) {

                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                options.title("Start" + markerPoints.toString()).snippet("Starting Point");
                // editor.putString("StartLoc",markerPoints.add());

            } else if (i == 1) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                options.title("End" + markerPoints.toString()).snippet("Finishing Point");
            }

            // Add new marker to the Google Map Android API V2
            googleMap.addMarker(options);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Travelling Mode
        String mode = "mode=driving";


        ConnectionDetector cd;
        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());
        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status


        if (rbBiCycling.isChecked()) {


            if (isInternetPresent) {
                mode = "mode=bicycling";
                mMode = 1;
            } else {
                // Internet connection is not present
                // Ask user to connect to Internet
                showAlertDialog(RunActivity.this, "No Internet Connection",
                        "You don't have internet connection.", false);
            }
        } else if (rbWalking.isChecked()) {

            if (isInternetPresent) {
                mode = "mode=walking";
                mMode = 2;
            } else {
                // Internet connection is not present
                // Ask user to connect to Internet
                showAlertDialog(RunActivity.this, "No Internet Connection",
                        "You don't have internet connection.", false);
            }
        } else if (rbLiveTrafic.isChecked()) {
            if (isInternetPresent) {

                mode = "mode=LiveTraffic";
                mMode = 3;

            } else {
                // Internet connection is not present
                // Ask user to connect to Internet
                showAlertDialog(RunActivity.this, "No Internet Connection",
                        "You don't have internet connection.", false);


            }


        }

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }


    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onLocationChanged(final Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("Current Location");
        markerOptions.snippet("You are here");


        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        markerOptions.position(latLng);

        location.getLatitude();
        location.getAccuracy();
        //  location.distanceTo(location);
        // getting speed from location change
        //  Toast.makeText(RunActivity.this, "Current speed:" + location.getSpeed(), Toast.LENGTH_SHORT).show();


        speech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    speech.setLanguage(Locale.UK);


                    String toSpeak = "Your Speed is " + String.valueOf((location.getSpeed() * 2) + " miles per hour");
                    Toast.makeText(getApplicationContext(), toSpeak,
                            Toast.LENGTH_SHORT).show();
                    speech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                }
            }
        });


    }



    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        SharedPreferences setting1 = getSharedPreferences(PREFS_NAME, 0);
                        String stime = setting1.getString("StartTime", "");
                        String ftime = setting1.getString("FinishTime", "");
                        ParseObject time = new ParseObject("Activity");

                        if (stime.equals("") && ftime.equals("")) {

                        stime = "10:00 AM";
                        ftime = "11:00 AM";
                        ParseUser activity = ParseUser.getCurrentUser();
                        time.put("createdBy",activity);
                        time.put("Distance", Miles.getText().toString() + " Mile");
                        time.put("Steps", StepCount.getText().toString());
                        time.put("ElapsedTime", ElapsedTime.getText().toString());
                        time.put("Calories_Burn", CaloriesBurn.getText().toString());
                        time.put("Total_Time", myChronometer.getText().toString());
                        time.increment("User_Activity");
                        time.put("StartTime", stime);
                        time.put("FinishTime", ftime);
                        time.saveInBackground();
                        //ParseUser user = ParseUser.getCurrentUser();
                        // user.increment("Activity");
                        RunActivity.this.finish();


                    }else {

                            ParseUser activity = ParseUser.getCurrentUser();
                            time.put("createdBy",activity);
                            time.put("Distance", Miles.getText().toString() + " Mile");
                            time.put("Steps", StepCount.getText().toString());
                            time.put("ElapsedTime", ElapsedTime.getText().toString());
                            time.put("Calories_Burn", CaloriesBurn.getText().toString());
                            time.put("Total_Time", myChronometer.getText().toString());
                            //time.increment("User_Activity");
                            time.put("StartTime", stime);
                            time.put("FinishTime", ftime);
                            time.saveInBackground();
                            //ParseUser user = ParseUser.getCurrentUser();
                            // user.increment("Activity");
                            RunActivity.this.finish();

                        }


                    }
                })
                .setNegativeButton("No", null)
                .show();
        stopService(new Intent(this, MyService.class));


    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub


    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (id == R.id.action_logout) {
            FlurryAgent.endTimedEvent("Logout Session");

            SharedPreferences setting2 = getSharedPreferences(PREFS_NAME, 0);
            String stime = setting2.getString("StartTime", "");
            String ftime = setting2.getString("FinishTime", "");
            ParseObject time = new ParseObject("Activity");

            if (stime.equals("") && ftime.equals("")) {

                stime = "10:00 AM";
                ftime = "11:00 AM";
                ParseUser activity = ParseUser.getCurrentUser();
                time.put("createdBy", activity);
                time.put("Distance", Miles.getText().toString() + " Mile");
                time.put("Steps", StepCount.getText().toString());
                time.put("ElapsedTime", ElapsedTime.getText().toString());
                time.put("Calories_Burn", CaloriesBurn.getText().toString());
                time.put("Total_Time", myChronometer.getText().toString());
                //time.increment("User_Activity");
                time.put("StartTime", stime);
                time.put("FinishTime", ftime);
                time.saveInBackground();
                //ParseUser user = ParseUser.getCurrentUser();
                // user.increment("Activity");
                RunActivity.this.finish();
                Intent myIntent = new Intent(RunActivity.this, StartActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// clear back stack
                startActivity(myIntent);
                ParseUser.logOut();
                stopService(new Intent(this, MyService.class));
                return true;
            }
        }

            if (id == R.id.action_photo) {


                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, 0);


                return true;

            }

            if (id == R.id.action_gps) {


                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }

                return true;

            }

        if (id == R.id.action_voice)

        {
            speech.setLanguage(Locale.CANADA);

            return true;

        }
        if (id == R.id.action_voice_us)

        {
            speech.setLanguage(Locale.US);
            return true;

        }

        if (id == R.id.action_reset) {

            StepCount.setText("");
            CaloriesBurn.setText("");
            ElapsedTime.setText("");
            Miles.setText("");


            return true;
        }

         return super.onOptionsItemSelected(item);
    }

    public void showAlertDialog(Context context, String title, String message, Boolean status) {


        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Main_Setting Dialog Title
        alertDialog.setTitle(title);

        // Main_Setting Dialog Message
        alertDialog.setMessage(message);

        // Main_Setting alert dialog icon
        alertDialog.setIcon((status) ? R.drawable.success : R.drawable.error);

        // Main_Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        // Showing Alert Message
        alertDialog.show();

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onInit(int i) {


    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJsonParser parser = new DirectionsJsonParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;


            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);

                // Changing the color polyline according to the mode

                if (mMode == MODE_BICYCLING) {
                    lineOptions.color(Color.BLUE);
                    lineOptions.getWidth();
                    googleMap.setTrafficEnabled(false);
                } else if (mMode == MODE_WALKING) {
                    lineOptions.color(Color.BLACK);
                    lineOptions.getWidth();
                    googleMap.setTrafficEnabled(false);
                } else if (mMode == Live_Trafic) {
                    googleMap.setTrafficEnabled(true);
                    lineOptions.getWidth();
                } else
                    googleMap.setTrafficEnabled(false);

            }

            if (result.size() < 1) {
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Drawing polyline in the Google Map for the i-th route
            googleMap.addPolyline(lineOptions);
        }
    }

    public void onStart() {

        FlurryAnalytics.startSession(this);
        super.onStart();

    }

    public void onStop() {
        super.onStop();
        FlurryAnalytics.stopSession(this);

    }


}
