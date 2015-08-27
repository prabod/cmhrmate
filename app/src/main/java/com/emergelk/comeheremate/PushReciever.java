package com.emergelk.comeheremate;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParseAnalytics;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by prabod on 8/26/15.
 */
public class PushReciever extends ParsePushBroadcastReceiver {

    private static final String TAG = "PUSH";

    static void updateMyActivity(Context context, String message) {

        Intent intent = new Intent("updateLocation");

        //put whatever data you want to send, if any
        intent.putExtra("location", message);

        //send broadcast
        context.sendBroadcast(intent);
    }

    @Override
    public void onPushOpen(Context context, Intent intent) {
        ParseAnalytics.trackAppOpenedInBackground(intent);
        Log.i(TAG, "onPushOpen " + intent.getExtras().getString("com.parse.Data"));
        Intent i = new Intent(context, MapsActivity.class);
        try {
            JSONObject extras = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            Iterator<?> keys = extras.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                if (extras.get(key) instanceof JSONObject) {
                    i.putExtra(key, extras.get(key).toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive " + intent.getExtras().getString("com.parse.Data"));
        JSONObject extras = null;
        try {
            extras = new JSONObject(intent.getExtras().getString("com.parse.Data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onReceive(context, intent);
        if (intent.hasExtra("location")) {
            try {
                updateMyActivity(context, extras.getString("location"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        Log.i(TAG, "onPushReceive " + intent.getExtras().getString("com.parse.Data"));
        super.onPushReceive(context, intent);
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        Log.i(TAG, "onPushDismiss " + intent.getExtras().getString("com.parse.Data"));
        super.onPushDismiss(context, intent);
    }

}
