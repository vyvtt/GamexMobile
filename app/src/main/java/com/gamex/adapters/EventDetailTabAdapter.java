package com.gamex.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gamex.fragments.ExDetailFragment;
import com.gamex.fragments.ExDetailListCompanyFragment;
import com.gamex.models.Exhibition;

import java.io.Serializable;

public class EventDetailTabAdapter extends FragmentPagerAdapter {
    private String title[] = {"Details", "Exhibitors"};
    private String exhibitionId;
    private Exhibition exhibitionDetail;

    public EventDetailTabAdapter(FragmentManager fm, Exhibition exhibitionDetail) {
        super(fm);
        this.exhibitionDetail = exhibitionDetail;
    }

    @Override
    public Fragment getItem(int i) {
        Bundle bundle = new Bundle();

        switch (i){
            case 0:
                bundle.putSerializable("EXHIBITION_DETAILS", (Serializable) exhibitionDetail);
                ExDetailFragment detailFragment = new ExDetailFragment();
                detailFragment.setArguments(bundle);
                return detailFragment;
            case 1:
                bundle.putSerializable("EXHIBITION_LIST_COMPANY", (Serializable) exhibitionDetail.getListCompany());
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