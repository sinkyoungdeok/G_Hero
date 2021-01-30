package com.aspsine.fragmentnavigator.demo;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.aspsine.fragmentnavigator.demo.firebase.UserFirebasePost;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class SharedApplication extends Application  {

    public static UserFirebasePost myUser = null;
    public static UserFirebasePost yourUser = null;
    public static String imgTag = "";
    public static ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();
    @Override
    public void onCreate() {
        super.onCreate();
    }

}
