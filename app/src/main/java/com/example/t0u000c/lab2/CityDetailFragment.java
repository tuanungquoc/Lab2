package com.example.t0u000c.lab2;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private ArrayList<DayForecast> futureForecastDataset;

    private TextView mCityHeader, mToday, mWeatherStatus ,mMax, mMin;
    private networkConnect nc;
    private View v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID cityId = (UUID) getArguments().getSerializable(CityAddFragment.EXTRA_CITY_ID);
        mCity = CityListSingleton.get(getActivity()).getCity(cityId);
        setHasOptionsMenu(true);
        if (mCity.getCityName() != null) {
            nc = new networkConnect();
            nc.execute();
        }
    }

    public static CityDetailFragment newInstance(UUID cityId){
        Bundle args = new Bundle();
        args.putSerializable(CityAddFragment.EXTRA_CITY_ID,cityId);
        CityDetailFragment fragment = new CityDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_city_detail, parent, false);
        if (NavUtils.getParentActivityName(getActivity()) != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
       /* futureForecastDataset = new ArrayList<DayForecast>();
        for(int i = 0 ; i < 5 ; i++){
            DayForecast dayForecast = new DayForecast(i+" Day", "50 celcius" , "");
            futureForecastDataset.add( dayForecast) ;
        }
        // specify an adapter (see also next example)
        mFutureForecastAdapter = new FutureForecastAdapter(futureForecastDataset);
        mFutureForecastView.setAdapter(mFutureForecastAdapter);
*/


        return v;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // To be implemented next
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }


    private class networkConnect extends AsyncTask<String, Void ,Wrapper> {


        //@Override
        protected void onPostExecute(Wrapper w) {
            Log.d("POST ", "inside");
            super.onPostExecute(w);
            Log.d("Got day values JSON:",w.dayWeather );
            Log.d("Got hourly values JSON:",w.hourlyweather );
            String stream = w.dayWeather;
            JSONObject object = null;
            try {
                object = new JSONObject(stream);
                JSONArray weatherArray = object.getJSONArray("weather");
                JSONObject  weatherObject = weatherArray.getJSONObject(0);
                Log.d("SetValues",weatherObject.getString("main") );
                mCity.setWeather(weatherObject.getString("main"));
                JSONObject mainObject = object.getJSONObject("main");
                String  min = mainObject.getString("temp_min");
                Log.d("SetValues",min );
                mCity.setTemp_min(Double.valueOf(min));
                String  max = mainObject.getString("temp_max");
                Log.d("SetValues",max );
                mCity.setTemp_max(Double.valueOf(max));

                Log.d("TEST",mCity.getCityName());
                mCityHeader = (TextView) v.findViewById(R.id.city_header_name);
                mCityHeader.setText(mCity.getCityName());
                mToday=  (TextView) v.findViewById(R.id.today);
                mToday.setText(Api.gettodayDate());
                mWeatherStatus = (TextView) v.findViewById(R.id.weatherStatus);
                mWeatherStatus.setText(mCity.getWeather());
                Log.d("SeetingLAyout",mCity.getWeather());
                mMax = (TextView) v.findViewById(R.id.max);
                mMax.setText(Double.toString(mCity.getTemp_max()));
                Log.d("SeetingLAyout",Double.toString( mCity.getTemp_max()));
                mMin = (TextView) v.findViewById(R.id.min);
                mMin.setText(Double.toString(mCity.getTemp_min()));
                Log.d("SeetingLAyout",Double.toString( mCity.getTemp_min()));

                 /*mFutureForecastView = (RecyclerView) v.findViewById(R.id.future_forecast_view);
        mFutureForecastView.setHasFixedSize(true);
        // use a linear layout manager
        mFutureForecastLayoutManager = new LinearLayoutManager(getActivity());
        mFutureForecastView.setLayoutManager(mFutureForecastLayoutManager);
        futureForecastDataset = new ArrayList<DayForecast>();
        for(int i = 0 ; i < 5 ; i++){
            DayForecast dayForecast = new DayForecast(i+" Day", "50 celcius" , "");
            futureForecastDataset.add( dayForecast) ;
        }
        // specify an adapter (see also next example)
        mFutureForecastAdapter = new FutureForecastAdapter(futureForecastDataset);
        mFutureForecastView.setAdapter(mFutureForecastAdapter);*/

                //Future
//                mFutureForecastView = (RecyclerView) v.findViewById(R.id.future_forecast_view);
//                mFutureForecastView.setHasFixedSize(false);
//                // use a linear layout manager
//                mFutureForecastLayoutManager = new LinearLayoutManager(CityDetailFragment.this);
//                mFutureForecastView.setLayoutManager(mFutureForecastLayoutManager);
                JSONObject fiveDays = new JSONObject(w.hourlyweather);
                JSONArray fiveDaysArray = fiveDays.getJSONArray("list");
                Log.d("FIVE DARYS", String.valueOf(fiveDaysArray));
                futureForecastDataset = new ArrayList<DayForecast>();
                for(int i = 0 ; i < fiveDaysArray.length() ; i++){

                    SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        Log.d(String.valueOf(i), fiveDaysArray.getJSONObject(i).getString("dt_txt"));
                        Date MyDate = newDateFormat.parse(fiveDaysArray.getJSONObject(i).getString("dt_txt"));
                        newDateFormat.applyPattern("HH");
                        Log.d("DAte:", newDateFormat.format(MyDate));
                        if(newDateFormat.format(MyDate).equals("12")) {
                            Log.d("DAte:", String.valueOf(MyDate));
                            newDateFormat.applyPattern("EEE");
                            DayForecast dayForecast = new DayForecast(newDateFormat.format(MyDate),
                                    fiveDaysArray.getJSONObject(i).getJSONObject("main").getString("temp_max"),
                                    fiveDaysArray.getJSONObject(i).getJSONObject("main").getString("temp_min"),
                                    fiveDaysArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon"));
                            futureForecastDataset.add(dayForecast);
                        }
                    } catch (ParseException e) {
                        Log.e("parsing error","Error in parsing at hourly" );
                        e.printStackTrace();
                    }

                }
                // specify an adapter (see also next example)
                mFutureForecastAdapter = new FutureForecastAdapter(getActivity(),futureForecastDataset);
                mFutureForecastView.setAdapter(mFutureForecastAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected Wrapper doInBackground(String... strings) {
            Connection con = new Connection();
            Wrapper w = new Wrapper();
            w.dayWeather = con.getUrlData(Api.requestCityNow(mCity.getCityName() + "," + mCity.getIsoCountry()));
            w.hourlyweather = con.getUrlData(Api.requestCity24Hrs(mCity.getCityName() + "," + mCity.getIsoCountry()));
            Log.d("Background", "before");
            return w;
        }
    }
}
