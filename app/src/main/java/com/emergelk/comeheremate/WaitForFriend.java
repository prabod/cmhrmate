package com.emergelk.comeheremate;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SendCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class WaitForFriend extends Activity {
    ImageView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intentLogIn = getIntent();
        Bundle bundle = intentLogIn.getExtras();
        String userName = bundle.getString("uname");
        setContentView(R.layout.activity_wait_for_friend);
        loading = (ImageView) findViewById(R.id.loading);

        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        loading.startAnimation(rotation);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", userName);
        List<ParseUser> users = null;
        try {
            users = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int len = users != null ? users.size() : 0;
        if (len > 0) {
            Toast.makeText(getApplicationContext(), "" + len, Toast.LENGTH_SHORT)
                    .show();
            final ParseQuery<ParseInstallation> pQuery = ParseInstallation.getQuery(); // <-- Installation query
            pQuery.whereEqualTo("username", userName);
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            String currentLocation = ""
                                    + location.getLatitude()
                                    + " ,"
                                    + location.getLongitude();
                            try {
                                JSONObject data = new JSONObject();
                                data.put("location", currentLocation);
                                ParsePush push = new ParsePush();
                                push.setQuery(pQuery);
                                push.setData(data);
                                push.sendInBackground(new SendCallback() {
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Log.d("push", "The push campaign has been created.");
                                        } else {
                                            Log.d("push", "Error sending push:" + e.getMessage());
                                        }
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    });
            try {
                JSONObject data = new JSONObject();
                data.put("alert", "from me");
                data.put("title", "ammatasiri");
                ParsePush push = new ParsePush();
                push.setQuery(pQuery);
                push.setData(data);
                //push.setMessage("helloo machn!!");
                push.sendInBackground(new SendCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("push", "The push campaign has been created.");
                        } else {
                            Log.d("push", "Error sending push:" + e.getMessage());
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            Toast.makeText(getApplicationContext(), "No users Found in that name", Toast.LENGTH_LONG)
                    .show();
        }

    }
}
