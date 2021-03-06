package com.aspsine.fragmentnavigator.demo.ui.adapter.demo.ui.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.SendNotification;
import com.aspsine.fragmentnavigator.demo.SharedApplication;
import com.aspsine.fragmentnavigator.demo.firebase.ChatFirebasePost;
import com.aspsine.fragmentnavigator.demo.firebase.UserFirebasePost;
import com.aspsine.fragmentnavigator.demo.item.chatListviewitem;
import com.aspsine.fragmentnavigator.demo.listener.OnBackPressedListener;
import com.aspsine.fragmentnavigator.demo.listviewadapter.chatAdapter;
import com.aspsine.fragmentnavigator.demo.ui.activity.MainActivity;
import com.aspsine.fragmentnavigator.demo.ui.widget.BottomNavigatorView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */



public class ContactsFragment extends Fragment implements BottomNavigatorView.OnBottomNavigatorViewItemClickListener, OnBackPressedListener {
    /* firebase */
    private DatabaseReference mPostReference;
    private DatabaseReference chatRef;
    private String content,ID;
    private EditText contentET;
    private Button btn;
    private ListView listView;
    private ArrayList<chatListviewitem> data;
    private chatAdapter adapter;
    private Toast toast;
    private MainActivity activity;
    private long backKeyPressedTime;
    /* firebase */
    private String FCMToken;
    public static final String TAG = ContactsFragment.class.getSimpleName();
    public static Fragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();
        return fragment;
    }

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    /*firebase*/
    public void getFirebaseDatabase(){
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                Map<String, Object> readUserMap = new HashMap<>();
                String prevName = null;
                String prevDate = null;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    ChatFirebasePost get = postSnapshot.getValue(ChatFirebasePost.class);
                    String[] info = {get.id, get.name, get.content, get.date};
                    chatListviewitem item;
                    String tempDate = info[3].split("/")[1];
                    if(info[0].equals(SharedApplication.myUser.id)) { // 내 채팅
                        if(get.readUsers.size() >= 2) { // 상대방이 읽었을때
                            item = new chatListviewitem(SharedApplication.myUser.profileUrl, info[2], tempDate, SharedApplication.myUser.name, true,true,prevName,prevDate);
                        } else { // 상대방이 아직 안읽었을때
                            item = new chatListviewitem(SharedApplication.myUser.profileUrl, info[2], tempDate, SharedApplication.myUser.name, true,false,prevName,prevDate);
                        }
                    } else { // 상대방 채팅
                        item = new chatListviewitem(SharedApplication.yourUser.profileUrl, info[2], tempDate ,SharedApplication.yourUser.name, false, true,prevName,prevDate);
                    }
                    get.readUsers.put(SharedApplication.myUser.name, true);
                    readUserMap.put(key,get);
                    data.add(item);
                    prevName = info[1];
                    prevDate = tempDate;
                }
                adapter.notifyDataSetChanged();
                listView.setSelection(data.size()-1);

                FirebaseDatabase.getInstance().getReference().child("/chat_list/id"+ID)
                        .updateChildren(readUserMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("/chat_list/id"+ID).addValueEventListener(postListener);


    }
    /*firebase*/

    /*firebase*/
    public void postFirebaseDatabase(boolean add){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy.M.d/aa h:m");
            Date time = new Date();
            String todayStr = mFormatter.format(time).toString();
            ChatFirebasePost post = new ChatFirebasePost(SharedApplication.myUser.id, SharedApplication.myUser.name, content, todayStr);
            postValues = post.toMap();
        }
        SendNotification.sendNotification(FCMToken,SharedApplication.myUser.name,content);
        mPostReference.child("/chat_list/id" + ID).push().setValue(postValues);
        clearET();
        content = null;
    }
    public void clearET () {
        contentET.setText("");
    }
    /*firebase*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);
        setHasOptionsMenu(true);

        activity = (MainActivity) getActivity();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        toast = Toast.makeText(getContext(),"한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT);


        data = new ArrayList<>();
        contentET = (EditText)v.findViewById(R.id.contents);
        btn = (Button)v.findViewById(R.id.send);
        listView = (ListView)v.findViewById(R.id.chatlist);
        mPostReference = FirebaseDatabase.getInstance().getReference();
        adapter = new chatAdapter(getContext(), R.layout.contact_item,data);
        listView.setAdapter(adapter);

        new BackgroundTask().execute();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(contentET.getText().toString().trim().length() >= 1) {
                    content = contentET.getText().toString().trim();
                    postFirebaseDatabase(true);

                }
            }
        });


        return v;
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
        View actionbar = inflater.inflate(R.layout.contacts_actionbar, null);
        actionBar.setCustomView(actionbar);

        menu.findItem(R.id.action_logout).setVisible(false);
        menu.findItem(R.id.action_exception).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBottomNavigatorViewItemClick(int position, View view) {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
            fragmentManager.beginTransaction().remove(ContactsFragment.this).commit();
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
            while(SharedApplication.yourUser == null);
            publishProgress(1);
            return 1;
        }
        protected void onProgressUpdate(Integer ... values) {
            FCMToken = SharedApplication.yourUser.FCMToken;
            if(SharedApplication.myUser.firstEnrolled.equals("T")) {
                ID = SharedApplication.myUser.id.replace(".","");
            } else {
                ID = SharedApplication.yourUser.id.replace(".","");
            }
            chatRef = FirebaseDatabase.getInstance().getReference("/chat_list/id"+ID);
            getFirebaseDatabase();
        }
        protected void onPostExecute(Integer result) {

        }
        protected void onCancelled() {

        }
    }



}
