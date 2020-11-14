package com.aspsine.fragmentnavigator.demo;

import android.app.Application;
import android.content.SharedPreferences;

import com.aspsine.fragmentnavigator.demo.firebase.UserFirebasePost;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SharedApplication extends Application  {

    public static UserFirebasePost myUser = null;
    public static UserFirebasePost yourUser = null;
    @Override
    public void onCreate() {
        super.onCreate();
    }

}
