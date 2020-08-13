package com.aspsine.fragmentnavigator.demo.ui.fragment;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aspsine.fragmentnavigator.FragmentNavigator;
import com.aspsine.fragmentnavigator.demo.R;
import com.aspsine.fragmentnavigator.demo.ui.adapter.demo.ui.adapter.ChildFragmentAdapter;
import com.aspsine.fragmentnavigator.demo.utils.SharedPrefUtils;

import java.lang.ref.WeakReference;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    public static final String TAG = ContactsFragment.class.getSimpleName();

    public static final String EXTRA_TEXT = "extra_text";

    private static final int MOCK_LOAD_DATA_DELAYED_TIME = 2000;

    private static Handler sHandler = new Handler(Looper.getMainLooper());

    private ContactsFragment.WeakRunnable mRunnable = new ContactsFragment.WeakRunnable(this);

    private String mText;

    private TextView tvText;

    private ProgressBar progressBar;

    public static Fragment newInstance(String text) {
        ContactsFragment fragment = new ContactsFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvText = (TextView) view.findViewById(R.id.tvText);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
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
        tvText.setText(mText + "\n" + "Loginbbb:" + isLogin);
    }

    /**
     * mock load data
     */
    private void loadData() {
        showProgressBar(true);
        sHandler.postDelayed(mRunnable, MOCK_LOAD_DATA_DELAYED_TIME);
    }

    private static class WeakRunnable implements Runnable {

        WeakReference<ContactsFragment> mMainFragmentReference;

        public WeakRunnable(ContactsFragment mainFragment) {
            this.mMainFragmentReference = new WeakReference<ContactsFragment>(mainFragment);
        }

        @Override
        public void run() {
            ContactsFragment mainFragment = mMainFragmentReference.get();
            if (mainFragment != null && !mainFragment.isDetached()) {
                mainFragment.showProgressBar(false);
                mainFragment.bindData();
            }
        }
    }
}
