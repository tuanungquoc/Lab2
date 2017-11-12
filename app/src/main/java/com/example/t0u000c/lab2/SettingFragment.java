package com.example.t0u000c.lab2;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;


/**
 * Created by t0u000c on 11/9/17.
 *
 * To allow user to change C/F settings
 */

public class SettingFragment extends Fragment {

    private Switch mTempType;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.setting_fragment, parent, false);
        final Switch simpleSwitch = (Switch) v.findViewById(R.id.setting_temp_type    );
        if(CityListSingleton.get(getActivity()).isDegreeCelcius())
            simpleSwitch.setChecked(true);
        else
            simpleSwitch.setChecked(false);

        simpleSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CityListSingleton.get(getActivity()).setDegreeCelcius(simpleSwitch.isChecked());
            }
        });
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        CityListSingleton.get(getActivity()).saveSettings();
    }

}
