package com.emergelk.comeheremate;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "hlg3Bqfz6IOHCdMl8pV1VVj0ZuFs6RHa8QLXwzS7", "sMls1qyyXcZO2oIHkk1xUQHd4MWF7wNVoElbAC2i");

    }
}