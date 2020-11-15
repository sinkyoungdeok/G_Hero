package com.aspsine.fragmentnavigator.demo.ui.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.SharedApplication;
import com.aspsine.fragmentnavigator.demo.firebase.DdayFirebasePost;
import com.bumptech.glide.Glide;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class AddDdayActivity extends AppCompatActivity {
    private TextView cancel, add,title;
    private TextView startShow;
    private String startDate;
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy. M. d");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy,M,d");
    private EditText titleEdit;
    private DatabaseReference mPostReference;
    private String ID;
    private ImageView ddayPreview;
    private Uri filePath;
    private String id;

    private String tempContent, tempKey, tempDdayUrl, tempStartDate;

    private SlideDateTimeListener startListener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {
            startShow.setText(mFormatter.format(date));
            startDate = dateFormat.format(date).toString();
        }

        @Override
        public void onDateTimeCancel()
        {
            Toast.makeText(AddDdayActivity.this,
                    "Canceled", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dday);
        startShow = (TextView) findViewById(R.id.startShow);
        Date time = new Date();
        startShow.setText(mFormatter.format(time).toString());
        startDate = dateFormat.format(time).toString();
        titleEdit = (EditText) findViewById(R.id.titleEdit);
        ddayPreview = (ImageView) findViewById(R.id.dday_preview);
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        id = intent.getExtras().getString("id");
        if(SharedApplication.myUser.firstEnrolled.equals("T")) {
            ID = SharedApplication.myUser.id.replace(".", "");
        } else {
            ID = SharedApplication.yourUser.id.replace(".","");
        }

        /*권한*/
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
        }

        ddayPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //이미지를 선택
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.
        actionBar.setDisplayHomeAsUpEnabled(false);
        View actionbar = LayoutInflater.from(this).inflate(R.layout.add_dday_actionbar, null);
        actionBar.setCustomView(actionbar);

        cancel = (TextView)findViewById(R.id.cancel);
        add = (TextView)findViewById(R.id.add);
        title = (TextView)findViewById(R.id.title);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tempContent = intent.getExtras().getString("content",null);
        tempDdayUrl = intent.getExtras().getString("ddayUrl", null);
        tempStartDate = intent.getExtras().getString("startDate", null);
        tempKey = intent.getExtras().getString("key", null);
        if(tempContent != null) {
            titleEdit.setText(tempContent);
            startShow.setText(tempStartDate);
            startDate = tempStartDate;
            Glide.with(this).load(tempDdayUrl).into(ddayPreview);
            add.setText("수정");
        }

        startShow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(startListener)
                        .setInitialDate(new Date())
                        //.setMinDate(minDate)
                        //.setMaxDate(maxDate)
                        //.setIs24HourTime(true)
                        //.setTheme(SlideDateTimePicker.HOLO_DARK)
                        //.setIndicatorColor(Color.parseColor("#990000"))
                        .build()
                        .show();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
                finish();
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
                ddayPreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void uploadFile() {
        if(filePath == null) {
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            String filename = id+ titleEdit.getText().toString() + ".png";
            StorageReference storageRef = storage.getReferenceFromUrl("gs://g-hero.appspot.com").child("dday_images/" + filename);
            UploadTask uploadTask =  storageRef.putFile(filePath);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String profileUrl = uri.toString();


                            DdayFirebasePost post = new DdayFirebasePost(titleEdit.getText().toString(),startDate,profileUrl);
                            Map<String, Object> postValues = post.toMap();
                            mPostReference.child("/dday_list/id" + ID).push().setValue(postValues);
                            finish();

                        }
                    });

                }
            });
        }
    }

}
