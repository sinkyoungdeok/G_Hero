package com.aspsine.fragmentnavigator.demo.ui.activity;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.firebase.UserFirebasePost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class InfoActivity extends AppCompatActivity {

    String id;
    EditText nameEdit, birthEdit, firstDayEdit;
    Button btn;
    RadioGroup gender;
    String genderValue;
    private DatabaseReference mPostReference;
    private CircleImageView ivPreview;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Intent intent = getIntent();
        id = intent.getExtras().getString("id");
        nameEdit = (EditText) findViewById(R.id.nameEdit);
        birthEdit = (EditText) findViewById(R.id.birthEdit);
        firstDayEdit = (EditText) findViewById(R.id.firstDayEdit);
        btn = (Button) findViewById(R.id.btn);
        gender = (RadioGroup) findViewById(R.id.gender);
        gender.setOnCheckedChangeListener(radioGroupButtonChangeListener);
        mPostReference = FirebaseDatabase.getInstance().getReference();

        /*권한*/
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
        }
        ivPreview = (CircleImageView) findViewById(R.id.iv_preview2);
        ivPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //이미지를 선택
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });


        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                uploadFile();
            }
        });
    }
    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if(i == R.id.man){
               genderValue = "man";
            } else if(i == R.id.woman){
                genderValue = "woman";
            }
        }
    };


    //결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면

        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ivPreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //upload the file
    private void uploadFile() {

        if(genderValue == null) {
            Toast.makeText(getApplicationContext(), "성별을 선택 해주세요.", Toast.LENGTH_SHORT).show();
        } else if (nameEdit.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "이름을 입력 해주세요.", Toast.LENGTH_SHORT).show();
        } else if (birthEdit.getText().toString().trim().length() ==0) {
            Toast.makeText(getApplicationContext(), "생일을 입력 해주세요.", Toast.LENGTH_SHORT).show();
        } else if (firstDayEdit.getText().toString().trim().length() ==0) {
            Toast.makeText(getApplicationContext(), "처음 만난 날을 입력 해주세요.", Toast.LENGTH_SHORT).show();
        } else if(filePath == null) {
            Toast.makeText(getApplicationContext(), "프로필을 선택 해주세요.", Toast.LENGTH_SHORT).show();
        } else{
            FirebaseStorage storage = FirebaseStorage.getInstance();

            String filename = id+"Profile" + ".png";
            StorageReference storageRef = storage.getReferenceFromUrl("gs://g-hero.appspot.com").child("images/" + filename);
            UploadTask uploadTask =  storageRef.putFile(filePath);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String profileUrl = uri.toString();

                            DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("user_list/id"+id.replace(".",""));
                            profileRef.child("profileUrl").setValue(profileUrl);
                            profileRef.child("name").setValue(nameEdit.getText().toString().trim());
                            profileRef.child("birthday").setValue(birthEdit.getText().toString().trim());
                            profileRef.child("firstDay").setValue(firstDayEdit.getText().toString().trim());
                            profileRef.child("gender").setValue(genderValue);
                            Intent intent = new Intent(InfoActivity.this, MainActivity.class);
                            intent.putExtra("id",id);
                            startActivity(intent);
                            finish();

                        }
                    });

                }
            });
        }



    }

}
