package com.aspsine.fragmentnavigator.demo.ui.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.fragmentnavigator.demo.R;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddDdayActivity extends AppCompatActivity {
    private TextView cancel, add,title;
    private TextView startShow;
    private String startDate;
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy. M. d");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy,M,d");
    private EditText titleEdit;
    private DatabaseReference mPostReference;
    private String ID;

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
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        ID = intent.getExtras().getString("id").replace(".","");

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
                //postFirebaseDatabase(true);
                finish();
            }
        });
    }
}
