package com.viral.omshiv.Settings.History;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.viral.omshiv.Services.FlurryAnalytics;
import com.viral.omshiv.daytrackviralp.R;
import com.viral.omshiv.Settings.MainSetting.Main_Setting;

import java.util.ArrayList;
import java.util.List;




public class History_Home extends Activity {

    ListView listview;
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    History_ListViewAdapter adapter;
    private List<History_support> User_History = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        new RemoteDataTask().execute();
        ActionBar actionBar = getActionBar();

        actionBar.setHomeButtonEnabled(true);


    }
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(History_Home.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Parse.com Custom ListView Tutorial");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            // Create the array
            User_History = new ArrayList<History_support>();
            try {
                // Locate the class table named "Country" in Parse.com
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                        "Activity");
                // query.whereEqualTo("createdBy",  ParseUser.getCurrentUser());
                query.whereEqualTo("createdBy", ParseUser.getCurrentUser());

                ob = query.find();
                for (ParseObject Prescription_Items : ob) {
                    History_support map = new History_support();
                    map.setStart_Time((String) Prescription_Items.get("StartTime"));
                    map.setFinish_Time((String) Prescription_Items.get("FinishTime"));
                    map.setCalories_Burn((String) Prescription_Items.get("Calories_Burn"));
                    map.setDistance_Covered((String) Prescription_Items.get("Distance"));
                    map.setStep_Taken((String) Prescription_Items.get("Steps"));
                    map.setElapsed_Time((String) Prescription_Items.get("ElapsedTime"));

                    User_History.add(map);
                }
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            listview = (ListView) findViewById(R.id.HistoryListView);
            // Pass the results into Booking_ListViewAdapter.java
            adapter = new History_ListViewAdapter(History_Home.this,
                    User_History);
            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
            mProgressDialog.dismiss();
        }


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
                // app icon in action bar clicked go home

                Intent intent = new Intent(this, Main_Setting.class);
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
        super.onStop();
        FlurryAnalytics.stopSession(this);
        this.finish();
    }

}
