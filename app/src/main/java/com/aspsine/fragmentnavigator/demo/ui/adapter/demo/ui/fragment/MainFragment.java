package com.aspsine.fragmentnavigator.demo.ui.adapter.demo.ui.fragment;


import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.SharedApplication;
import com.aspsine.fragmentnavigator.demo.listener.OnBackPressedListener;
import com.aspsine.fragmentnavigator.demo.ui.activity.MainActivity;
import com.aspsine.fragmentnavigator.demo.ui.widget.BottomNavigatorView;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements BottomNavigatorView.OnBottomNavigatorViewItemClickListener, OnBackPressedListener {

    public static final String TAG = "dialog_event";
    private TextView myNameText;
    private TextView yourNameText;
    private TextView todayText;
    private TextView ingdayText;
    private DatabaseReference mPostReference;
    MainActivity activity;
    Toast toast;
    long backKeyPressedTime;
    /* profile */
    private ImageView myImg, yourImg;
    private FirebaseStorage storage, yourstorage;
    private StorageReference storageReference, yourstorageReference;
    private StorageReference pathReference, yourpathReference;
    /* profile */

    /* floating button */
    private FloatingActionButton fab, fab1, fab2; // fab3;
    private LinearLayout fabLayout1, fabLayout2; //fabLayout3;
    boolean isFABOpen = false;
    /* floating button */
    public static Fragment newInstance() {
        MainFragment fragment = new MainFragment();
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

        fabLayout1 = (LinearLayout) view.findViewById(R.id.fabLayout1);
        fabLayout2 = (LinearLayout) view.findViewById(R.id.fabLayout2);
        //fabLayout3 = (LinearLayout) view.findViewById(R.id.fabLayout3);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab1 = (FloatingActionButton) view.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);
        //fab3 = (FloatingActionButton) view.findViewById(R.id.fab3);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        new BackgroundTask().execute();

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



        return view;
    }


    private void showFABMenu() {
        isFABOpen = true;
        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);
        //fabLayout3.setVisibility(View.VISIBLE);
        //fabBGLayout.setVisibility(View.VISIBLE);
        fab.animate().rotationBy(180);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
        //fabLayout3.animate().translationY(-getResources().getDimension(R.dimen.standard_145));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        //fabBGLayout.setVisibility(View.GONE);
        fab.animate().rotation(0);
        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0);
        //fabLayout3.animate().translationY(0);
        fabLayout2.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                    //fabLayout3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        menu.findItem(R.id.action_add2).setVisible(false);
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

    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {
        protected void onPreExecute() {

        }
        protected Integer doInBackground(Integer ... values) {
            while(SharedApplication.myUser == null);
            publishProgress(1);
            while(SharedApplication.yourUser == null);
            publishProgress(2);
            return 1;
        }
        protected void onProgressUpdate(Integer ... values) {
            if(values[0] == 1) {
                myNameText.setText(SharedApplication.myUser.name);
                if(!SharedApplication.myUser.profileUrl.equals("")) {
                    Glide.with(getContext()).load(SharedApplication.myUser.profileUrl).into(myImg);
                    myImg.setBackgroundResource(0);
                }
                Calendar cal = Calendar.getInstance( );
                String split_data[] = SharedApplication.myUser.firstDay.split(",");
                int year = Integer.parseInt(split_data[0]);
                int month = Integer.parseInt(split_data[1]);
                int day = Integer.parseInt(split_data[2]);
                Calendar cal2 = new GregorianCalendar(year, month-1, day);
                long diffSec = (cal.getTimeInMillis() - cal2.getTimeInMillis())/1000;
                long diffDays = diffSec / (24*60*60);
                ingdayText.setText( Long.toString(diffDays+1) + "일째");

                SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy년 M월 d일 ");
                Date time = new Date();
                String todayStr = mFormatter.format(time).toString();
                Calendar oCalendar = Calendar.getInstance( );
                final String[] week = { "일", "월", "화", "수", "목", "금", "토" };

                todayStr += week[oCalendar.get(Calendar.DAY_OF_WEEK) - 1] + "요일";
                todayText.setText(todayStr);

            } else {
                yourNameText.setText(SharedApplication.yourUser.name);
                if(!SharedApplication.yourUser.profileUrl.equals("")) {
                    Glide.with(getContext()).load(SharedApplication.yourUser.profileUrl).into(yourImg);
                    yourImg.setBackgroundResource(0);
                }
            }
        }
        protected void onPostExecute(Integer result) {

        }
        protected void onCancelled() {

        }
    }


}
