package com.aspsine.fragmentnavigator.demo.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.aspsine.fragmentnavigator.demo.R;
import com.bumptech.glide.Glide;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import us.technerd.tnimageview.TNImageView;

public class GifActivity extends AppCompatActivity {
    private String imageUriStr;
    private ImageView faceImg;
    private ImageView gifImg;
    private TNImageView tnImageView;
    private List<ImageView> imageViews = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif);
        faceImg = (ImageView) findViewById(R.id.imageView6);
        gifImg = (ImageView) findViewById(R.id.imageView4);
        tnImageView = new TNImageView();

        Intent intent = getIntent();
        imageUriStr = intent.getExtras().getString("imageUri");

        Glide.with(getApplicationContext()).load(R.mipmap.angry_no_face).into(gifImg);

        imageViews.add(faceImg);
        tnImageView.addListofImageViews(imageViews);
        tnImageView.bringToFrontOnTouch(true);

        faceImg.setImageURI(Uri.parse(imageUriStr));

    }
}