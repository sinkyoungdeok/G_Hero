package com.aspsine.fragmentnavigator.demo.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.SharedApplication;
import com.aspsine.fragmentnavigator.demo.ui.adapter.demo.ui.fragment.ContactsFragment;
import com.aspsine.fragmentnavigator.demo.utils.Screenshot;
import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import us.technerd.tnimageview.TNImageView;

public class GifActivity extends AppCompatActivity {
    private String imageUriStr;
    private ImageView faceImg;
    private ImageView gifImg;
    private TNImageView tnImageView;
    private String imgTag;
    private List<ImageView> imageViews = new ArrayList<>();

    private View main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif);
        faceImg = (ImageView) findViewById(R.id.imageView6);
        gifImg = (ImageView) findViewById(R.id.imageView4);
        main = (RelativeLayout) findViewById(R.id.main);
        Button btn = (Button) findViewById(R.id.emoticonBtn);
        tnImageView = new TNImageView();

        Intent intent = getIntent();
        imageUriStr = intent.getExtras().getString("imageUri");
        imgTag = intent.getExtras().getString("imgTag");
        if(imgTag.equals("angry")) {
            Glide.with(getApplicationContext()).load(R.mipmap.emoticon_angry_no_face).into(gifImg);
        } else if ( imgTag.equals("questionmark")) {
            Glide.with(getApplicationContext()).load(R.mipmap.emoticon_questionmark_no_face).into(gifImg);
        } else if (imgTag.equals( "heart")) {
            Glide.with(getApplicationContext()).load(R.mipmap.emoticon_heart_no_face).into(gifImg);
        } else if (imgTag.equals("heart2")) {
            Glide.with(getApplicationContext()).load(R.mipmap.emoticon_heart2_no_face).into(gifImg);
        } else if (imgTag.equals("happy")) {
            Glide.with(getApplicationContext()).load(R.mipmap.emoticon_happy_no_face).into(gifImg);
        } else if (imgTag.equals("sleep")) {
            Glide.with(getApplicationContext()).load(R.mipmap.emoticon_sleep_no_face).into(gifImg);
        } else if (imgTag.equals("angry2")) {
            Glide.with(getApplicationContext()).load(R.mipmap.emoticon_angry2_no_face).into(gifImg);
        } else if (imgTag.equals("surprise")) {
            Glide.with(getApplicationContext()).load(R.mipmap.emoticon_surprise_no_face).into(gifImg);
        } else if (imgTag.equals("beggar")) {
            Glide.with(getApplicationContext()).load(R.mipmap.emoticon_beggar_no_face).into(gifImg);
        } else if (imgTag.equals("run")) {
            Glide.with(getApplicationContext()).load(R.mipmap.emoticon_run_no_face).into(gifImg);
        } else if (imgTag.equals("full")) {
            Glide.with(getApplicationContext()).load(R.mipmap.emoticon_full_no_face).into(gifImg);
        } else if (imgTag.equals("foolish")) {
            Glide.with(getApplicationContext()).load(R.mipmap.emoticon_foolish_no_face).into(gifImg);
        }


        imageViews.add(faceImg);
        tnImageView.addListofImageViews(imageViews);
        tnImageView.bringToFrontOnTouch(true);

        faceImg.setImageURI(Uri.parse(imageUriStr));


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bitmap b = Screenshot.takescreenshotOfRootView(main);
                SharedApplication.bitmapList.add(b);

                if (SharedApplication.bitmapList.size() >= 10) {
                    Intent intent = new Intent(GifActivity.this, GifCompleteActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }



}