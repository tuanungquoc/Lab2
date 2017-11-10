package com.example.t0u000c.lab2;

import android.support.v4.app.Fragment;

/**
 * Created by t0u000c on 11/9/17.
 */

public class SettingActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new SettingFragment();
    }
}
