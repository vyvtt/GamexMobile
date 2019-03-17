package com.gamex.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gamex.fragments.BookmarkCompanyFragment;
import com.gamex.fragments.BookmarkExhibitionFragment;

public class BookmarkTabAdapter extends FragmentPagerAdapter {

    private String title[] = {"Exhibition", "Company"};

    public BookmarkTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                return new BookmarkExhibitionFragment();
            case 1:
                return new BookmarkCompanyFragment();
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
