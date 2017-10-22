package com.example.t0u000c.lab2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by t0u000c on 10/21/17.
 */

public class CityDetailFragment extends Fragment {

    private RecyclerView mDailyForecastView;
    private RecyclerView mFutureForecastView;

    private RecyclerView.Adapter mDailyForecastAdapter;
    private RecyclerView.Adapter mFutureForecastAdapter;

    private RecyclerView.LayoutManager mDailyForecastLayoutManager;
    private RecyclerView.LayoutManager mFutureForecastLayoutManager;

    private City mCity;

    private ArrayList<String> dailyForecastDataset;
    private ArrayList<String> futureForecastDataset;

    private TextView mCityHeader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_city_detail,parent,false);
        UUID cityId = (UUID) getActivity().getIntent().getSerializableExtra(CityAddFragment.EXTRA_CITY_ID);
        mCity  = CityListSingleton.get(getActivity()).getCity(cityId);
        Log.d("TEST",mCity.getCityName());
        mCityHeader = (TextView) v.findViewById(R.id.city_header_name);
        mCityHeader.setText(mCity.getCityName());

        /**
         * Daily forecast
         */

        mDailyForecastView = (RecyclerView) v.findViewById(R.id.daily_forecast_view);
        mDailyForecastView.setHasFixedSize(true);
        // use a linear layout manager
        mDailyForecastLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        mDailyForecastView.setLayoutManager(mDailyForecastLayoutManager);
        dailyForecastDataset = new ArrayList<String>();
        for(int i = 0 ; i < 24 ; i++){
            dailyForecastDataset.add( i + " HOUR") ;
        }
        mDailyForecastAdapter = new DailyForecastAdapter(dailyForecastDataset);
        mDailyForecastView.setAdapter(mDailyForecastAdapter);

        /**
         * Future forecast
         */
        mFutureForecastView = (RecyclerView) v.findViewById(R.id.future_forecast_view);
        mFutureForecastView.setHasFixedSize(true);
        // use a linear layout manager
        mFutureForecastLayoutManager = new LinearLayoutManager(getActivity());
        mFutureForecastView.setLayoutManager(mFutureForecastLayoutManager);
        futureForecastDataset = new ArrayList<String>();
        for(int i = 0 ; i < 15 ; i++){
            futureForecastDataset.add( i + " Day") ;
        }
        // specify an adapter (see also next example)
        mFutureForecastAdapter = new FutureForecastAdapter(futureForecastDataset);
        mFutureForecastView.setAdapter(mFutureForecastAdapter);



        return v;

    }
}
