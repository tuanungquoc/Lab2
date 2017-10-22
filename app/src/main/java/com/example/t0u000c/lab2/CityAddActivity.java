package com.example.t0u000c.lab2;

import android.support.v4.app.Fragment;

import java.util.UUID;

/**
 * Created by t0u000c on 10/21/17.
 */

public class CityAddActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        UUID cityId = (UUID) getIntent().getSerializableExtra(CityAddFragment.EXTRA_CITY_ID);
        return CityAddFragment.newInstance(cityId);
    }


}
