package com.viral.omshiv.Settings.MainSetting;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.viral.omshiv.Services.FlurryAnalytics;
import com.viral.omshiv.Settings.History.History_Home;
import com.viral.omshiv.daytrackviralp.MainActivity;
import com.viral.omshiv.daytrackviralp.R;


public class Main_Setting extends Activity {


    protected Button MyDetailsBtn;
    protected Button HistorySetBtn;
    protected Button AdditionalsetBtn;

    private Switch Weather;
    public Switch TexttoSpeech;
    private Switch Music;
    public Switch GpsSwitch;
    static final int TIME_DIALOG_ID = 1111;
    private EditText startTime;
    public Button btnClick;
    private int hour;
    private int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);

        MyDetailsBtn = (Button) findViewById(R.id.MyDetails);
        HistorySetBtn = (Button) findViewById(R.id.HistorySet);
        AdditionalsetBtn = (Button) findViewById(R.id.AdditionalSet);
        GpsSwitch = (Switch) findViewById(R.id.GpsSwitch);


        GpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {

                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }

                } else {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            }
        });


        MyDetailsBtn.setOnClickListener
                (new View.OnClickListener() {


                     @Override
                     public void onClick(View view) {

                         Intent takeUserRegister = new Intent(Main_Setting.this, Personal_Details.class);
                         startActivity(takeUserRegister);

                     }
                 }

                );


        HistorySetBtn.setOnClickListener
                (new View.OnClickListener() {


                     @Override
                     public void onClick(View view) {

                         Intent takeUserRegister = new Intent(Main_Setting.this, History_Home.class);
                         startActivity(takeUserRegister);

                     }
                 }

                );


        AdditionalsetBtn.setOnClickListener
                (new View.OnClickListener() {


                     @Override
                     public void onClick(View view) {

                         Intent takeUserRegister = new Intent(Main_Setting.this, AdditionalSettingActivity.class);
                         startActivity(takeUserRegister);

                     }
                 }

                );


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void onStart() {

        FlurryAnalytics.startSession(this);

        super.onStart();

    }

    public void onStop() {

        FlurryAnalytics.stopSession(this);
        super.onStop();
    }


}
