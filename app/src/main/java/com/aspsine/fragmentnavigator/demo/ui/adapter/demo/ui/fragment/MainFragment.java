package com.aspsine.fragmentnavigator.demo.ui.adapter.demo.ui.fragment;


import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.firebase.UserFirebasePost;
import com.aspsine.fragmentnavigator.demo.listener.OnBackPressedListener;
import com.aspsine.fragmentnavigator.demo.ui.activity.MainActivity;
import com.aspsine.fragmentnavigator.demo.ui.adapter.demo.utils.SharedPrefUtils;
import com.aspsine.fragmentnavigator.demo.ui.widget.BottomNavigatorView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements BottomNavigatorView.OnBottomNavigatorViewItemClickListener, OnBackPressedListener {

    public static final String TAG = MainFragment.class.getSimpleName();
    private static String ID;

    private TextView myNameText;
    private TextView yourNameText;
    private TextView todayText;
    private TextView ingdayText;

    private DatabaseReference mPostReference;

    MainActivity activity;
    Toast toast;
    long backKeyPressedTime;
    private String yourID;

    /* profile */
    private ImageView myImg, yourImg;
    private FirebaseStorage storage, yourstorage;
    private StorageReference storageReference, yourstorageReference;
    private StorageReference pathReference, yourpathReference;
    /* profile */

    public static Fragment newInstance(String text) {
        MainFragment fragment = new MainFragment();
        ID = text;
        return fragment;
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mText = getArguments().getString(EXTRA_TEXT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        toast = Toast.makeText(getContext(),"한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT);
        // Inflate the layout for this fragment
        //Toast.makeText(getActivity(),"aa",Toast.LENGTH_SHORT).show();
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        mPostReference = FirebaseDatabase.getInstance().getReference();
        myNameText = (TextView) view.findViewById(R.id.myName);
        yourNameText = (TextView) view.findViewById(R.id.yourName);
        todayText = (TextView) view.findViewById(R.id.today);
        ingdayText = (TextView) view.findViewById(R.id.ingday);
        myImg = (ImageView) view.findViewById(R.id.myImg);
        yourImg = (ImageView) view.findViewById(R.id.yourImg);



        /* 커스텀 액션바 */
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.
        actionBar.setDisplayHomeAsUpEnabled(false);

        View actionbar = inflater.inflate(R.layout.main_actionbar, null);
        actionBar.setCustomView(actionbar);
        /* 커스텀 액션바 */

        getUserFirebaseDatabase(ID);

        SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy년 M월 d일 ");
        Date time = new Date();
        String todayStr = mFormatter.format(time).toString();
        Calendar oCalendar = Calendar.getInstance( );
        final String[] week = { "일", "월", "화", "수", "목", "금", "토" };

        todayStr += week[oCalendar.get(Calendar.DAY_OF_WEEK) - 1] + "요일";
        todayText.setText(todayStr);


        /*profile*/
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://g-hero.appspot.com");
        pathReference = storageReference.child("images/" + ID+ "Profile.png");
        pathReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()) {
                    Glide.with(getContext())
                            .load(task.getResult())
                            .into(myImg);
                    myImg.setBackgroundResource(0);
                }
            }
        });
        /*
        yourstorage = FirebaseStorage.getInstance();
        yourstorageReference = yourstorage.getReferenceFromUrl("gs://g-hero.appspot.com");
        yourpathReference = yourstorageReference.child("images/" + yourID+ "Profile.png");
        yourpathReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()) {
                    Glide.with(getContext())
                            .load(task.getResult())
                            .into(yourImg);
                    yourImg.setBackgroundResource(0);
                }
            }
        });
        */
        /*profile*/


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }




    public void getUserFirebaseDatabase(String UserId){
        Query myGetQuery = mPostReference.child("/user_list").orderByChild("id").equalTo(UserId);
        myGetQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    UserFirebasePost user = postSnapshot.getValue(UserFirebasePost.class);
                    yourID = user.otherHalf;

                    yourstorage = FirebaseStorage.getInstance();
                    yourstorageReference = yourstorage.getReferenceFromUrl("gs://g-hero.appspot.com");
                    yourpathReference = yourstorageReference.child("images/" + yourID+ "Profile.png");
                    yourpathReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()) {
                                Glide.with(getContext())
                                        .load(task.getResult())
                                        .into(yourImg);
                                yourImg.setBackgroundResource(0);
                            }
                        }
                    });

                    myNameText.setText(user.name);
                    Calendar cal = Calendar.getInstance( );
                    String split_data[] = user.firstDay.split(",");
                    int year = Integer.parseInt(split_data[0]);
                    int month = Integer.parseInt(split_data[1]);
                    int day = Integer.parseInt(split_data[2]);
                    Calendar cal2 = new GregorianCalendar(year, month-1, day);
                    long diffSec = (cal.getTimeInMillis() - cal2.getTimeInMillis())/1000;
                    long diffDays = diffSec / (24*60*60);
                    ingdayText.setText( Long.toString(diffDays+1) + "일째");



                    Query yourGetQuery = mPostReference.child("/user_list").orderByChild("id").equalTo(user.otherHalf);
                    yourGetQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                UserFirebasePost yourUser = postSnapshot.getValue(UserFirebasePost.class);
                                yourNameText.setText(yourUser.name);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });









    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.
        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.main_actionbar, null);
        actionBar.setCustomView(actionbar);

        menu.findItem(R.id.action_logout).setVisible(true);
        menu.findItem(R.id.action_exception).setVisible(true);
        menu.findItem(R.id.action_add).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBottomNavigatorViewItemClick(int position, View view) {

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast.show();
            return;
        }
        if(System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(MainFragment.this).commit();
            fragmentManager.popBackStack();
            getActivity().finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            toast.cancel();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setOnBackPressedListener(this);
    }




}
