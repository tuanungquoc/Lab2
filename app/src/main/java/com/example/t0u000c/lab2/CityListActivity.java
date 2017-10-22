package com.example.t0u000c.lab2;

import android.support.v4.app.Fragment;

/**
 * Created by t0u000c on 10/21/17.
 */

public class CityListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CityListFragment();
    }


}
