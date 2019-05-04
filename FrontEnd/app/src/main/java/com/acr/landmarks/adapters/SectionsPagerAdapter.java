package com.acr.landmarks.adapters;


import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.acr.landmarks.ui.LandmarkCardsFragment;
import com.acr.landmarks.ui.MapFragment;
import com.acr.landmarks.ui.ToursFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {


    private  BottomSheetBehavior sheetBehavior;

    public SectionsPagerAdapter(FragmentManager fm, BottomSheetBehavior behavior) {
        super(fm);
        sheetBehavior = behavior;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch(position){
            case 0:
                ToursFragment toursFragment = new ToursFragment();
                //sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                return toursFragment;
            case 1:
                MapFragment mapFragment = new MapFragment();
                //sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                return mapFragment;
            case 2:
                LandmarkCardsFragment cardsFragment = new LandmarkCardsFragment();
                //sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                return cardsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }


}