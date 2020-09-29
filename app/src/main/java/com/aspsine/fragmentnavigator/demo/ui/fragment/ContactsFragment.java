package com.aspsine.fragmentnavigator.demo.ui.fragment;



import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aspsine.fragmentnavigator.FragmentNavigator;
import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.firebase.ChatFirebasePost;
import com.aspsine.fragmentnavigator.demo.ui.adapter.demo.ui.adapter.ChildFragmentAdapter;
import com.aspsine.fragmentnavigator.demo.ui.adapter.demo.ui.widget.TabLayout;
import com.aspsine.fragmentnavigator.demo.ui.adapter.demo.utils.SharedPrefUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {
    /* firebase */
    private DatabaseReference mPostReference;
    String ID, name, content;
    long chatCnt=0;
    EditText contentET;
    Button btn;
    ListView listView;
    ArrayList<String> data;
    ArrayAdapter<String> arrayAdapter;
    /* firebase */
    public static final String TAG = com.aspsine.fragmentnavigator.demo.ui.adapter.demo.ui.fragment.ContactsFragment.class.getSimpleName();

    public static final String EXTRA_TEXT = "extra_text";

    private static final int MOCK_LOAD_DATA_DELAYED_TIME = 2000;

    private static Handler sHandler = new Handler(Looper.getMainLooper());

    private com.aspsine.fragmentnavigator.demo.ui.fragment.ContactsFragment.WeakRunnable mRunnable = new com.aspsine.fragmentnavigator.demo.ui.fragment.ContactsFragment.WeakRunnable(this);

    private String mText;

    private TextView tvText;

    private ProgressBar progressBar;

    public static Fragment newInstance(String text) {
        com.aspsine.fragmentnavigator.demo.ui.fragment.ContactsFragment fragment = new com.aspsine.fragmentnavigator.demo.ui.fragment.ContactsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mText = getArguments().getString(EXTRA_TEXT);

    }
    /*firebase*/
    public void getFirebaseDatabase(){
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange", "Data is Updated");

                data.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    ChatFirebasePost get = postSnapshot.getValue(ChatFirebasePost.class);
                    String[] info = {get.id, get.name, get.content, String.valueOf(get.chatCnt)};
                    if(chatCnt <= get.chatCnt)
                        chatCnt = get.chatCnt + 1;
                    String result = info[2];
                    data.add(result);
                    Log.d("getFirebaseDatabase", "key: " + key);
                    Log.d("getFirebaseDatabase", "info: " + info[0] + info[1] + info[2]);
                }
                arrayAdapter.clear();
                arrayAdapter.addAll(data);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("/id_list/id"+ID).addValueEventListener(postListener);
    }
    /*firebase*/

    /*firebase*/
    public void postFirebaseDatabase(boolean add){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            ChatFirebasePost post = new ChatFirebasePost(ID, name, content, chatCnt);
            postValues = post.toMap();
        }
        childUpdates.put("/id_list/id" + ID + "/cnt" + Long.toString(chatCnt) , postValues);
        mPostReference.updateChildren(childUpdates);
        clearET();
    }
    public void clearET () {
        contentET.setText("");
        //name = "";
        //ID = "";
    }
    /*firebase*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);
        getActivity().setTitle("채팅");
        /* firebase */
        data = new ArrayList<String>();
        contentET = (EditText)v.findViewById(R.id.contents);
        btn = (Button)v.findViewById(R.id.send);
        listView = (ListView)v.findViewById(R.id.chatlist);
        mPostReference = FirebaseDatabase.getInstance().getReference();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                content = contentET.getText().toString();

                postFirebaseDatabase(true);
            }
        });
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        listView.setAdapter(arrayAdapter);
        ID = "1";
        name = "1";
        getFirebaseDatabase();
        /* firebase */

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //tvText = (TextView) view.findViewById(R.id.tvText);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            loadData();
        } else {
            mText = savedInstanceState.getString(EXTRA_TEXT);
            bindData();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_TEXT, mText);
    }

    @Override
    public void onDestroyView() {
        sHandler.removeCallbacks(mRunnable);
        tvText = null;
        progressBar = null;
        super.onDestroyView();
    }

    private void showProgressBar(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void bindData() {
        boolean isLogin = SharedPrefUtils.isLogin(getActivity());
        //tvText.setText(mText + "\n" + "Loginbbb:" + isLogin);
    }

    /**
     * mock load data
     */
    private void loadData() {
        showProgressBar(true);
        sHandler.postDelayed(mRunnable, MOCK_LOAD_DATA_DELAYED_TIME);
    }

    private static class WeakRunnable implements Runnable {

        WeakReference<com.aspsine.fragmentnavigator.demo.ui.fragment.ContactsFragment> mMainFragmentReference;

        public WeakRunnable(com.aspsine.fragmentnavigator.demo.ui.fragment.ContactsFragment mainFragment) {
            this.mMainFragmentReference = new WeakReference<com.aspsine.fragmentnavigator.demo.ui.fragment.ContactsFragment>(mainFragment);
        }

        @Override
        public void run() {
            com.aspsine.fragmentnavigator.demo.ui.fragment.ContactsFragment mainFragment = mMainFragmentReference.get();
            if (mainFragment != null && !mainFragment.isDetached()) {
                mainFragment.showProgressBar(false);
                mainFragment.bindData();
            }
        }
    }
}
