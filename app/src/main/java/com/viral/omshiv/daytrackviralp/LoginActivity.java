package com.viral.omshiv.daytrackviralp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.viral.omshiv.Services.ConnectionDetector;
import com.viral.omshiv.Services.FlurryAnalytics;

import java.util.Arrays;
import java.util.List;


public class LoginActivity extends Activity {

    protected EditText mUsername;
    protected EditText mPassword;
    protected Button mLoginBtn;
    protected Button mCreateAccountBtn;
    protected Toast toast;
    protected Button PassReset;
    Boolean isInternetPresent = true;
    ConnectionDetector cd;
    private Dialog progressDialog;

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LoginActivity.this.finish();

                    }
                })
                .setNegativeButton("No", null)
                .show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);

    }
    public void onLoginClick(View v) {
        progressDialog = ProgressDialog.show(LoginActivity.this, "", "Logging in...", true);

        List<String> permissions = Arrays.asList("public_profile", "email");
        // NOTE: for extended permissions, like "user_about_me", your app must be reviewed by the Facebook team
        // (https://developers.facebook.com/docs/facebook-login/permissions/)

        ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                progressDialog.dismiss();
                if (user == null) {
                    Toast.makeText(LoginActivity.this, "Incorrect Facebook Login", Toast.LENGTH_SHORT).show();
                } else if (user.isNew()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();

                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        ParseAnalytics.trackAppOpened(getIntent());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        cd = new ConnectionDetector(getApplicationContext());

        //initialize
        mUsername = (EditText) findViewById(R.id.usernameLoginTextBox);
        mPassword = (EditText) findViewById(R.id.passwordLoginTextBox);
        mLoginBtn = (Button) findViewById(R.id.loginBtn);
        mCreateAccountBtn = (Button) findViewById(R.id.createAccountbtnLogin);
        PassReset = (Button)findViewById(R.id.reset);

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user", ParseUser.getCurrentUser());
        installation.saveInBackground();
        installation.put("User",true);
        installation.saveInBackground();





    //listen to when the mLogin button is click

        mLoginBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                //
                // flurry implementation when user click login button
                //  progressDialog = ProgressDialog.show(LoginActivity.this, "", "Logging in...", true);
                FlurryAgent.logEvent("Login Clicked");
                ConnectionDetector cd;
                // creating connection detector class instance
                cd = new ConnectionDetector(getApplicationContext());
                // get Internet status
                isInternetPresent = cd.isConnectingToInternet();
                // check for Internet status

                if (isInternetPresent) {


                    // get the user inputs from edit text and convert to string
                    final String username = mUsername.getText().toString().trim();
                    String password = mPassword.getText().toString().trim();

                    // Intent takeUserHome = new Intent(LoginActivity.this, MainActivity.class);
                    mUsername.getText().clear();
                    mPassword.getText().clear();
                    // startActivity(takeUserHome);


                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {

                            if (e == null) {
                                // success !

                                Toast toast = Toast.makeText(LoginActivity.this, " Welcome ! " + username, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();


                                // Take user to homepage
                                Intent takeUserHome = new Intent(LoginActivity.this, MainActivity.class);
                                mUsername.getText().clear();
                                mPassword.getText().clear();
                                startActivity(takeUserHome);
                                LoginActivity.this.finish();
                            } else {
                                // sorry there is a login problem
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(e.getMessage());
                                builder.setTitle("Sorry");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });


                } else {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    showAlertDialog(LoginActivity.this, "No Internet Connection",
                            "Please check your internet connection.", false);

                }

            }
        });

        PassReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent takeUserRegister = new Intent(LoginActivity.this, PasswordReset.class);
                startActivity(takeUserRegister);

            }
        });

        mCreateAccountBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {


                ConnectionDetector cd;
                // creating connection detector class instance
                cd = new ConnectionDetector(getApplicationContext());
                // get Internet status
                isInternetPresent = cd.isConnectingToInternet();
                // check for Internet status
                if (isInternetPresent) {

                    Intent takeUserRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(takeUserRegister);
                    LoginActivity.this.finish();

                } else {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    showAlertDialog(LoginActivity.this, "No Internet Connection",
                            "Please check your internet connection.", false);


                }
            }


        });


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




    public void onStart() {

        FlurryAnalytics.startSession(this);
        super.onStart();


    }

    public void onStop() {

        FlurryAnalytics.stopSession(this);
        super.onStop();
    }
}

