package com.aspsine.fragmentnavigator.demo.ui.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.aspsine.fragmentnavigator.demo.R;

public class AddDdayActivity extends AppCompatActivity {
    private TextView cancel, add,title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dday);

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

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //postFirebaseDatabase(true);
                finish();
            }
        });
    }
}
