package com.aspsine.fragmentnavigator.demo.ui.activity;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.firebase.UserFirebasePost;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class InfoActivity extends AppCompatActivity {

    String id;
    EditText nameEdit, birthEdit, firstDayEdit;
    Button btn;
    RadioGroup gender;
    String genderValue;
    private DatabaseReference mPostReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Intent intent = getIntent();
        id = intent.getExtras().getString("id");
        nameEdit = (EditText) findViewById(R.id.nameEdit);
        birthEdit = (EditText) findViewById(R.id.birthEdit);
        firstDayEdit = (EditText) findViewById(R.id.firstDayEdit);
        btn = (Button) findViewById(R.id.btn);
        gender = (RadioGroup) findViewById(R.id.gender);
        gender.setOnCheckedChangeListener(radioGroupButtonChangeListener);

        mPostReference = FirebaseDatabase.getInstance().getReference();


        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                userUpdateFirebaseDatabase(nameEdit.getText().toString(), birthEdit.getText().toString(), firstDayEdit.getText().toString());
            }
        });
    }
    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if(i == R.id.man){
               genderValue = "man";
            } else if(i == R.id.woman){
                genderValue = "woman";
            }
        }
    };



    public void userUpdateFirebaseDatabase(final String name, final String birthday, final String firstDay){
        Query getQuery = mPostReference.child("/user_list").orderByChild("id").equalTo(id);

        getQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    UserFirebasePost user = postSnapshot.getValue(UserFirebasePost.class);
                    user.name = name;
                    user.birthday = birthday;
                    user.firstDay = firstDay;
                    user.gender = genderValue;
                    Map<String, Object> childUpdates = new HashMap<>();
                    Map<String, Object> postValues = user.toMap();
                    String temp =  user.id.replace(".","");
                    childUpdates.put("/user_list/id" +temp , postValues);
                    mPostReference.updateChildren(childUpdates);

                    Intent intent = new Intent(InfoActivity.this, MainActivity.class);
                    intent.putExtra("id",id);
                    startActivity(intent);
                    finish();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
