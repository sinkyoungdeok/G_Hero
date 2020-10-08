package com.aspsine.fragmentnavigator.demo.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.aspsine.fragmentnavigator.demo.R;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 300); // 1초 후에 hd handler 실행  3000ms = 3초

    }

    private class splashhandler implements Runnable{
        public void run(){
            String defaultposition = null;
            Intent intent = new Intent(getApplication(), LoginActivity.class);
            Intent getintent = getIntent();
            if(getintent.getExtras() != null) {
                defaultposition = getintent.getExtras().getString("defaultFragment");
                intent.putExtra("defaultFragment", defaultposition);
            }
            startActivity(intent); //로딩이 끝난 후, ChoiceFunction 이동
            finish(); // 로딩페이지 Activity stack에서 제거
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }

}
