package com.gamex.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gamex.fragments.ExDetailFragment;
import com.gamex.fragments.YourExCurrentFragment;
import com.gamex.fragments.YourExPastFragment;

public class EventDetailTabAdapter extends FragmentPagerAdapter {
    private String title[] = {"Details", "List Company"};

    public EventDetailTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                return new ExDetailFragment();
            case 1:
                return new YourExPastFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}