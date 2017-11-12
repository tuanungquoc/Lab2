package com.example.t0u000c.lab2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by t0u000c on 10/22/17.
 *
 * To allow swipe to saved cities details
 */

public class CityDetailPagerActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private ArrayList<City> mCities;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);
        mCities = CityListSingleton.get(this).getmCities();
        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                return CityDetailFragment.newInstance(mCities.get(position).getmId());
            }

            @Override
            public int getCount() {
                return mCities.size();
            }
        });


        UUID cityId = (UUID)getIntent()
                .getSerializableExtra(CityAddFragment.EXTRA_CITY_ID);
        for (int i = 0; i < mCities.size(); i++) {
            if (mCities.get(i).getmId().equals(cityId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
