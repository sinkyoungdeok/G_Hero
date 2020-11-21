package com.aspsine.fragmentnavigator.demo.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.broadcast.BroadcastManager;
import com.aspsine.fragmentnavigator.demo.firebase.CodeFirebasePost;
import com.aspsine.fragmentnavigator.demo.firebase.UserFirebasePost;
import com.aspsine.fragmentnavigator.demo.kakao.SessionCallback;
import com.aspsine.fragmentnavigator.demo.utils.SharedPrefUtils;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
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
import com.kakao.auth.Session;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etEmail;
    private EditText etPassword;
    private Button signupBtn;
    private DatabaseReference mPostReference;
    private boolean codeCheck = false;
    private boolean userCheck = false;
    /* 카카오 로그인 */
    private SessionCallback sessionCallback;
    /* 카카오 로그인 */
    private FirebaseAuth auth;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String defaultposition = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preferences = getSharedPreferences("account",MODE_PRIVATE);
        editor = preferences.edit();
        String id =preferences.getString("id",null);
        Intent getintent = getIntent();
        if(getintent.getExtras() != null)
            defaultposition = getintent.getExtras().getString("defaultFragment");
        if(id != null ) {
            // 여기 부분 정보입력에 따라서 다르게 움직여야됨 ,,
            Intent intent;
            intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("id",id);
            if(defaultposition != null) intent.putExtra("defaultFragment",defaultposition);
            startActivity(intent);
            finish();
        }


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
            String emailReg = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
            Pattern p = Pattern.compile(emailReg);
            Matcher m = p.matcher(email);
            if(!m.matches()) {
                Toast.makeText(LoginActivity.this, "이메일 형식을 지켜주세요", Toast.LENGTH_SHORT).show();
            } else {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    markUserLogin();
                                    notifyUserLogin();
                                    finish();
                                    Intent intent;
                                    if (codeCheck) {
                                        intent = new Intent(LoginActivity.this, ConnectActivity.class);
                                    } else if (userCheck) {
                                        intent = new Intent(LoginActivity.this, InfoActivity.class);
                                    } else {
                                        editor.putString("id", email); // sharedpreference
                                        editor.commit();
                                        intent = new Intent(LoginActivity.this, MainActivity.class);
                                    }
                                    intent.putExtra("id", email);
                                    if (defaultposition != null)
                                        intent.putExtra("defaultFragment", defaultposition);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LoginActivity.this, "아이디 혹은 패스워드를 확인 해주세요", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

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
        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        Toast.makeText(this,"카카오 로그인은 준비 중입니다", Toast.LENGTH_SHORT).show();
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            Toast.makeText(this,"테스트", Toast.LENGTH_SHORT).show();
            return;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    protected void redirectSignupActivity() // 로그인 성공시 이동
    {
        markUserLogin();
        notifyUserLogin();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    /*카카오 로그인*/




}

