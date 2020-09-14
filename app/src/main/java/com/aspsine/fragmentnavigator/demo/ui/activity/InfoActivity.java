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
                userUpdateFirebaseDatabase(nameEdit.getText().toString(), birthEdit.getText().toString(), firstDayEdit.getText().toString());
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

    public void userUpdateFirebaseDatabase(final String name, final String birthday, final String firstDay){
        Query getQuery = mPostReference.child("/user_list").orderByChild("id").equalTo(id);

        getQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    UserFirebasePost user = postSnapshot.getValue(UserFirebasePost.class);
                    user.name = name;
                    user.birthday = birthday;
                    user.firstDay = firstDay;
                    user.gender = genderValue;
                    Map<String, Object> childUpdates = new HashMap<>();
                    Map<String, Object> postValues = user.toMap();
                    String temp =  user.id.replace(".","");
                    childUpdates.put("/user_list/id" +temp , postValues);
                    mPostReference.updateChildren(childUpdates);

                    Intent intent = new Intent(InfoActivity.this, MainActivity.class);
                    intent.putExtra("id",id);
                    startActivity(intent);
                    finish();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
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


        //업로드할 파일이 있으면 수행

        if (filePath != null) {
            //업로드 진행 Dialog 보이기
            //final ProgressDialog progressDialog = new ProgressDialog(this);
            //progressDialog.setTitle("업로드중...");
            //progressDialog.show();

            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();

            //Unique한 파일명을 만들자.
            //SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            //Date now = new Date();
            String filename = "profile" + ".png";
            //storage 주소와 폴더 파일명을 지정해 준다.
            StorageReference storageRef = storage.getReferenceFromUrl("gs://g-hero.appspot.com").child("images/" + filename);
            //올라가거라...
            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                            //Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //progressDialog.dismiss();
                            //Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            //progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }



    }

}
