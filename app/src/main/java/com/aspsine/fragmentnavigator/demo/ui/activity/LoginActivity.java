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
import com.aspsine.fragmentnavigator.demo.firebase.CodeFirebasePost;
import com.aspsine.fragmentnavigator.demo.firebase.UserFirebasePost;
import com.aspsine.fragmentnavigator.demo.kakao.SessionCallback;
import com.aspsine.fragmentnavigator.demo.utils.SharedPrefUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private EditText etEmail;
    private EditText etPassword;
    private Button signupBtn;
    private DatabaseReference mPostReference;
    private boolean codeCheck = false;
    private boolean userCheck = false;
    /* 카카오 로그인 */
    private SessionCallback sessionCallback;
    /* 카카오 로그인 */
    /* 구글 로그인 */
    private SignInButton btn_google;
    private FirebaseAuth auth;
    private GoogleSignInClient googleApiClient;
    private static final int REO_SIGN_GOOGLE = 1000;
    /* 구글 로그인 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword  = (EditText)findViewById(R.id.et_password);
        Button button = (Button) findViewById(R.id.login_in_button);
        signupBtn = (Button) findViewById(R.id.signup_button);
        button.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
        mPostReference = FirebaseDatabase.getInstance().getReference();

        /*카카오 로그인*/
        sessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);
        /*카카오 로그인*/

        /*구글 로그인*/
        final GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        /*
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();

         */
        googleApiClient = GoogleSignIn.getClient(this, googleSignInOptions);

        btn_google = findViewById(R.id.sign_in_button);
        setGooglePlusButtonText(btn_google,"구글계정으로 로그인");
        btn_google.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                Intent intent = googleApiClient.getSignInIntent();
                startActivityForResult(intent, REO_SIGN_GOOGLE);
            }
        });
        /*구글 로그인*/

        signupBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
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
        final String email = String.valueOf(etEmail.getText()).trim();
        String password = String.valueOf(etPassword.getText()).trim();
        getFirebaseDatabase(email);
        if(check(email, password)){
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                markUserLogin();
                                notifyUserLogin();
                                finish();
                                Intent intent;
                                if(codeCheck) {
                                    intent = new Intent(LoginActivity.this, ConnectActivity.class);
                                } else if(userCheck) {
                                    intent = new Intent(LoginActivity.this, InfoActivity.class);
                                } else {
                                    intent = new Intent(LoginActivity.this, MainActivity.class);
                                }
                                intent.putExtra("id",email);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this, "로그인 오류", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }
    public void getFirebaseDatabase(final String id){
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange", "Data is Updated");


                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    CodeFirebasePost get = postSnapshot.getValue(CodeFirebasePost.class);
                    String[] info = {get.id,get.code};
                    if(info[0].equals(id)) codeCheck = true;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("/code_list/").addValueEventListener(postListener);

        if(codeCheck) return;
        Query getQuery = mPostReference.child("/user_list").orderByChild("id").equalTo(id);

        getQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UserFirebasePost user = postSnapshot.getValue(UserFirebasePost.class);
                    if(user.birthday.equals("")) userCheck = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
        super.onActivityResult(requestCode, resultCode, data);
        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }


        if(requestCode == REO_SIGN_GOOGLE){

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                resultLogin(account);
            } catch (ApiException e){
                Toast.makeText(getApplicationContext(),"로그인 실패2", Toast.LENGTH_SHORT).show();
                System.out.println(e + "test");
            }

            /*
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()) { // 인증결과가 성공할경우
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

             */

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
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /*구글 로그인*/


}

