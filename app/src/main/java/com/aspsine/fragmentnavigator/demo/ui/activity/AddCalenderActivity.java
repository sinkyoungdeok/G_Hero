package com.aspsine.fragmentnavigator.demo.ui.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.firebase.CalFirebasePost;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;


public class AddCalenderActivity extends AppCompatActivity {
    TextView cancel, add,title;

    //private SimpleDateFormat mFormatter = new SimpleDateFormat("MMMM dd yyyy hh:mm aa");
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy. M. d.  aa h:m");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy,M,d");
    private SimpleDateFormat timeFormat =  new SimpleDateFormat("h:m");

    private TextView startShow, endShow;
    private Switch oneDaySwitch, dDaySwitch;
    private boolean oneDayCheck, dDayCheck;
    private DatabaseReference mPostReference;
    private String ID;
    private String startDate, startTime, endDate, endTime;
    private EditText titleEdit;

    private SlideDateTimeListener startListener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {
            //Toast.makeText(AddCalenderActivity.this,
                    //mFormatter.format(date), Toast.LENGTH_SHORT).show();
            startShow.setText(mFormatter.format(date));
            startDate = dateFormat.format(date).toString();
            startTime = timeFormat.format(date).toString();
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel()
        {
            Toast.makeText(AddCalenderActivity.this,
                    "Canceled", Toast.LENGTH_SHORT).show();
        }
    };

    private SlideDateTimeListener endListener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {
            //Toast.makeText(AddCalenderActivity.this,
            //mFormatter.format(date), Toast.LENGTH_SHORT).show();
            endShow.setText(mFormatter.format(date));
            endDate = dateFormat.format(date).toString();
            endTime = timeFormat.format(date).toString();
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel()
        {
            Toast.makeText(AddCalenderActivity.this,
                    "Canceled", Toast.LENGTH_SHORT).show();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_calender);
        startShow = (TextView) findViewById(R.id.startShow);
        endShow = (TextView) findViewById(R.id.endShow);
        oneDaySwitch = (Switch) findViewById(R.id.switch1);
        dDaySwitch = (Switch) findViewById(R.id.switch2);
        oneDayCheck = false;
        dDayCheck = false;
        Date time = new Date();
        startShow.setText(mFormatter.format(time).toString());
        startDate = dateFormat.format(time).toString();
        startTime = timeFormat.format(time).toString();
        endDate = dateFormat.format(time).toString();
        endTime = timeFormat.format(time).toString();
        endShow.setText(mFormatter.format(time).toString());
        mPostReference = FirebaseDatabase.getInstance().getReference();
        titleEdit = (EditText) findViewById(R.id.titleEdit);

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.
        actionBar.setDisplayHomeAsUpEnabled(false);

        View actionbar = LayoutInflater.from(this).inflate(R.layout.custom_actionbar, null);
        actionBar.setCustomView(actionbar);

        cancel = (TextView)findViewById(R.id.cancel);
        add = (TextView)findViewById(R.id.add);
        title = (TextView)findViewById(R.id.title);
        Intent intent = getIntent();
        ID = intent.getExtras().getString("id").replace(".","");
        System.out.println(ID+"test1");

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
                if(oneDayCheck) {
                    Toast.makeText(AddCalenderActivity.this, "하루 종일 체크를 풀어주세요", Toast.LENGTH_SHORT).show();
                } else {
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
            }
        });
        endShow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                if(oneDayCheck){
                    Toast.makeText(AddCalenderActivity.this, "하루 종일 체크를 풀어주세요", Toast.LENGTH_SHORT).show();
                } else {
                    new SlideDateTimePicker.Builder(getSupportFragmentManager())
                            .setListener(endListener)
                            .setInitialDate(new Date())
                            //.setMinDate(minDate)
                            //.setMaxDate(maxDate)
                            //.setIs24HourTime(true)
                            //.setTheme(SlideDateTimePicker.HOLO_DARK)
                            //.setIndicatorColor(Color.parseColor("#990000"))
                            .build()
                            .show();
                }
            }
        });
        oneDaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    oneDayCheck = true;
                    //Toast.makeText(AddCalenderActivity.this, "on", Toast.LENGTH_SHORT).show();
                } else {
                    oneDayCheck = false;
                    //Toast.makeText(AddCalenderActivity.this, "off", Toast.LENGTH_SHORT).show();

                }
            }
        });
        dDaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    dDayCheck = true;
                    //Toast.makeText(AddCalenderActivity.this, "on", Toast.LENGTH_SHORT).show();
                } else {
                    dDayCheck = false;
                    //Toast.makeText(AddCalenderActivity.this, "off", Toast.LENGTH_SHORT).show();

                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postFirebaseDatabase(true);
                finish();
            }
        });

    }
    public void postFirebaseDatabase(boolean add){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        String dDayOrSchedule ="";
        if(!dDayCheck) {
            dDayOrSchedule = "schedule";
        } else {
            dDayOrSchedule = "dday";
        }
        if(add){
            CalFirebasePost post = new CalFirebasePost(titleEdit.getText().toString(), startDate, startTime, endDate, endTime, dDayOrSchedule);
            postValues = post.toMap();
        }
        String temp = startDate + " , " + startTime + "~" + endDate + " , " + endTime + " , " + dDayOrSchedule + " , " + titleEdit.getText().toString();
        childUpdates.put("/schedule/id" + ID + "/"+ temp , postValues);
        mPostReference.updateChildren(childUpdates);
    }
}
