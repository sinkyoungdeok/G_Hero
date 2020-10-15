package com.aspsine.fragmentnavigator.demo.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.decorators.EventDecorator;
import com.aspsine.fragmentnavigator.demo.decorators.OneDayDecorator;
import com.aspsine.fragmentnavigator.demo.decorators.SaturdayDecorator;
import com.aspsine.fragmentnavigator.demo.decorators.SundayDecorator;
import com.aspsine.fragmentnavigator.demo.firebase.CalFirebasePost;
import com.aspsine.fragmentnavigator.demo.firebase.UserFirebasePost;
import com.aspsine.fragmentnavigator.demo.item.calendarListviewitem;
import com.aspsine.fragmentnavigator.demo.listener.OnBackPressedListener;
import com.aspsine.fragmentnavigator.demo.listviewadapter.calendarAdapter;
import com.aspsine.fragmentnavigator.demo.ui.activity.MainActivity;
import com.aspsine.fragmentnavigator.demo.ui.widget.BottomNavigatorView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalenderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalenderFragment extends Fragment  implements BottomNavigatorView.OnBottomNavigatorViewItemClickListener, OnBackPressedListener {

    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    MaterialCalendarView materialCalendarView;
    ListView calList;
    private ArrayList<calendarListviewitem> data;
    private calendarAdapter adapter;

    Map<String, String> mCal = new HashMap<>();
    Integer result_len = 0;
    String clickCal = "";
    String[] result = new String[100000];
    String ID = "1";
    Button calAdd;
    int month_day[] = { 31,28,31,30,31,30,31,31,30,31,30,31 };
    MainActivity activity;
    Toast toast;
    long backKeyPressedTime;

    private static UserFirebasePost myUser, yourUser;
    private DatabaseReference mPostReference;


    public CalenderFragment() {
        // Required empty public constructor
    }

    public static CalenderFragment newInstance(UserFirebasePost myuser, UserFirebasePost youruser) {
        CalenderFragment fragment = new CalenderFragment();
        myUser = myuser;
        yourUser = youruser;
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calender, container, false);

        activity = (MainActivity) getActivity();
        toast = Toast.makeText(getContext(),"한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT);
        setHasOptionsMenu(true);
        getActivity().setTitle("캘린더");
        data = new ArrayList<>();
        calList =  (ListView)view.findViewById(R.id.calList);
        mPostReference = FirebaseDatabase.getInstance().getReference();
        materialCalendarView = (MaterialCalendarView)view.findViewById(R.id.calendarView);
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1)) // 달력의 시작
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) // 달력의 끝
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay day) {
                StringBuffer buffer = new StringBuffer();
                int yearOne = day.getYear();
                int monthOne = day.getMonth() + 1;
                buffer.append(yearOne).append("년 ").append(monthOne).append("월");
                return buffer;
            }
        });


        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator);


        result[0] = "2020,08,10";
        result_len = 1;
        if(myUser.firstEnrolled.equals("T")) {
            ID = myUser.id.replace(".", "");
        } else {
            ID = yourUser.id.replace(".","");
        }
        getFirebaseDatabase();

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                int Year = date.getYear();
                int Month = date.getMonth() + 1;
                int Day = date.getDay();

                String shot_Day = Year + "," + Month + "," + Day;

                materialCalendarView.clearSelection();
                clickCal = shot_Day;
                if(mCal.containsKey(shot_Day))
                {
                    // 여기에서 이제 2개 이상을 띄워 주기 위해서는 for(String data : mCal.get(shot_Day).split("\n") ) { ~~~ 로 하나하나 처리해주면 끝 ..
                    calList.setVisibility(View.VISIBLE);
                    data.clear();

                    calendarListviewitem item2 = new calendarListviewitem("","","");
                    data.add(item2);
                    String[] splitData = mCal.get(shot_Day).split(",");
                    calendarListviewitem item = new calendarListviewitem(splitData[1],splitData[2],splitData[0]);
                    data.add(item);

                    calendarListviewitem item3 = new calendarListviewitem("","","");
                    data.add(item3);
                    adapter = new calendarAdapter(getContext(), R.layout.calendar_item, data);
                    calList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    data.clear();
                    adapter = null;
                    calList.setVisibility(View.INVISIBLE);
                }
            }
        });

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
        View actionbar = inflater.inflate(R.layout.calender_actionbar, null);
        actionBar.setCustomView(actionbar);

        menu.findItem(R.id.action_logout).setVisible(false);
        menu.findItem(R.id.action_exception).setVisible(false);
        menu.findItem(R.id.action_add).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBottomNavigatorViewItemClick(int position, View view) {

    }

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        String[] Time_Result;

        ApiSimulator(String[] Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환노

            for(int i = 0 ; i < result_len ; i ++){
                CalendarDay day = CalendarDay.from(calendar);
                String[] time = Time_Result[i].split(",");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);


                calendar.set(year,month-1,dayy);
                CalendarDay today =  CalendarDay.today();
                if(today.toString().equals(day.toString())) continue; // 오늘날짜 일경우엔 밑막대기 추가 노
                dates.add(day); // 밑막대기 추가하는 코드
            }



            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (getActivity().isFinishing()) {
                return;
            }

            materialCalendarView.addDecorator(new EventDecorator(Color.RED, calendarDays,getActivity()));
        }
    }

    public void getFirebaseDatabase(){
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    CalFirebasePost get = postSnapshot.getValue(CalFirebasePost.class);
                    String[] info = {get.content,get.startDate, get.startTime, get.endDate, get.endTime, get.scheduleOrDday};
                    if(info[5].equals("schedule")) {


                        /*
                        ---------------- 개발해야되는것 --------------
                        여기부분에서 시작날짜부터 끝날자까지 전부다 분홍표시해줘야됨,,
                        그런데, 해쉬함수를 썻기때문에 해쉬함수에서 이미 존재한 키면 \n으로 넣어야 되고 아니면 put해야됨
                        ---------------개발해야되는것-----------------
                         */
                        String split_data[] = info[1].split(",");
                        int startYear = Integer.parseInt(split_data[0]);
                        int startMonth = Integer.parseInt(split_data[1]);
                        int startDay = Integer.parseInt(split_data[2]);
                        split_data =  info[3].split(",");
                        int endYear =  Integer.parseInt(split_data[0]);
                        int endMonth =  Integer.parseInt(split_data[1]);
                        int endDay = Integer.parseInt(split_data[2]);
                        String contentNtime = info[0] +"," + info[2] +"," + info[4];

                        for(;endYear >= startYear; startYear++) {

                            // 윤년 처리
                            if((startYear % 4 == 0 && startYear % 100 != 0) || (startYear % 400 == 0))
                                month_day[1] = 29;
                            else
                                month_day[1] = 28;

                            if(endYear == startYear) {
                                for (; endMonth >= startMonth; startMonth++) {
                                    if(endMonth == startMonth) {
                                        for(; endDay >= startDay; startDay++) {
                                            String temp = String.valueOf(startYear) + "," + String.valueOf(startMonth) +","+String.valueOf(startDay);

                                            if(mCal.containsKey(temp)){
                                                String prev = mCal.get(temp);
                                                prev += '\n' + contentNtime;
                                                mCal.put(temp,prev);

                                            } else {
                                                result[result_len - 1] = temp;
                                                result[result_len] = "2020,08,01"; // 쓰레기값 넣기( 마지막에 넣은것들은 왠지 모르겠지만 표시가 안됨
                                                result_len += 1;
                                                mCal.put(temp, contentNtime);
                                            }


                                        }
                                    } else {
                                        for(; month_day[startMonth-1]>=startDay; startDay ++) {
                                            String temp = String.valueOf(startYear) + "," + String.valueOf(startMonth) +","+String.valueOf(startDay);

                                            if(mCal.containsKey(temp)){
                                                String prev = mCal.get(temp);
                                                prev += '\n' + contentNtime ;
                                                mCal.put(temp,prev);

                                            } else {
                                                result[result_len - 1] = temp;
                                                result[result_len] = "2020,08,01"; // 쓰레기값 넣기( 마지막에 넣은것들은 왠지 모르겠지만 표시가 안됨
                                                result_len += 1;
                                                mCal.put(temp, contentNtime);
                                            }
                                        }
                                    }
                                    startDay = 1;
                                }
                            } else {
                                for (; 12 >= startMonth; startMonth++) {
                                    for(; month_day[startMonth-1] >=startDay;startDay++) {
                                        String temp = String.valueOf(startYear) + "," + String.valueOf(startMonth) +","+String.valueOf(startDay);

                                        if(mCal.containsKey(temp)){
                                            String prev = mCal.get(temp);
                                            prev += '\n' + contentNtime ;
                                            mCal.put(temp,prev);

                                        } else {
                                            result[result_len - 1] = temp;
                                            result[result_len] = "2020,08,01"; // 쓰레기값 넣기( 마지막에 넣은것들은 왠지 모르겠지만 표시가 안됨
                                            result_len += 1;
                                            mCal.put(temp, contentNtime);
                                        }
                                    }
                                    startDay =1;
                                }
                            }
                            startMonth = 1;
                        }



                    } else {
                        // 디데이 일경우 처리
                        /*
                        ---------------개발해야되는것-----------------
                        여기부분에선 끝날짜에만 분홍표시를 해주면 된다.
                        그러므로, 해쉬함수에 put해주면됨
                        ---------------개발해야되는것-----------------
                         */
                    }

                }
                new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("/schedule/id"+ID).addValueEventListener(postListener);
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
            fragmentManager.beginTransaction().remove(CalenderFragment.this).commit();
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
