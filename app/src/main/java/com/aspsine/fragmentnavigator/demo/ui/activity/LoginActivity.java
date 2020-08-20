package com.aspsine.fragmentnavigator.demo.ui.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.broadcast.BroadcastManager;
import com.aspsine.fragmentnavigator.demo.kakao.SessionCallback;
import com.aspsine.fragmentnavigator.demo.utils.SharedPrefUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etEmail;
    private EditText etPassword;

    /* 카카오 로그인 */
    private SessionCallback sessionCallback;
    /* 카카오 로그인 */
    /* 구글 로그인 */
    private SignInButton btn_google;
    private FirebaseAuth auth;
    private GoogleApiClient googleApiClient;
    private static final int REO_SIGN_GOOGLE = 100;
    /* 구글 로그인 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword  = (EditText)findViewById(R.id.et_password);
        Button button = (Button) findViewById(R.id.login_in_button);
        button.setOnClickListener(this);

        /*카카오 로그인*/
        sessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);
        /*카카오 로그인*/

        /*구글 로그인*/
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                //.enableAutoManage(this, (GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();
        auth = FirebaseAuth.getInstance();

        btn_google = findViewById(R.id.sign_in_button);
        setGooglePlusButtonText(btn_google,"구글계정으로 로그인");
        btn_google.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, REO_SIGN_GOOGLE);
            }
        });
        /*구글 로그인*/
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_in_button:
                tryLogin();
                break;
            // ...
        }

    }
    void tryLogin(){
        String email = String.valueOf(etEmail.getText()).trim();
        String password = String.valueOf(etPassword.getText()).trim();

        if(check(email, password)){
            markUserLogin();
            notifyUserLogin();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }
    boolean check(String email, String password){
        if (TextUtils.isEmpty(email)){
            etEmail.setError(getString(R.string.error_invalid_email));
            return false;
        }
        if (TextUtils.isEmpty(password)){
            etPassword.setError(getString(R.string.error_invalid_password));
            return false;
        }
        return true;
    }
    private void markUserLogin(){
        SharedPrefUtils.login(this);
    }
    private void notifyUserLogin(){
        BroadcastManager.sendLoginBroadcast(this, 1);
    }

    /*카카오 로그인*/
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 세션 콜백 삭제
        Session.getCurrentSession().removeCallback(sessionCallback);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // 로그인 인증을 요청 했을 때 결과 값을 되돌려 받는
        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REO_SIGN_GOOGLE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess() == true) { // 인증결과가 성공할경우
                GoogleSignInAccount account = result.getSignInAccount(); // account 라는 데이터는 구글로그인 정보를 담고있다. (닉네임, 프로필사진URI,이메일주소,..등)
                resultLogin(account); // 로그인 결과 값 출력 수행하라는 메소
            }
            else{
                markUserLogin();
                notifyUserLogin();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                Toast.makeText(getApplicationContext(),"로그인 실패2", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class SessionCallback implements ISessionCallback
    {
        @Override
        public void onSessionOpened()
        {
            redirectSignupActivity();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception)
        {
            if(exception != null)
            {
                Logger.e(exception);
            }
        }

    }
    protected void redirectSignupActivity() // 로그인 성공시 이동
    {
        markUserLogin();
        notifyUserLogin();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    /*카카오 로그인*/

    /*구글 로그인*/
    private void resultLogin(final GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) { // 로그인이 성공했을경우
                            Toast.makeText(getApplicationContext(),"로그인 성공", Toast.LENGTH_SHORT).show();
                            markUserLogin();
                            notifyUserLogin();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("nickName",account.getDisplayName());
                            intent.putExtra("photoURI",String.valueOf(account.getPhotoUrl()));
                            startActivity(intent);
                            finish();
                        } else { // 로그인 실패일경우
                            Toast.makeText(getApplicationContext(),"로그인 실패", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    /*구글 로그인*/


}

