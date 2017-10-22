package com.example.t0u000c.lab2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toolbar;

/**
 * Created by t0u000c on 10/21/17.
 */

public class CityListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CityListFragment();
    }


}
