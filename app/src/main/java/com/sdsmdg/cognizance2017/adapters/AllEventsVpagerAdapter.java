package com.sdsmdg.cognizance2017.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sdsmdg.cognizance2017.fragments.AllEventsRecyclerFragment;

public class AllEventsVpagerAdapter extends FragmentPagerAdapter {

    private static int DAYS = 3;
    private int choice;

    public AllEventsVpagerAdapter(FragmentManager fm, int choice) {
        super(fm);
        this.choice = choice;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will correspond to Day1
                return AllEventsRecyclerFragment.newInstance(0, choice);
            case 1: // Fragment # 0 - This will correspond to Day2
                return AllEventsRecyclerFragment.newInstance(1, choice);
            case 2: // Fragment # 1 - This will correspond to Day3
                return AllEventsRecyclerFragment.newInstance(2, choice);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return DAYS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Day " + (position + 1);
    }
}
