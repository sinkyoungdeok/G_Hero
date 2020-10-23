package com.aspsine.fragmentnavigator.demo;

import android.app.Application;

import com.aspsine.fragmentnavigator.demo.firebase.UserFirebasePost;

public class SharedApplication extends Application  {

    public static UserFirebasePost myUser;
    public static UserFirebasePost yourUser;

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
