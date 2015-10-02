package com.viral.omshiv.Settings.MainSetting;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.viral.omshiv.Services.FlurryAnalytics;
import com.viral.omshiv.daytrackviralp.MainActivity;
import com.viral.omshiv.daytrackviralp.R;


public class AdditionalSettingActivity extends Activity implements AdapterView.OnItemSelectedListener {
    static final int TIME_DIALOG_ID = 1111;
    static final int FINISH_TIME_DIALOG_ID = 2;
    public EditText StartTime;
    public EditText Finishtime;
    protected Button Set;
    private int hour;
    public EditText MyWeight;
    public EditText MyHeight;
    private int minute;

    public static final String PREFS_NAME = "MyPreferencesFile";
    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_additional);
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);

        StartTime = (EditText) findViewById(R.id.starttime);
        Finishtime = (EditText) findViewById(R.id.finishtime);
        MyWeight = (EditText) findViewById(R.id.lastname);
        MyHeight = (EditText) findViewById(R.id.Height);

        Set = (Button) findViewById(R.id.setBtn);
        loadSavedPreferences();
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user", ParseUser.getCurrentUser());
        installation.saveInBackground();
        installation.put("User",true);
        installation.saveInBackground();

        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        StartTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });

        Finishtime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(FINISH_TIME_DIALOG_ID);
            }
        });

        Set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ST = StartTime.getText().toString();
                String FT = Finishtime.getText().toString();
                String MW = MyWeight.getText().toString();
                String MH = MyHeight.getText().toString();


                if (ST.equals("") || FT.equals("") || MW.equals("") || MH.equals(""))

                {

                    showAlertDialog();
                } else {
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("StartTime", StartTime.getText().toString());
                    editor.putString("FinishTime", Finishtime.getText().toString());
                    editor.putString("Height", MyHeight.getText().toString());
                    editor.putString("Weight", MyWeight.getText().toString());

                    editor.commit();
                    saveData();

                    Intent Start = new Intent(AdditionalSettingActivity.this, MainActivity.class);
                    startActivity(Start);
                    Toast.makeText(AdditionalSettingActivity.this, "Settings Updated Successfully", Toast.LENGTH_SHORT).show();


                }


            }
        });
    }

    private void loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        StartTime.setText(sharedPreferences.getString("string_et1", ""));
        Finishtime.setText(sharedPreferences.getString("string_et2", ""));
        MyHeight.setText(sharedPreferences.getString("string_et3", ""));
        MyWeight.setText(sharedPreferences.getString("string_et4", ""));

    }

    private void savePreferences(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void saveData() {
        savePreferences("string_et1", StartTime.getText().toString());
        savePreferences("string_et2", Finishtime.getText().toString());
        savePreferences("string_et3", MyHeight.getText().toString());
        savePreferences("string_et4", MyWeight.getText().toString());
    }

    @Override
    public void onBackPressed() {
        saveData();
        super.onBackPressed();
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:

                // set time picker as current time
                return new TimePickerDialog(this, timePickerListener, hour, minute,
                        false);
            case FINISH_TIME_DIALOG_ID:

                return new TimePickerDialog(this, finishtimePickerListener, hour, minute, false);
        }

        return null;
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {


        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            // TODO Auto-generated method stub
            hour = hourOfDay;
            minute = minutes;

            updateTime(hour, minute);

        }

    };
    private TimePickerDialog.OnTimeSetListener finishtimePickerListener = new TimePickerDialog.OnTimeSetListener() {


        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            // TODO Auto-generated method stub
            hour = hourOfDay;
            minute = minutes;

            updateTimefinish(hour, minute);

        }

    };




    private void updateTime(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";


        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();


        StartTime.setText(aTime);


    }

    private void updateTimefinish(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";


        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();


        Finishtime.setText(aTime);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.additional, menu);
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
                // app icon in action bar clicked go home
                saveData();
                Intent intent = new Intent(this, Main_Setting.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showAlertDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Please set all Field")
                .setNegativeButton("OK", null)
                .show();

    }


    public void onStart() {

        FlurryAnalytics.startSession(this);
        super.onStart();

    }

    public void onStop() {
        super.onStop();
        FlurryAnalytics.stopSession(this);
        this.finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
