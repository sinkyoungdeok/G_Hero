package com.aspsine.fragmentnavigator.demo.ui.fragment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.decorators.EventDecorator;
import com.aspsine.fragmentnavigator.demo.decorators.OneDayDecorator;
import com.aspsine.fragmentnavigator.demo.decorators.SaturdayDecorator;
import com.aspsine.fragmentnavigator.demo.decorators.SundayDecorator;
import com.aspsine.fragmentnavigator.demo.firebase.CalFirebasePost;
import com.aspsine.fragmentnavigator.demo.firebase.ChatFirebasePost;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

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
public class CalenderFragment extends Fragment {

    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    MaterialCalendarView materialCalendarView;
    EditText calEdit,calRegistered;
    Button calBtn;
    Map<String, String> mCal = new HashMap<>();
    Integer result_len = 0;
    String clickCal = "";
    String[] result = new String[100000];
    String ID = "1";
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calender, container, false);
        calEdit = (EditText) view.findViewById(R.id.calEdit);
        calBtn = (Button) view.findViewById(R.id.calBtn);
        calRegistered = (EditText) view.findViewById(R.id.calRegistered);
        calRegistered.setFocusable(false);
        calRegistered.setClickable(false);

        mPostReference = FirebaseDatabase.getInstance().getReference();

        materialCalendarView = (MaterialCalendarView)view.findViewById(R.id.calendarView);
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1)) // 달력의 시작
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) // 달력의 끝
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator);


        result[0] = "2020,08,10";
        result_len = 1;
        getFirebaseDatabase();



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
                calEdit.setHint(shot_Day+ " 일정");
                calEdit.setText("");
                clickCal = shot_Day;
                if(mCal.containsKey(shot_Day))
                    calRegistered.setText(mCal.get(shot_Day));
                else
                    calRegistered.setText("일정없음");

            }
        });

        return view;
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
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for(int i = 0 ; i < result_len ; i ++){
                CalendarDay day = CalendarDay.from(calendar);
                String[] time = Time_Result[i].split(",");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                dates.add(day);
                calendar.set(year,month-1,dayy);
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
                    String[] info = {get.content,get.date};

                    result[result_len-1] = info[1];
                    result[result_len] = "2020,08,01"; // 쓰레기값 넣기( 마지막에 넣은것들은 왠지 모르겠지만 표시가 안됨
                    result_len += 1;
                    mCal.put(info[1],info[0]);
                    System.out.println(mCal.get(info[1]));

                    //String result = info[0];
                    //System.out.println(info[0] +"," + info[1]);
                    //Log.d("getFirebaseDatabase", "key: " + key);
                    //Log.d("getFirebaseDatabase", "info: " + info[0] + info[1]);
                }
                new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("/schedule/id"+ID).addValueEventListener(postListener);
    }
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

}