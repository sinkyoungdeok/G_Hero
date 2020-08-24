package com.aspsine.fragmentnavigator.demo.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.firebase.CalFirebasePost;
import com.aspsine.fragmentnavigator.demo.firebase.UserFirebasePost;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText et_email, et_passwd;
    Button signup_button;
    FirebaseAuth firebaseAuth;
    private DatabaseReference mPostReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        et_email = (EditText) findViewById(R.id.et_email);
        et_passwd = (EditText) findViewById(R.id.et_password);
        signup_button = (Button) findViewById(R.id.signup_button);
        firebaseAuth = FirebaseAuth.getInstance();
        mPostReference = FirebaseDatabase.getInstance().getReference();



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
                                    postFirebaseDatabase(true,email);
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
    public void postFirebaseDatabase(boolean add,String email){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            UserFirebasePost post = new UserFirebasePost(email,"","","","","","","","F");
            //System.out.println(post.id +","+ post.name+","+ post.birthday +","+ post.firstDay +","+ post.profileUrl +","+post.code +","+post.phoneNumber +","+post.firstEnrolled);
            postValues = post.toMap();
        }
        email =  email.replace(".","");
        System.out.println(email);
        childUpdates.put("/user_list/id" +email , postValues);
        mPostReference.updateChildren(childUpdates);
    }
}
