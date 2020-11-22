package com.aspsine.fragmentnavigator.demo.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.item.emoticonListviewitem;
import com.aspsine.fragmentnavigator.demo.listener.OnBackPressedListener;
import com.aspsine.fragmentnavigator.demo.listviewadapter.emoticonAdapter;
import com.aspsine.fragmentnavigator.demo.ui.activity.AddDdayActivity;
import com.aspsine.fragmentnavigator.demo.ui.activity.MainActivity;
import com.aspsine.fragmentnavigator.demo.ui.adapter.demo.ui.fragment.MainFragment;
import com.aspsine.fragmentnavigator.demo.ui.widget.BottomNavigatorView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmoticonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmoticonFragment extends Fragment implements BottomNavigatorView.OnBottomNavigatorViewItemClickListener, OnBackPressedListener {


    MainActivity activity;
    Toast toast;
    long backKeyPressedTime;

    private ImageView imgShow;
    private ListView listView;
    private ArrayList<emoticonListviewitem> data;
    private emoticonAdapter adapter;
    public EmoticonFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static EmoticonFragment newInstance(String param1) {
        EmoticonFragment fragment = new EmoticonFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emoticon, container, false);
        init(view);




        return view;
    }
    private void init(View view) {
        viewInit(view);
        settingInit();
    }

    private void viewInit(View view) {
        imgShow = (ImageView) view.findViewById(R.id.imageView5);
        listView = (ListView) view.findViewById(R.id.emoticonList);
    }
    private void settingInit() {
        activity = (MainActivity) getActivity();
        toast = Toast.makeText(getContext(),"한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT);
        setHasOptionsMenu(true);
        data = new ArrayList<>();
        emoticonListViewSetting();



        Glide.with(this).load(R.mipmap.angry).into(imgShow);
    }
    private void emoticonListViewSetting() {
        emoticonListviewitem item = new emoticonListviewitem(R.mipmap.angry,R.mipmap.questionmark,R.mipmap.emoticonheart,imgShow);
        data.add(item);
        emoticonListviewitem item2 = new emoticonListviewitem(R.mipmap.angry,R.mipmap.questionmark,R.mipmap.emoticonheart,imgShow);
        data.add(item2);
        adapter = new emoticonAdapter(getContext(), R.layout.emoticon_item, data);
        listView.setAdapter(adapter);
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
        View actionbar = inflater.inflate(R.layout.emoticon_actionbar, null);
        actionBar.setCustomView(actionbar);

        menu.findItem(R.id.action_logout).setVisible(false);
        menu.findItem(R.id.action_exception).setVisible(false);
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
            fragmentManager.beginTransaction().remove(EmoticonFragment.this).commit();
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
