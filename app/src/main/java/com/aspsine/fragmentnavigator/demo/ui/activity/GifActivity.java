package com.aspsine.fragmentnavigator.demo.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.aspsine.fragmentnavigator.demo.R;

import java.net.URI;

public class GifActivity extends AppCompatActivity {
    private String imageUriStr;
    private ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif);
        img = (ImageView) findViewById(R.id.imageView6);
        Intent intent = getIntent();
        imageUriStr = intent.getExtras().getString("imageUri");
        img.setImageURI(Uri.parse(imageUriStr));

    }
}