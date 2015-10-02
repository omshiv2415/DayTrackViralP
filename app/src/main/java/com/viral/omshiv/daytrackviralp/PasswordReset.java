package com.viral.omshiv.daytrackviralp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

/**
 * Created by omshiv on 10/01/2015.
 */
public class PasswordReset extends Activity {
    private EditText EmailAddress;
    private Button Reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);




        EmailAddress = (EditText)findViewById(R.id.Email);
        Reset = (Button)findViewById(R.id.passreset);

        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = EmailAddress.getText().toString();

                ParseUser.requestPasswordResetInBackground(email , new RequestPasswordResetCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // An email was successfully sent with reset instructions.
                            Toast.makeText(PasswordReset.this, "An email was successfully sent with reset instruction", Toast.LENGTH_LONG).show();
                        } else {
                            // Something went wrong. Look at the ParseException to see what's up.
                            Toast.makeText(PasswordReset.this, "Please provide correct Email", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }
}
