package com.aspsine.fragmentnavigator.demo.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.SharedApplication;
import com.aspsine.fragmentnavigator.demo.firebase.ChatFirebasePost;
import com.aspsine.fragmentnavigator.demo.firebase.DdayFirebasePost;
import com.aspsine.fragmentnavigator.demo.item.chatListviewitem;
import com.aspsine.fragmentnavigator.demo.item.ddayListviewitem;
import com.aspsine.fragmentnavigator.demo.listener.OnBackPressedListener;
import com.aspsine.fragmentnavigator.demo.listviewadapter.ddayAdapter;
import com.aspsine.fragmentnavigator.demo.ui.activity.MainActivity;
import com.aspsine.fragmentnavigator.demo.ui.adapter.demo.ui.fragment.MainFragment;
import com.aspsine.fragmentnavigator.demo.ui.widget.BottomNavigatorView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DdayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DdayFragment extends Fragment  implements BottomNavigatorView.OnBottomNavigatorViewItemClickListener, OnBackPressedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ImageView no_data;
    private ImageView no_icon_gray;
    private ListView listview;
    private ArrayList<ddayListviewitem> data;
    private ddayAdapter adapter;
    private DatabaseReference mPostReference;
    private boolean dataCheck = false;
    private String ID;

    MainActivity activity;
    Toast toast;
    long backKeyPressedTime;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DdayFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static DdayFragment newInstance(String param1) {
        DdayFragment fragment = new DdayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dday, container, false);
        setHasOptionsMenu(true);
        no_data = (ImageView) view.findViewById(R.id.no_data);
        no_icon_gray = (ImageView) view.findViewById(R.id.no_icon_gray);
        listview = (ListView) view.findViewById(R.id.ddaylist);
        data = new ArrayList<>();
        mPostReference = FirebaseDatabase.getInstance().getReference();

        activity = (MainActivity) getActivity();
        toast = Toast.makeText(getContext(),"한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT);

        if(SharedApplication.myUser.firstEnrolled.equals("T")) {
            ID = SharedApplication.myUser.id.replace(".", "");
        } else {
            ID = SharedApplication.yourUser.id.replace(".","");
        }
        getFirebaseDatabase();




        return view;
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
        View actionbar = inflater.inflate(R.layout.dday_actionbar, null);
        actionBar.setCustomView(actionbar);

        menu.findItem(R.id.action_logout).setVisible(false);
        menu.findItem(R.id.action_exception).setVisible(false);
        menu.findItem(R.id.action_add).setVisible(false);
        menu.findItem(R.id.action_add2).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBottomNavigatorViewItemClick(int position, View view) {

    }
    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast.show();
            return;
        }
        if(System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(DdayFragment.this).commit();
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

    public void getFirebaseDatabase() {
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    DdayFirebasePost get = postSnapshot.getValue(DdayFirebasePost.class);
                    String[] info = {get.content, get.startDate, get.ddayUrl};
                    String ddayResult = ddayCalculator(info[1]);
                    ddayListviewitem icon = new ddayListviewitem(info[2], ddayResult, info[0]);
                    data.add(icon);
                    dataCheck = true;


                }
                if(dataCheck) {
                    adapter = new ddayAdapter(getContext(), R.layout.dday_item, data);
                    listview.setAdapter(adapter);
                    no_data.setVisibility(View.INVISIBLE);
                    no_icon_gray.setVisibility(View.INVISIBLE);
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("/dday_list/id"+ID).addValueEventListener(postListener);
    }

    public String ddayCalculator(String startDate) {
        String split_data[] = startDate.split(",");
        String plusOrMinus;
        int year = Integer.parseInt(split_data[0]);
        int month = Integer.parseInt(split_data[1]);
        int day = Integer.parseInt(split_data[2]);
        Calendar calNow = Calendar.getInstance( );
        Calendar calDday = new GregorianCalendar(year, month-1, day);
        long diffSec = (calDday.getTimeInMillis() - calNow.getTimeInMillis())/1000;
        if(diffSec >0) {
            plusOrMinus = " - ";
        } else {
            plusOrMinus = " + ";
            diffSec = diffSec * -1 ;
        }
        long diffDays = diffSec / (24*60*60);
        if (plusOrMinus.equals(" + ") == true) {
            diffDays -=1;
        }

        return "D" + plusOrMinus +Long.toString(diffDays +1 ) ;
    }

}
