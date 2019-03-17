package com.gamex.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gamex.fragments.RewardExchangeFragment;
import com.gamex.fragments.RewardHistoryFragment;


public class RewardTabAdapter extends FragmentPagerAdapter {

    private String title[] = {"Exchange Reward", "Reward History"};

    public RewardTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                return new RewardExchangeFragment();
            case 1:
                return new RewardHistoryFragment();
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
