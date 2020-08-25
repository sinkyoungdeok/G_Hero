package com.aspsine.fragmentnavigator.demo.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.firebase.CodeFirebasePost;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ConnectActivity extends AppCompatActivity {
    EditText myCode, yourCode;
    String id;
    String code;
    private DatabaseReference mPostReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        mPostReference = FirebaseDatabase.getInstance().getReference();

        myCode = (EditText) findViewById(R.id.myCode);
        yourCode = (EditText) findViewById(R.id.yourCode);
        myCode.setFocusable(false);
        myCode.setClickable(false);
        Intent intent = getIntent();
        id = intent.getExtras().getString("id");
        getFirebaseDatabase(id);

    }
    public void getFirebaseDatabase(final String id){
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange", "Data is Updated");
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    String get = (String) postSnapshot.getValue();
                    if(!get.equals(id)) {
                        code = get;
                        myCode.setText(code);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("/code_list/id"+id.replace(".","")).addValueEventListener(postListener);
    }
}
