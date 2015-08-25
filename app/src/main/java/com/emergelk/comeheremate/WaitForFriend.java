package com.emergelk.comeheremate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class WaitForFriend extends Activity {
    ImageView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
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
            ParseQuery<ParseInstallation> pQuery = ParseInstallation.getQuery(); // <-- Installation query
            pQuery.whereEqualTo("username", userName);
            JSONObject data = null;
            try {
                data = new JSONObject("{" +
                        "\"alert\": \"Test\"," +
                        "\"title\" :\"testTitle\"," +
                        "\"location\" :\"testLocation\"," +
                        "\"uri\" :\"comeheremate://host/map\"" +
                        "}");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ParsePush push = new ParsePush();
            push.setQuery(pQuery);
            push.setData(data);
            push.sendInBackground();
        }
    }
}
