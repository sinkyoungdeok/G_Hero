package com.aspsine.fragmentnavigator.demo.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.aspsine.fragmentnavigator.FragmentNavigator;
import com.aspsine.fragmentnavigator.demo.Action;
import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.SharedApplication;
import com.aspsine.fragmentnavigator.demo.broadcast.BroadcastManager;
import com.aspsine.fragmentnavigator.demo.firebase.UserFirebasePost;
import com.aspsine.fragmentnavigator.demo.listener.OnBackPressedListener;
import com.aspsine.fragmentnavigator.demo.ui.adapter.demo.ui.adapter.FragmentAdapter;
import com.aspsine.fragmentnavigator.demo.ui.adapter.demo.ui.fragment.ContactsFragment;
import com.aspsine.fragmentnavigator.demo.ui.widget.BottomNavigatorView;
import com.aspsine.fragmentnavigator.demo.utils.SharedPrefUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class MainActivity extends AppCompatActivity implements BottomNavigatorView.OnBottomNavigatorViewItemClickListener {

    private static final int DEFAULT_POSITION = 0;

    private FragmentNavigator mNavigator;

    private BottomNavigatorView bottomNavigatorView;

    private MenuItem mLogoutMenu;

    private MenuItem mAddMenu;
    private MenuItem mAddMenu2;

    private String ID;

    private UserFirebasePost myUser = null;

    private UserFirebasePost yourUser = null;

    private DatabaseReference mPostReference;

    private int defaultPosition;

    private String defaultposition;

    private Gson gson;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String jsonMyUser;
    private String jsonYourUser;

    OnBackPressedListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPostReference = FirebaseDatabase.getInstance().getReference();
        preferences = getSharedPreferences("account",MODE_PRIVATE);
        editor = preferences.edit();
        gson = new GsonBuilder().create();
        Intent intent = getIntent();
        ID = intent.getExtras().getString("id");
        defaultposition = intent.getExtras().getString("defaultFragment");
        if (defaultposition == null) {
            defaultPosition = 0;
        } else if (defaultposition.equals("chat")) {
            defaultPosition = 1;
        }
        bottomNavigatorView = (BottomNavigatorView) findViewById(R.id.bottomNavigatorView);
        if (bottomNavigatorView != null) {
            bottomNavigatorView.setOnBottomNavigatorViewItemClickListener(this);
        }
        mNavigator = new FragmentNavigator(getSupportFragmentManager(), new FragmentAdapter(), R.id.container);
        mNavigator.setDefaultPosition(defaultPosition);
        mNavigator.onCreate(savedInstanceState);
        setCurrentTab(mNavigator.getCurrentPosition());
        jsonMyUser = preferences.getString("myUser",null);
        jsonYourUser = preferences.getString("yourUser",null);
        if( jsonMyUser == null) {
            Query myGetQuery = mPostReference.child("/user_list").orderByChild("id").equalTo(ID);
            myGetQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        UserFirebasePost myUser = postSnapshot.getValue(UserFirebasePost.class);
                        SharedApplication.myUser = myUser;
                        jsonMyUser = gson.toJson(myUser, UserFirebasePost.class);
                        editor.putString("myUser",jsonMyUser); // sharedpreference
                        editor.commit();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else {
            SharedApplication.myUser = gson.fromJson(jsonMyUser,UserFirebasePost.class);
        }
        if( jsonYourUser == null) {
            Query yourGetQuery = mPostReference.child("/user_list").orderByChild("otherHalf").equalTo(ID);
            yourGetQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        UserFirebasePost yourUser = postSnapshot.getValue(UserFirebasePost.class);
                        SharedApplication.yourUser = yourUser;
                        jsonYourUser = gson.toJson(yourUser, UserFirebasePost.class);
                        editor.putString("yourUser",jsonYourUser);
                        editor.commit();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            SharedApplication.yourUser = gson.fromJson(jsonYourUser,UserFirebasePost.class);
        }



        //setCurrentTab(mNavigator.getCurrentPosition());

        BroadcastManager.register(this, mLoginStatusChangeReceiver, Action.LOGIN, Action.LOGOUT);


    }
    public void setDefaultFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //ContactsFragment.newInstance(myUser, yourUser);
        transaction.add(R.id.container, ContactsFragment.newInstance());
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mLogoutMenu = menu.findItem(R.id.action_logout);
        mAddMenu = menu.findItem(R.id.action_add);
        mAddMenu2 = menu.findItem(R.id.action_add2);
        toggleMenu(SharedPrefUtils.isLogin(this));
        mAddMenu.setVisible(false);
        mAddMenu2.setVisible(false);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mNavigator.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_exception:
                startActivity(new Intent(this, ExceptionActivity.class));
                return true;
            case R.id.action_logout:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                logout();
                return true;
            case R.id.action_add:
                Intent intent = new Intent(this, AddCalenderActivity.class);
                intent.putExtra("id",ID);
                startActivity(intent);
                return true;
            case R.id.action_add2:
                Intent intent2 = new Intent(this, AddDdayActivity.class);
                intent2.putExtra("id",ID);
                startActivity(intent2);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        BroadcastManager.unregister(this, mLoginStatusChangeReceiver);
        super.onDestroy();
    }

    @Override
    public void onBottomNavigatorViewItemClick(int position, View view) {
        setCurrentTab(position);
    }

    private void logout(){
        editor.putString("id",null);
        editor.putString("myUser",null);
        editor.putString("yourUser",null);
        editor.commit();
        SharedApplication.myUser = null;
        SharedApplication.yourUser = null;

    }

    private void onUserLogin(int position) {
        if (position == -1) {
            resetAllTabsAndShow(mNavigator.getCurrentPosition());
        } else {
            resetAllTabsAndShow(position);
        }
        toggleMenu(true);
    }

    private void onUserLogout(int position) {
        if (position == -1) {
            resetAllTabsAndShow(mNavigator.getCurrentPosition());
        } else {
            resetAllTabsAndShow(position);
        }
        toggleMenu(true);
    }

    private void setCurrentTab(int position) {
        mNavigator.showFragment(position);
        bottomNavigatorView.select(position);
    }

    private void resetAllTabsAndShow(int position){
        mNavigator.resetFragments(position, true);
        bottomNavigatorView.select(position);
    }

    private void toggleMenu(boolean login) {

        if (login) {
            mLogoutMenu.setVisible(true);
        } else {
            mLogoutMenu.setVisible(false);
        }
    }

    private BroadcastReceiver mLoginStatusChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                int position = intent.getIntExtra("EXTRA_POSITION", -1);
                if (action.equals(Action.LOGIN)) {
                    onUserLogin(position);
                } else if (action.equals(Action.LOGOUT)) {
                    onUserLogout(position);
                }
            }
        }
    };
    public void setOnBackPressedListener(OnBackPressedListener listener) {
        this.listener = listener;
    }
    @Override
    public void onBackPressed() {
        if(listener != null) {
            listener.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}
