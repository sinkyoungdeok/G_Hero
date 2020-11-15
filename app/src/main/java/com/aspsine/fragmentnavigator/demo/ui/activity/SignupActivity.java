package com.aspsine.fragmentnavigator.demo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.firebase.CodeFirebasePost;
import com.aspsine.fragmentnavigator.demo.firebase.UserFirebasePost;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kakao.auth.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private EditText et_email, et_passwd;
    private Button signup_button;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mPostReference;
    private Map<String, String> mCode = new HashMap<>();
    private Random random = new Random();
    private String randCode = "";
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(!task.isSuccessful()) {
                            return;
                        }
                        token = task.getResult().getToken();
                    }
                });
        et_email = (EditText) findViewById(R.id.et_email);
        et_passwd = (EditText) findViewById(R.id.et_password);
        signup_button = (Button) findViewById(R.id.signup_button);
        firebaseAuth = FirebaseAuth.getInstance();
        mPostReference = FirebaseDatabase.getInstance().getReference();
        getFirebaseDatabase();
        while(true)
        {
            randCode = "";
            for(int i=0; i< 4; i++) {
                int randNum = random.nextInt(10);
                randCode += Integer.toString(randNum);
            }
            randCode += " ";
            for(int i=0; i< 4; i++) {
                int randNum = random.nextInt(10);
                randCode += Integer.toString(randNum);
            }
            if(mCode.containsKey(randCode))
                continue;
            break;
        }
        /* 이메일 회원가입 */
        signup_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String email = et_email.getText().toString().trim();
                String pwd = et_passwd.getText().toString().trim();

                String emailReg = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
                Pattern p = Pattern.compile(emailReg);
                Matcher m = p.matcher(email);
                if (!m.matches()) {
                    Toast.makeText(SignupActivity.this, "이메일 형식을 지켜주세요", Toast.LENGTH_SHORT).show();
                } else if(pwd.length() < 6) {
                    Toast.makeText(SignupActivity.this, "비밀번호는 6글자 이상으로 해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        postFirebaseDatabase(true, email, token);
                                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(SignupActivity.this, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            });
                }

            }
        });





    }


    public void postFirebaseDatabase(boolean add,String email,String token){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            UserFirebasePost post = new UserFirebasePost(email,"","","","","",randCode,token,"","","");
            //System.out.println(post.id +","+ post.name+","+ post.birthday +","+ post.firstDay +","+ post.profileUrl +","+post.code +","+post.phoneNumber +","+post.firstEnrolled);
            postValues = post.toMap();
        }
        String temp =  email.replace(".","");
        //System.out.println(email);
        childUpdates.put("/user_list/id" +temp , postValues);
        mPostReference.updateChildren(childUpdates);

        if(add){
            CodeFirebasePost post = new CodeFirebasePost(email, randCode);
            postValues = post.toMap();
        }
        childUpdates.put("/code_list/id" +temp, postValues);
        mPostReference.updateChildren(childUpdates);

    }

    public void getFirebaseDatabase(){
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange", "Data is Updated");


                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    CodeFirebasePost get = postSnapshot.getValue(CodeFirebasePost.class);
                    String[] info = {get.id,get.code};
                    mCode.put(get.code,get.id);


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("/code_list/").addValueEventListener(postListener);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // 로그인 인증을 요청 했을 때 결과 값을 되돌려 받는
        super.onActivityResult(requestCode, resultCode, data);
        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        Toast.makeText(this,"카카오 로그인은 준비 중입니다", Toast.LENGTH_SHORT).show();
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }


    }
}
