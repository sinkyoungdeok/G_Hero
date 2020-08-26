package com.aspsine.fragmentnavigator.demo.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.firebase.CodeFirebasePost;
import com.aspsine.fragmentnavigator.demo.firebase.UserFirebasePost;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ConnectActivity extends AppCompatActivity {
    EditText myCode, yourCodeEdt;
    String id;
    String code;
    String yourId;
    Button btn;
    boolean btncheck = false;
    private DatabaseReference mPostReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        mPostReference = FirebaseDatabase.getInstance().getReference();

        myCode = (EditText) findViewById(R.id.myCode);
        yourCodeEdt = (EditText) findViewById(R.id.yourCode);
        btn = (Button) findViewById(R.id.button);
        myCode.setFocusable(false);
        myCode.setClickable(false);
        Intent intent = getIntent();
        id = intent.getExtras().getString("id");
        getFirebaseDatabase();
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btncheck = true;
                getFirebaseDatabaseConnect(yourCodeEdt.getText().toString());
            }
        });

    }
    public void getFirebaseDatabase(){
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange", "Data is Updated");
                code = "";
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    String get = (String) postSnapshot.getValue();
                    if(!get.equals(id)) {
                        code = get;
                        myCode.setText(code);
                    }

                }
                if(code == "") {
                    Intent intent = new Intent(ConnectActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("/code_list/id"+id.replace(".","")).addValueEventListener(postListener);
    }
    public void getFirebaseDatabaseConnect(final String yourCode){
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(btncheck) {
                    boolean yourExistCheck = false;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String key = postSnapshot.getKey();
                        CodeFirebasePost get = postSnapshot.getValue(CodeFirebasePost.class);
                        String[] info = {get.id,get.code};
                        if(info[1].equals(yourCode)){
                            yourExistCheck = true;
                            yourId = info[0];
                        }
                    }
                    if(yourExistCheck) {
                        codeDeleteFirebaseDatabase(myCode.getText().toString());
                        codeDeleteFirebaseDatabase(yourCode);
                        userUpdateFirebaseDatabase(id,yourId,"T");
                        userUpdateFirebaseDatabase(yourId,id,"F");
                        Intent intent = new Intent(ConnectActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),"코드가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                    btncheck = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("/code_list").addValueEventListener(postListener);


    }
    public void codeDeleteFirebaseDatabase(String delCode) {
        Query deleteQuery = mPostReference.child("/code_list").orderByChild("code").equalTo(delCode);

        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    postSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void userUpdateFirebaseDatabase(String updateId, final String otherHalf, final String firstEnrolled){
        Query getQuery = mPostReference.child("/user_list").orderByChild("id").equalTo(updateId);

        getQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    UserFirebasePost user = postSnapshot.getValue(UserFirebasePost.class);
                    user.otherHalf = otherHalf;
                    user.firstEnrolled = firstEnrolled;
                    Map<String, Object> childUpdates = new HashMap<>();
                    Map<String, Object> postValues = user.toMap();
                    String temp =  user.id.replace(".","");
                    childUpdates.put("/user_list/id" +temp , postValues);
                    mPostReference.updateChildren(childUpdates);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
