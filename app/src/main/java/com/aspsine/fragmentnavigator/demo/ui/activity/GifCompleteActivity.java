package com.aspsine.fragmentnavigator.demo.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.SharedApplication;
import com.aspsine.fragmentnavigator.demo.utils.Screenshot;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;

public class GifCompleteActivity extends AppCompatActivity {

    private ImageView gifImg;
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif_complete);
        gifImg = (ImageView) findViewById(R.id.imageView7);
        btn = (Button) findViewById(R.id.emoticonBtn);

        AnimationDrawable animatedGIF = new AnimationDrawable();
        for( int i=1; i<= SharedApplication.bitmapList.size(); i++ ) {
            animatedGIF.addFrame(new BitmapDrawable(getResources(), SharedApplication.bitmapList.get(i-1)), i*20);
        }
        animatedGIF.setOneShot(false);

        gifImg.setImageDrawable(animatedGIF);
        animatedGIF.start();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(),"저장이 완료 되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });
    }
}