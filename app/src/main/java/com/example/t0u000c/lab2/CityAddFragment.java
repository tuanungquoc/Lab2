package com.example.t0u000c.lab2;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.util.UUID;

/**
 * Created by t0u000c on 10/21/17.
 */

public class CityAddFragment extends Fragment {
    private City mCity;
    private AutoCompleteTextView autoCompleteTextView;
    public static final String EXTRA_CITY_ID = "com.tuan.android.cityintent.city_id";
    public static final String TAG1 ="testuan";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID cityId = (UUID)getArguments().getSerializable(EXTRA_CITY_ID);
        mCity = CityListSingleton.get(getActivity()).getCity(cityId);
    }

    public static CityAddFragment newInstance(UUID uId){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CITY_ID,uId);
        CityAddFragment fragment = new CityAddFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_city,parent,false);
        autoCompleteTextView = (AutoCompleteTextView) v.findViewById(R.id.autoCompleteTextView);
        String[] words = getResources().getStringArray(R.array.auto_complete);

//        UUID cityId = (UUID) getActivity().getIntent().getSerializableExtra(CityAddFragment.EXTRA_CITY_ID);
//        mCity = CityListSingleton.get(getActivity()).getCity(cityId);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,words);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String cityName = (String)autoCompleteTextView.getAdapter().getItem(i);
                Log.d(TAG1,cityName + " is selected");
                //serch to see if city already exists
                int pos = CityListSingleton.get(getActivity()).getCityByName(cityName);
                if( pos == -1) {
                    mCity.setCityName(cityName);

                }else{
                    CityListSingleton.get(getActivity()).getmCities().remove(CityListSingleton.get(getActivity()).getmCities().size()-1);
                    Toast.makeText(getActivity(),cityName + " already added",Toast.LENGTH_LONG).show();
                }
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
            }

        });
        return v;
    }
}
