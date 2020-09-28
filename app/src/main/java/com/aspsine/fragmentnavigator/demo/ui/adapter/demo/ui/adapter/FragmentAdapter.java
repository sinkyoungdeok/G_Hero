package com.aspsine.fragmentnavigator.demo.ui.adapter.demo.ui.adapter;

import android.content.Context;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.aspsine.fragmentnavigator.FragmentNavigatorAdapter;
import com.aspsine.fragmentnavigator.demo.ui.adapter.demo.ui.fragment.ContactsFragment;
import com.aspsine.fragmentnavigator.demo.ui.adapter.demo.ui.fragment.MainFragment;
import com.aspsine.fragmentnavigator.demo.ui.fragment.CalenderFragment;
import com.aspsine.fragmentnavigator.demo.ui.fragment.DdayFragment;
import com.aspsine.fragmentnavigator.demo.ui.fragment.EmoticonFragment;

/**
 * Created by aspsine on 16/3/31.
 */
public class FragmentAdapter implements FragmentNavigatorAdapter {

    private static final String TABS[] = {"Home", "Chats", "Emoticon", "Dday", "Calendar"};
    private String id;
    public FragmentAdapter(String id) {
        this.id = id;
    }

    @Override
    public Fragment onCreateFragment(int position) {
        if(position == 0) {
            return MainFragment.newInstance(id);
        }
        else if (position == 1){
            return ContactsFragment.newInstance(TABS[position]);
        }
        else if (position == 2) {
            return EmoticonFragment.newInstance(TABS[position]);
        }
        else if(position == 3){
            return DdayFragment.newInstance(id);
        }
        return CalenderFragment.newInstance(TABS[position]);
    }



    @Override
    public String getTag(int position) {
        if (position == 1) {
            return ContactsFragment.TAG;
        }
        return MainFragment.TAG + TABS[position];
    }

    @Override
    public int getCount() {
        return TABS.length;
    }
}
