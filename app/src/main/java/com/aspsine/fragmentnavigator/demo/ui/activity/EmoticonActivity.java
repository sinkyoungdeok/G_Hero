package com.aspsine.fragmentnavigator.demo.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.aspsine.fragmentnavigator.demo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.util.List;

public class EmoticonActivity extends AppCompatActivity {
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoticon);
        mContext = this;

        final RelativeLayout RelativeLayout_main = findViewById(R.id.RelativeLayout_main);
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                    .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                    .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                    .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                    .build();

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.test);

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options);

        Task<List<FirebaseVisionFace>> result =
                detector.detectInImage(image)
                    .addOnSuccessListener(
                            new OnSuccessListener<List<FirebaseVisionFace>>() {
                                @Override
                                public void onSuccess(List<FirebaseVisionFace> faces) {

                                    Point p = new Point();
                                    Display display = getWindowManager().getDefaultDisplay();
                                    display.getSize(p);

                                    //p.x; p.y;
                                    // 1:10 = 10:x 공식 활용

                                    for (FirebaseVisionFace face : faces) {
                                        Rect bounds = face.getBoundingBox();
                                        float rotY = face.getHeadEulerAngleY();
                                        float rotZ = face.getHeadEulerAngleZ();

                                        FirebaseVisionFaceLandmark leftEye = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE);
                                        float lex = leftEye.getPosition().getX();
                                        float ley = leftEye.getPosition().getY();

                                        FirebaseVisionFaceLandmark leftCheek = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_CHEEK);
                                        float lcx = leftCheek.getPosition().getX();
                                        float lcy = leftCheek.getPosition().getY();

                                        FirebaseVisionFaceLandmark rightCheek = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_CHEEK);
                                        float rex = rightCheek.getPosition().getX();
                                        float rey = rightCheek.getPosition().getY();

                                        ImageView imageLE = new ImageView(mContext);
                                        imageLE.setImageResource(R.mipmap.mnug);
                                        imageLE.setX(p.x * lex / bitmap.getWidth() - 100);
                                        imageLE.setY(p.y * ley / bitmap.getHeight() - 100);

                                        imageLE.setLayoutParams(new RelativeLayout.LayoutParams(200,200));

                                        RelativeLayout_main.addView(imageLE);
                                        ImageView imageLC = new ImageView(mContext);
                                        imageLC.setImageResource(R.mipmap.left_whiskers);
                                        imageLC.setX(p.x * lcx / bitmap.getWidth() - 100);
                                        imageLC.setY(p.y * lcy / bitmap.getHeight() - 100);
                                        imageLC.setLayoutParams(new RelativeLayout.LayoutParams(200,200));
                                        RelativeLayout_main.addView(imageLC);
                                        ImageView imageRC = new ImageView(mContext);
                                        imageRC.setImageResource(R.mipmap.right_whiskers);
                                        imageRC.setLayoutParams(new RelativeLayout.LayoutParams(200,200));
                                        RelativeLayout_main.addView(imageRC);
                                        imageRC.setX(p.x * rex / bitmap.getWidth() - 100);
                                        imageRC.setY(p.y * rey / bitmap.getHeight() - 100);
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
