package com.viral.omshiv.daytrackviralp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.viral.omshiv.Services.FlurryAnalytics;

import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends Activity implements AdapterView.OnItemSelectedListener {


    protected EditText Name;
    protected EditText DateOfBirth;
    protected EditText Enail;
    protected EditText CreateId;
    protected EditText Password;
    protected EditText age;
    protected EditText lastname;
    protected Button RegisterButton;
    protected TextView AllreadyHaveanAccount;
    private TextToSpeech speech;
    private int mYear;
    private int mMonth;
    private int mDay;
    static final int DATE_DIALOG_ID = 1;
    String Gender;


    Spinner spinnerGender;

    private String[] state = {"Male", "Female"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user", ParseUser.getCurrentUser());
        installation.saveInBackground();
        installation.put("User",true);
        installation.saveInBackground();

        Name = (EditText) findViewById(R.id.Name);
        DateOfBirth = (EditText) findViewById(R.id.DateofBirth);
        Enail = (EditText) findViewById(R.id.EmailAddress);
        CreateId = (EditText) findViewById(R.id.CreateID);
        Password = (EditText) findViewById(R.id.Password);
        lastname = (EditText) findViewById(R.id.LastName);


        spinnerGender = (Spinner) findViewById(R.id.mygender);

        ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, state);
        adapter_state
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter_state);
        spinnerGender.setOnItemSelectedListener(this);


        DateOfBirth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        updateDisplay();

        // when user press already have an account it will take user to login page
        AllreadyHaveanAccount = (TextView) findViewById(R.id.AllReadyAccount);


        AllreadyHaveanAccount.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Switching to Login Screen/closing register screen
                finish();
            }
        });


        RegisterButton = (Button) findViewById(R.id.SignUpButton);
        // LISTEN TO REGISTER BUTTON CLICK

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the username, password and email and convert them to string
                String pName = Name.getText().toString().trim();
                String pDateOfBirth = DateOfBirth.getText().toString().trim();
                String pEmail = Enail.getText().toString().trim();
                String PCid = CreateId.getText().toString().trim();
                String pPassword = Password.getText().toString().trim();
                String plastName = lastname.getText().toString().trim();
                ParseUser user = new ParseUser();

                


                if (pDateOfBirth.equals("")||  pName.equals("")
                    || plastName.equals("")|| PCid.equals("")){

                    speech=new TextToSpeech(RegisterActivity.this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status != TextToSpeech.ERROR){
                                speech.setLanguage(Locale.UK);
                                String toSpeak =("Please provide all the Details");
                                speech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                                Toast.makeText(RegisterActivity.this, toSpeak,
                                Toast.LENGTH_SHORT).show();


                            }
                        }
                    });

                } else if(!isValidEmail(pEmail)){
                    speech=new TextToSpeech(RegisterActivity.this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status != TextToSpeech.ERROR){
                                speech.setLanguage(Locale.UK);
                                String toSpeak =("Please provide correct Email");
                                speech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                                Toast.makeText(RegisterActivity.this, toSpeak,
                                        Toast.LENGTH_SHORT).show();


                            }
                        }
                    });

                } else if (!isValidPassword(pPassword)){

                    speech=new TextToSpeech(RegisterActivity.this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status != TextToSpeech.ERROR){
                                speech.setLanguage(Locale.UK);
                                String toSpeak =("Password must be between 7 and 21");
                                speech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                                Toast.makeText(RegisterActivity.this, toSpeak,
                                        Toast.LENGTH_SHORT).show();


                            }
                        }
                    });
                    
                    
                }  else{


                    user.setPassword(pPassword);
                    user.setUsername(PCid);
                    user.setEmail(pEmail);
                    user.put("LastName", plastName);
                    user.put("Name", pName);
                    user.put("DateofBirth", pDateOfBirth);
                    user.put("Gender", Gender);


                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                // user signed up successfully

                                Toast.makeText(RegisterActivity.this, "Success ! Welcome", Toast.LENGTH_LONG).show();
                                // take user homepage
                                Intent takeUserHome = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(takeUserHome);

                            } else {
                                //there was an error signing up user.

                            }
                        }
                    });


                }


            }
        });};
    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() >= 7 && pass.length() <= 21 ){
            return true;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {

            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
        }
        return null;
    }

    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {

            case DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
                break;
        }
    }

    private void updateDisplay() {
        DateOfBirth.setText(
                new StringBuilder()
                        // Month is sunny based so add 1
                        .append(mDay).append("/")
                        .append(mMonth + 1).append("/")
                        .append(mYear).append(" "));
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
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
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RegisterActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerGender.setSelection(position);
        String selState = (String) spinnerGender.getSelectedItem();
        Gender = selState;

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {


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
