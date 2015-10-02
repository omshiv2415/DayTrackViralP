package com.viral.omshiv.Services;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.viral.omshiv.daytrackviralp.MainActivity;
import com.viral.omshiv.daytrackviralp.R;

/**
 * Created by omshiv on 18/12/2014.
 */
public class Facebook_login extends Activity {

    Boolean isInternetPresent = true;
    ConnectionDetector cd;
    protected EditText mUsername;
    protected EditText mPassword;
    protected Button mLoginBtn;
    protected Button mCreateAccountBtn;
    protected Toast toast;
    protected Button FacebookLogin;
    private SharedPreferences mPrefs;

    private AsyncFacebookRunner mAsyncRunner;
    private static String APP_ID = "721954007887378"; // Replace your App ID here

    private Facebook facebook;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode==2000)
        {
            Intent myIntent = new Intent(Facebook_login.this,MainActivity.class);
            startActivity (myIntent);
            // finish(); //if you want to do not use this
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        loginToFacebook();
    }
    public void loginToFacebook() {
        mPrefs = getPreferences(MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);

        if (access_token != null) {
            facebook.setAccessToken(access_token);


        }

        if (expires != 0) {
            facebook.setAccessExpires(expires);


        }

        if (!facebook.isSessionValid()) {


            facebook.authorize(this,
                    new String[]{"email", "publish_stream"},
                    new Facebook.DialogListener() {

                        @Override
                        public void onCancel() {
                            // Function to handle cancel event
                        }

                        @Override
                        public void onComplete(Bundle values) {
                            // Function to handle complete event
                            // Edit Preferences and update facebook acess_token
                            SharedPreferences.Editor editor = mPrefs.edit();
                            editor.putString("access_token",
                                    facebook.getAccessToken());
                            editor.putLong("access_expires",
                                    facebook.getAccessExpires());
                            editor.commit();


                        }

                        @Override
                        public void onError(DialogError error) {
                            // Function to handle error

                        }

                        @Override
                        public void onFacebookError(FacebookError fberror) {
                            // Function to handle Facebook errors

                        }


                    });

        }
    }


}
