package com.aspsine.fragmentnavigator.demo.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.aspsine.fragmentnavigator.demo.R;
import com.github.gabrielbb.cutout.CutOut;
import com.github.gabrielbb.cutout.SharedApplication;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class EmoticonActivity extends AppCompatActivity {
    private CircleImageView emoticonImg;
    private Uri filePath;
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoticon);
        btn = (Button) findViewById(R.id.btn);
        /*권한*/
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
        }
        emoticonImg = (CircleImageView) findViewById(R.id.emoticonImg);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);

        emoticonImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //이미지를 선택
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });
        btn.setOnClickListener(view -> {
            CutOut.activity()
                    .src(filePath)
                    .bordered()
                    .noCrop()
                    .start(this);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면

        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                emoticonImg.setImageBitmap(bitmap);
                MLKitStart(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void MLKitStart(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        SharedApplication.width = width;
        SharedApplication.height = height;
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .build();
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options);
        Task<List<FirebaseVisionFace>> result =
                detector.detectInImage(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionFace>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionFace> faces) {


                                        for (FirebaseVisionFace face : faces) {


                                            float x = face.getBoundingBox().centerX();
                                            float y = face.getBoundingBox().centerY();

                                            float xOffset = face.getBoundingBox().width() / 2.0f;
                                            float yOffset = face.getBoundingBox().height() / 2.0f;
                                            float left = x - xOffset;
                                            float top = y - yOffset;
                                            float right = x + xOffset;
                                            float bottom = y + yOffset;

                                            SharedApplication.left = left;
                                            SharedApplication.top = top;
                                            SharedApplication.right = right;
                                            SharedApplication.bottom = bottom;



                                        }



                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

    }

}
