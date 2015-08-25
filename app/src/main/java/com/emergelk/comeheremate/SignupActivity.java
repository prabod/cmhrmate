package com.emergelk.comeheremate;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    Button verify;
    EditText countryCode;
    EditText phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        verify = (Button) findViewById(R.id.Verify);
        countryCode = (EditText) findViewById(R.id.countryCode);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        countryCode.setText(VerifyMobile
                .getCountryCode(getApplicationContext()));
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = countryCode.getText().toString()
                        + phoneNumber.getText().toString();
                Intent in = new Intent(SignupActivity.this, VerifyMobile.class);
                in.putExtra("app_id", "3790cb9fbc8c427995eff16");
                in.putExtra("access_token", "58ab8a50e1ccba2161693e26ee7e5a300b7572fa");
                in.putExtra("mobile", mobile);

                startActivityForResult(in, VerifyMobile.REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VerifyMobile.REQUEST_CODE) {
            String message = data.getStringExtra("message");

            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                    .show();
            final String mobile = countryCode.getText().toString()
                    + phoneNumber.getText().toString();
            // Register New account
            ParseUser user = new ParseUser();
            user.setUsername(mobile);
            user.setPassword(mobile);
            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("logged", "logged");
                        try {
                            ParseUser.logIn(mobile, mobile);
                            ParseUser currentUser = ParseUser.getCurrentUser();
                            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                            installation.put("username", currentUser.getUsername());
                            installation.save();
                            finish();
                            Intent goToMain = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(goToMain);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        Log.d("logged", e.toString());
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                        finish();
                        Intent goToMain = new Intent(SignupActivity.this, MainActivity.class);
                        startActivity(goToMain);
                    }


                }
            });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
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
}
