package com.aspsine.fragmentnavigator.demo.ui.adapter.demo.ui.fragment;


import android.content.Context;
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
import com.aspsine.fragmentnavigator.demo.firebase.ChatFirebasePost;
import com.aspsine.fragmentnavigator.demo.firebase.UserFirebasePost;
import com.aspsine.fragmentnavigator.demo.item.chatListviewitem;
import com.aspsine.fragmentnavigator.demo.listener.OnBackPressedListener;
import com.aspsine.fragmentnavigator.demo.listviewadapter.chatAdapter;
import com.aspsine.fragmentnavigator.demo.ui.activity.MainActivity;
import com.aspsine.fragmentnavigator.demo.ui.widget.BottomNavigatorView;
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
import java.util.Date;
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

    private static String id;

    private static UserFirebasePost myUser, yourUser;

    public static Fragment newInstance(UserFirebasePost myuser, UserFirebasePost youruser) {
        ContactsFragment fragment = new ContactsFragment();
        myUser = myuser;
        yourUser = youruser;
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
        /*
        chatRef.addChildEventListener(new ChildEventListener() {
            Map<String, Object> readUserMap = new HashMap<>();
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                ChatFirebasePost get = dataSnapshot.getValue(ChatFirebasePost.class);
                String[] info = {get.id, get.name, get.content, get.date};
                chatListviewitem item;
                if(info[1].equals(myUser.name)) {
                    item = new chatListviewitem(myUser.profileUrl, info[2], info[3].split("/")[1] ,myUser.name, true);
                } else {
                    item = new chatListviewitem(yourUser.profileUrl, info[2], info[3].split("/")[1] ,yourUser.name, false);
                }
                get.readUsers.put(myUser.FCMToken,true);
                readUserMap.put(key,get);
                data.add(item);
                adapter.notifyDataSetChanged();
                listView.setSelection(data.size()-1);



            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        */

        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                Map<String, Object> readUserMap = new HashMap<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    ChatFirebasePost get = postSnapshot.getValue(ChatFirebasePost.class);
                    String[] info = {get.id, get.name, get.content, get.date};

                    chatListviewitem item;
                    if(info[1].equals(myUser.name)) { // 내 채팅
                        if(get.readUsers.size() == 2) { // 상대방이 읽었을때
                            item = new chatListviewitem(myUser.profileUrl, info[2], info[3].split("/")[1], myUser.name, true,true);
                        } else { // 상대방이 아직 안읽었을때
                            item = new chatListviewitem(myUser.profileUrl, info[2], info[3].split("/")[1], myUser.name, true,false);
                        }
                    } else { // 상대방 채팅
                        item = new chatListviewitem(yourUser.profileUrl, info[2], info[3].split("/")[1] ,yourUser.name, false, true);
                    }
                    get.readUsers.put(myUser.name, true);
                    readUserMap.put(key,get);
                    data.add(item);
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
            ChatFirebasePost post = new ChatFirebasePost(myUser.id, myUser.name, content, todayStr);
            postValues = post.toMap();
        }
        SendNotification.sendNotification(FCMToken,myUser.name,content);
        mPostReference.child("/chat_list/id" + ID).push().setValue(postValues);
        clearET();
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
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL); // 새로 아이템 추가시 자동 스크롤이 이동되게 ==> 이미 잘되긴하는데 혹시몰라서 추가함
        mPostReference = FirebaseDatabase.getInstance().getReference();
        adapter = new chatAdapter(getContext(), R.layout.contact_item,data);
        listView.setAdapter(adapter);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                content = contentET.getText().toString();

                postFirebaseDatabase(true);
            }
        });

        FCMToken = yourUser.FCMToken;
        if(myUser.firstEnrolled.equals("T")) {
            ID = myUser.id.replace(".","");
        } else {
            ID = yourUser.id.replace(".","");
        }
        chatRef = FirebaseDatabase.getInstance().getReference("/chat_list/id"+ID);
        getFirebaseDatabase();
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
        menu.findItem(R.id.action_add).setVisible(false);
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



}
