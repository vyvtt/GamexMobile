package com.gamex.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gamex.fragments.ExDetailFragment;
import com.gamex.fragments.ExDetailListCompanyFragment;

public class EventDetailTabAdapter extends FragmentPagerAdapter {
    private String title[] = {"Details", "List Company"};
    private String exhibitionId;

    public EventDetailTabAdapter(FragmentManager fm, String exhibitionId) {
        super(fm);
        this.exhibitionId = exhibitionId;
    }

    @Override
    public Fragment getItem(int i) {
        Bundle bundle = new Bundle();
        bundle.putString("exhibitionId", exhibitionId);
        switch (i){
            case 0:
                ExDetailFragment detailFragment = new ExDetailFragment();
                detailFragment.setArguments(bundle);
                return detailFragment;
            case 1:
                ExDetailListCompanyFragment listCompanyFragment = new ExDetailListCompanyFragment();
                listCompanyFragment.setArguments(bundle);
                return listCompanyFragment;
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