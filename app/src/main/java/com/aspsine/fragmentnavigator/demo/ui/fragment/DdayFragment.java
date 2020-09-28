package com.aspsine.fragmentnavigator.demo.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.item.ddayListviewitem;
import com.aspsine.fragmentnavigator.demo.listviewadapter.ddayAdapter;
import com.aspsine.fragmentnavigator.demo.ui.widget.BottomNavigatorView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DdayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DdayFragment extends Fragment  implements BottomNavigatorView.OnBottomNavigatorViewItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ImageView no_data;
    private ImageView no_icon_gray;
    private ListView listview;
    private ArrayList<ddayListviewitem> data;

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
        ddayListviewitem icon = new ddayListviewitem(R.mipmap.icon,"icona");
        ddayListviewitem icon2 = new ddayListviewitem(R.mipmap.icon,"iconb");

        data.add(icon);
        data.add(icon2);

        ddayAdapter adapter = new ddayAdapter(getContext(), R.layout.dday_item, data);
        listview.setAdapter(adapter);



        //밑에 두줄은 데이터가 하나라도 있을때 처리하면됨,,
        //no_data.setVisibility(View.INVISIBLE);
        //no_icon_gray.setVisibility(View.INVISIBLE);

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
}
