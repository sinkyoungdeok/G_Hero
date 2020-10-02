package com.aspsine.fragmentnavigator.demo.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.firebase.CalFirebasePost;
import com.aspsine.fragmentnavigator.demo.firebase.ChatFirebasePost;
import com.aspsine.fragmentnavigator.demo.firebase.CodeFirebasePost;
import com.aspsine.fragmentnavigator.demo.firebase.UserFirebasePost;
import com.aspsine.fragmentnavigator.demo.ui.fragment.CalenderFragment;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;

public class SignupActivity extends AppCompatActivity {

    EditText et_email, et_passwd;
    Button signup_button;
    FirebaseAuth firebaseAuth;
    private DatabaseReference mPostReference;
    Map<String, String> mCode = new HashMap<>();
    Random random = new Random();
    String randCode = "";
    String token;
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
        signup_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final String email = et_email.getText().toString().trim();
                String pwd = et_passwd.getText().toString().trim();
                firebaseAuth.createUserWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()) {
                                    postFirebaseDatabase(true,email,token);
                                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(SignupActivity.this, "등록 에러", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        });

            }
        });




    }
    public void postFirebaseDatabase(boolean add,String email,String token){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            UserFirebasePost post = new UserFirebasePost(email,"","","","","",randCode,token,"","");
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
}
