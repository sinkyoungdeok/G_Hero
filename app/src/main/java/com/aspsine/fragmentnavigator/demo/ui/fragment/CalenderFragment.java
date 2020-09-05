package com.aspsine.fragmentnavigator.demo.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.decorators.EventDecorator;
import com.aspsine.fragmentnavigator.demo.decorators.OneDayDecorator;
import com.aspsine.fragmentnavigator.demo.decorators.SaturdayDecorator;
import com.aspsine.fragmentnavigator.demo.decorators.SundayDecorator;
import com.aspsine.fragmentnavigator.demo.firebase.CalFirebasePost;
import com.aspsine.fragmentnavigator.demo.firebase.ChatFirebasePost;
import com.aspsine.fragmentnavigator.demo.ui.activity.AddCalenderActivity;
import com.aspsine.fragmentnavigator.demo.ui.activity.LoginActivity;
import com.aspsine.fragmentnavigator.demo.ui.activity.SignupActivity;
import com.aspsine.fragmentnavigator.demo.ui.widget.BottomNavigatorView;
import com.aspsine.fragmentnavigator.demo.utils.SharedPrefUtils;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalenderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalenderFragment extends Fragment  implements BottomNavigatorView.OnBottomNavigatorViewItemClickListener  {

    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    MaterialCalendarView materialCalendarView;
    //EditText calEdit,calRegistered;
    //Button calBtn;
    ListView calList;
    ArrayList<String> data;
    ArrayAdapter<String> arrayAdapter;
    Map<String, String> mCal = new HashMap<>();
    Integer result_len = 0;
    String clickCal = "";
    String[] result = new String[100000];
    String ID = "1";
    Button calAdd;
    int month_day[] = { 31,28,31,30,31,30,31,31,30,31,30,31 };


    private DatabaseReference mPostReference;


    public CalenderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment CalenderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalenderFragment newInstance(String param1) {
        CalenderFragment fragment = new CalenderFragment();
        Bundle bundle = new Bundle();

        fragment.setArguments(bundle);
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calender, container, false);
        //calEdit = (EditText) view.findViewById(R.id.calEdit);
        //calBtn = (Button) view.findViewById(R.id.calBtn);
        //calRegistered = (EditText) view.findViewById(R.id.calRegistered);
        //calRegistered.setFocusable(false);
        //calRegistered.setClickable(false);
        /*

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.
        actionBar.setDisplayHomeAsUpEnabled(false);



        View actionbar = inflater.inflate(R.layout.custom_actionbar, null);
        actionBar.setCustomView(actionbar);
        */

        setHasOptionsMenu(true);

        getActivity().setTitle("캘린더");
        data = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        calList =  (ListView)view.findViewById(R.id.calList);
        calList.setAdapter(arrayAdapter);

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
        getFirebaseDatabase();


        /*
        calBtn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  result[result_len-1] = clickCal;
                  result[result_len] = "2020,08,15"; // 쓰레기값 넣기( 마지막에 넣은것들은 왠지 모르겠지만 표시가 안됨
                  result_len += 1;
                  mCal.put(clickCal,calEdit.getText().toString());
                  new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());
                  calRegistered.setText(calEdit.getText().toString());

                  postFirebaseDatabase(true,calEdit.getText().toString(),clickCal);
              }
          });

         */

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                int Year = date.getYear();
                int Month = date.getMonth() + 1;
                int Day = date.getDay();

                //Log.i("Year test", Year + "");
                //Log.i("Month test", Month + "");
                //Log.i("Day test", Day + "");

                String shot_Day = Year + "," + Month + "," + Day;

                //Log.i("shot_Day test", shot_Day + "");
                materialCalendarView.clearSelection();
                //calEdit.setHint(shot_Day+ " 일정");
                //calEdit.setText("");
                clickCal = shot_Day;
                /*
                if(mCal.containsKey(shot_Day))
                    calRegistered.setText(mCal.get(shot_Day));
                else
                    calRegistered.setText("일정없음");
                */
                if(mCal.containsKey(shot_Day))
                {
                    data.clear();
                    data.add("\n \n");
                    data.add("\n"+mCal.get(shot_Day)+"\n");
                    data.add("\n \n");
                    arrayAdapter.clear();
                    arrayAdapter.addAll(data);
                    arrayAdapter.notifyDataSetChanged();
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
                Log.d("onDataChange", "Data is Updated");


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
                                            result[result_len - 1] = String.valueOf(startYear) + "," + String.valueOf(startMonth) +","+String.valueOf(startDay);
                                            result[result_len] = "2020,08,01"; // 쓰레기값 넣기( 마지막에 넣은것들은 왠지 모르겠지만 표시가 안됨
                                            result_len += 1;
                                            mCal.put(String.valueOf(startYear) + "," + String.valueOf(startMonth) +","+String.valueOf(startDay), info[0]);
                                        }
                                    } else {
                                        for(; month_day[startMonth-1]>=startDay; startDay ++) {
                                            result[result_len - 1] = String.valueOf(startYear) + "," + String.valueOf(startMonth) +","+String.valueOf(startDay);
                                            result[result_len] = "2020,08,01"; // 쓰레기값 넣기( 마지막에 넣은것들은 왠지 모르겠지만 표시가 안됨
                                            result_len += 1;
                                            mCal.put(String.valueOf(startYear) + "," + String.valueOf(startMonth) +","+String.valueOf(startDay), info[0]);
                                        }
                                    }
                                    startDay = 1;
                                }
                            } else {
                                for (; 12 >= startMonth; startMonth++) {
                                    for(; month_day[startMonth-1] >=startDay;startDay++) {
                                        result[result_len - 1] = String.valueOf(startYear) + "," + String.valueOf(startMonth) +","+String.valueOf(startDay);
                                        result[result_len] = "2020,08,01"; // 쓰레기값 넣기( 마지막에 넣은것들은 왠지 모르겠지만 표시가 안됨
                                        result_len += 1;
                                        mCal.put(String.valueOf(startYear) + "," + String.valueOf(startMonth) +","+String.valueOf(startDay), info[0]);
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
    /*
    public void postFirebaseDatabase(boolean add,String content, String date){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            CalFirebasePost post = new CalFirebasePost(content,date);
            postValues = post.toMap();
        }
        childUpdates.put("/schedule/id" + ID + "/"+ date , postValues);
        mPostReference.updateChildren(childUpdates);
    }

     */

}
