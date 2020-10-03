package com.aspsine.fragmentnavigator.demo.ui.adapter.demo.ui.adapter;

import androidx.fragment.app.Fragment;

import com.aspsine.fragmentnavigator.FragmentNavigatorAdapter;
import com.aspsine.fragmentnavigator.demo.firebase.UserFirebasePost;
import com.aspsine.fragmentnavigator.demo.ui.adapter.demo.ui.fragment.ContactsFragment;
import com.aspsine.fragmentnavigator.demo.ui.adapter.demo.ui.fragment.MainFragment;

/**
 * Created by aspsine on 16/4/3.
 */
public class ChildFragmentAdapter implements FragmentNavigatorAdapter {

    public static final String[] TABS = {"Friends", "Groups", "Official"};

    UserFirebasePost temp;
    @Override
    public Fragment onCreateFragment(int position) {
        return ContactsFragment.newInstance(temp,temp);
    }

    @Override
    public String getTag(int position) {
        return MainFragment.TAG + TABS[position];
    }

    @Override
    public int getCount() {
        return TABS.length;
    }
}
