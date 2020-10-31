package com.aspsine.fragmentnavigator.demo.ui.fragment;

import android.content.Context;
import android.os.Bundle;

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
import com.aspsine.fragmentnavigator.demo.item.ddayListviewitem;
import com.aspsine.fragmentnavigator.demo.listener.OnBackPressedListener;
import com.aspsine.fragmentnavigator.demo.listviewadapter.ddayAdapter;
import com.aspsine.fragmentnavigator.demo.ui.activity.MainActivity;
import com.aspsine.fragmentnavigator.demo.ui.adapter.demo.ui.fragment.MainFragment;
import com.aspsine.fragmentnavigator.demo.ui.widget.BottomNavigatorView;

import java.util.ArrayList;

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

        activity = (MainActivity) getActivity();
        toast = Toast.makeText(getContext(),"한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT);

        if(true) {
            ddayListviewitem icon = new ddayListviewitem(R.mipmap.dday_1, "D - 35", "1주년 기념일");
            ddayListviewitem icon2 = new ddayListviewitem(R.mipmap.dday_2, "D - 50", "남미 여행가는 날");
            ddayListviewitem icon3 = new ddayListviewitem(R.mipmap.dday_3, "D - 130", "22번째 생일");
            ddayListviewitem icon4 = new ddayListviewitem(R.mipmap.dday_4, "D - 150", "처음 만난 날");
            ddayListviewitem icon5 = new ddayListviewitem(R.mipmap.dday_5, "D - 200", "등산가는 날");
            ddayListviewitem icon6 = new ddayListviewitem(R.mipmap.dday_6, "D - 250", "도서관 데이트");

            data.add(icon);
            data.add(icon2);
            data.add(icon3);
            data.add(icon4);
            data.add(icon5);
            data.add(icon6);


            adapter = new ddayAdapter(getContext(), R.layout.dday_item, data);
            listview.setAdapter(adapter);


            // 밑의 두줄로 데이터가 없을때의 화면을 지워준다.
            no_data.setVisibility(View.INVISIBLE);
            no_icon_gray.setVisibility(View.INVISIBLE);
        }
        //listview.setAdapter(null);




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
        menu.findItem(R.id.action_add).setVisible(true);
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

}
