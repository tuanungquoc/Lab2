package com.example.t0u000c.lab2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import android.Manifest;

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

    private TextView mCityHeader, mToday, mWeatherStatus ,mMax, mMin, mCurrentLocation;
   // private networkConnect nc;
    private View v;

    private Geocoder geocoder;
    private LocationRequest mLocationRequest;
    private static int INTERVAL_UPDATE = 10000;
    private static int FASTEST_INTERVAL = 2000;

    private String[] hourData = new String[8];
    private Date localtime= null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID cityId = (UUID) getArguments().getSerializable(CityAddFragment.EXTRA_CITY_ID);
        mCity = CityListSingleton.get(getActivity()).getCity(cityId);
        setHasOptionsMenu(true);
        if (mCity.getCityName() != null) {
            processCityViewTimeZone(mCity);

        }
    }





    public  void processCityViewTimeZone(final City city){

        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    Api.requestTimeZoneData(city.getLat()+"",city.getLon()+""), new JSONObject(),
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response)
                        {
                            mToday=  (TextView) v.findViewById(R.id.today);
                            //Get and set local date of the city chosen.
                            final JSONObject localzonedata;
                            try {
                                localzonedata = new JSONObject(response.toString());
                                String test = Api.getCurrentTime(localzonedata.getString("formatted"));
                                mToday.setText(Api.gettodayDate(localzonedata.getString("formatted")));
                                city.setIsoCountry(localzonedata.getString("countryCode"));
                                SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                localtime= newDateFormat.parse(localzonedata.getString("formatted"));
                                //w.localtimetoUTCunix=con.getUrlData(Api.convertLocalToUTC(object2.getString("zoneName"),newDateFormat.format(localtime)));


                                final long localtimetoUTCunix = Long.parseLong(localzonedata.getString("timestamp")) -Long.parseLong( localzonedata.getString("gmtOffset"));
                                Date todaytimereset= null;
                                try {
                                    todaytimereset = Api.incrementDate(localtime);
                                } catch (ParseException e) {
                                    Log.e("Time reset", String.valueOf(e));
                                    e.printStackTrace();
                                }
                                final long tomorrowlocaldatetoUTCunix= Api.datetoUnix(todaytimereset, localzonedata.getString("zoneName"));;

                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                                        Api.requestCity24Hrs(city.getCityName() + "," + city.getIsoCountry()), new JSONObject(),
                                        new Response.Listener<JSONObject>()
                                        {
                                            @Override
                                            public void onResponse(JSONObject response)
                                            {
                                                JSONObject fiveDays = null;
                                                try {
                                                    fiveDays = new JSONObject(response.toString());

                                                    JSONArray fiveDaysArray = fiveDays.getJSONArray("list");
                                                    Log.d("FIVE DARYS", String.valueOf(fiveDaysArray));

                                                    dailyForecastDataset = new ArrayList<String>();
                                                    dailyForecastDataset.add( "Now"+System.getProperty("line.separator")+ mCity.getWeather()+System.getProperty("line.separator")+
                                                            (CityListSingleton.get(getActivity()).isDegreeCelcius() ? Api.getFarenheit(mCity.getTemp()) : mCity.getTemp()));


                                                    futureForecastDataset = new ArrayList<DayForecast>();
                                                    SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                    Date today = null;
                                                    try {
                                                        today = newDateFormat.parse(localzonedata.getString("formatted"));
                                                    } catch (ParseException e) {
                                                        Log.e("City:localtime in 5days", String.valueOf(e));
                                                        e.printStackTrace();
                                                    }
                                                    int counter5days=0;

                                                    String UTCnowdatestring = Api.utcToDate(localtimetoUTCunix,"Antarctica/Troll");
                                                    Date UTCnowdate = newDateFormat.parse(UTCnowdatestring);
                                                    Calendar calendar = Calendar.getInstance(); // creates a new calendar instance
                                                    calendar.setTime(UTCnowdate);   // assigns calendar to given date
                                                    float UTCnowdatehour= calendar.get(Calendar.HOUR_OF_DAY);
                                                    Log.d("UTC MINUTES NOW:",String.valueOf(calendar.get(Calendar.MINUTE)));
                                                    UTCnowdatehour=UTCnowdatehour+(calendar.get(Calendar.MINUTE)/60.0f);
                                                    Log.d("UTC NOW HOUR",String.valueOf(UTCnowdatehour) );
                                                    String UTCnowhourmatch = Api.getMatch(UTCnowdatehour);
                                                    Log.d("UTC NOW HOUR MATCH",String.valueOf(UTCnowhourmatch) );


                                                    String localtoUTCincrementedstring =Api.utcToDate(tomorrowlocaldatetoUTCunix, "Antarctica/Troll");
                                                    Date localtoUTCincrementeddate= newDateFormat.parse(localtoUTCincrementedstring);
                                                    Log.d("Incremented utcdate00hr", String.valueOf(localtoUTCincrementeddate));
                                                    Calendar cal = Calendar.getInstance(); // creates calendar
                                                    cal.setTime(localtoUTCincrementeddate);// sets calendar time/date
                                                    cal.add(Calendar.HOUR_OF_DAY, 12); // adds 12 hour
                                                    String localtoUTCincrementednoonstring = newDateFormat.format(cal.getTime());
                                                    Log.d("local2utc's tmrw noon",localtoUTCincrementednoonstring);
                                                    Date localtoUTCincrementednoon= newDateFormat.parse(localtoUTCincrementednoonstring);
                                                    newDateFormat.applyPattern("HH");
                                                    int localToUTCnoon= Integer.parseInt(newDateFormat.format(localtoUTCincrementednoon));
                                                    String localToUTCnoonmatch = Api.getMatch(localToUTCnoon);
                                                    Log.d("Matched Noon:",localToUTCnoonmatch );
                                                    //Date localtime= newDateFormat.parse(localzonedata.getString("formatted"));
                                                    int localtimeHR= Integer.parseInt(newDateFormat.format(localtime));
                                                    Log.d("LOCAL TIME HR:",String.valueOf(localtimeHR));
                                                    int temp =localtimeHR;
                                                    int counter24hours =0;
                                                    for(int i = 0 ; i < fiveDaysArray.length() ; i++){
                                                        SimpleDateFormat newdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                                        try {
                                                            Log.d(String.valueOf(i), fiveDaysArray.getJSONObject(i).getString("dt_txt"));
                                                            long weatherdatenowunix = Long.parseLong(fiveDaysArray.getJSONObject(i).getString("dt"));
                                                            Date MyDate = newdf.parse(fiveDaysArray.getJSONObject(i).getString("dt_txt"));
                                                            Log.d("MYYDAte:", String.valueOf(MyDate));
                                                            newdf.applyPattern("HH");
                                                            String hr= String.valueOf(Integer.parseInt(newdf.format(MyDate)));

                                                            Log.d("formating mydate", hr);
                                                            Log.d("is match true?", String.valueOf(MyDate.after(localtoUTCincrementeddate)));

                                                            if((weatherdatenowunix > localtimetoUTCunix) && counter24hours <8 ){
                                                                Log.d("INSIDE HOURLY", fiveDaysArray.getJSONObject(i).getString("dt_txt" ));

                                                                if(counter24hours ==0 && (Integer.parseInt(hr) == Integer.parseInt(UTCnowhourmatch))){
                                                                    dailyForecastDataset.set(counter24hours,"Now"+ System.getProperty("line.separator")+ fiveDaysArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main")+System.getProperty("line.separator")+
                                                                            (CityListSingleton.get(getActivity()).isDegreeCelcius() ? Api.getFarenheit( Double.parseDouble(fiveDaysArray.getJSONObject(i).getJSONObject("main").getString("temp"))) :
                                                                                    Double.parseDouble(fiveDaysArray.getJSONObject(i).getJSONObject("main").getString("temp"))));                                                                    counter24hours--;
                                                                }else{
                                                                    temp=temp+3;
                                                                    if(temp>=24){
                                                                        temp-=24;
                                                                    }
                                                                    String ampm= null;
                                                                    if(temp==12)
                                                                        ampm="Noon";
                                                                    else if(temp<12)
                                                                        ampm=temp+"AM";
                                                                    else
                                                                        ampm=temp+"PM";
                                                                    dailyForecastDataset.add(ampm+System.getProperty("line.separator")+ fiveDaysArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main")+System.getProperty("line.separator")+
                                                                            (CityListSingleton.get(getActivity()).isDegreeCelcius() ? Api.getFarenheit( Double.parseDouble(fiveDaysArray.getJSONObject(i).getJSONObject("main").getString("temp"))) :
                                                                                    Double.parseDouble(fiveDaysArray.getJSONObject(i).getJSONObject("main").getString("temp"))));

                                                                    ;

                                                                }
                                                                Log.d("coubnter24hr:", String.valueOf(counter24hours));
                                                                counter24hours++;
                                                            }

                                                            if((hr.equals(localToUTCnoonmatch) && MyDate.after(localtoUTCincrementeddate))|| ((i== fiveDaysArray.length()-1) && counter5days==4)) {
                                                                Log.d("DAte:", String.valueOf(MyDate));
                                                                newdf.applyPattern("EEE");
                                                                DayForecast dayForecast = new DayForecast(newdf.format(MyDate),

                                                                        CityListSingleton.get(getActivity()).isDegreeCelcius() ? Api.getFarenheit( Double.parseDouble(fiveDaysArray.getJSONObject(i).getJSONObject("main").getString("temp_max"))) +"":
                                                                                fiveDaysArray.getJSONObject(i).getJSONObject("main").getString("temp_max"),
                                                                        CityListSingleton.get(getActivity()).isDegreeCelcius() ? Api.getFarenheit( Double.parseDouble(fiveDaysArray.getJSONObject(i).getJSONObject("main").getString("temp_min"))) +"":
                                                                                fiveDaysArray.getJSONObject(i).getJSONObject("main").getString("temp_min"),
                                                                        fiveDaysArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon"));
                                                                futureForecastDataset.add(dayForecast);
                                                                counter5days++;
                                                            }
                                                        } catch (ParseException e) {
                                                            Log.e("parsing error","Error in parsing at hourly" );
                                                            e.printStackTrace();
                                                        }

                                                    }

                                                    mDailyForecastAdapter = new DailyForecastAdapter(dailyForecastDataset);
                                                    mDailyForecastView.setAdapter(mDailyForecastAdapter);
                                                    // specify an adapter (see also next example)
                                                    mFutureForecastAdapter = new FutureForecastAdapter(getActivity(),futureForecastDataset);
                                                    mFutureForecastView.setAdapter(mFutureForecastAdapter);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener()
                                        {
                                            @Override
                                            public void onErrorResponse(VolleyError error)
                                            {

                                            }
                                        });
                                NetworkSingleton.get(getActivity()).addRequest(jsonObjectRequest,"City View Header Current Date");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            processCityViewTimeZone(mCity);
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetworkSingleton.get(getActivity()).addRequest(jsonObjectRequest,"Getting local time");



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
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
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


        /**
         * Future forecast
         */
        mFutureForecastView = (RecyclerView) v.findViewById(R.id.future_forecast_view);
        mFutureForecastView.setHasFixedSize(true);
        // use a linear layout manager
        mFutureForecastLayoutManager = new LinearLayoutManager(getActivity());
        mFutureForecastView.setLayoutManager(mFutureForecastLayoutManager);


        mCurrentLocation = (TextView) v.findViewById(R.id.current_location);
        startLocationUpdates();
        getLastLocation();

        mCityHeader = (TextView) v.findViewById(R.id.city_header_name);
        mCityHeader.setText(mCity.getCityName());
        mWeatherStatus = (TextView) v.findViewById(R.id.weatherStatus);
        mWeatherStatus.setText(mCity.getWeather());
        Log.d("SeetingLAyout",mCity.getWeather());
        mMax = (TextView) v.findViewById(R.id.max);
        mMax.setText(mCity.getTemp_max() + "" + (char) 0x00B0);
        Log.d("SeetingLAyout",Double.toString( mCity.getTemp_max()));
        mMin = (TextView) v.findViewById(R.id.min);
        mMin.setText(mCity.getTemp_min() +  "" + (char) 0x00B0);
        Log.d("SeetingLAyout",Double.toString( mCity.getTemp_min()));

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


//    private class networkConnect extends AsyncTask<String, Void ,Wrapper> {
//
//        private Context mContext;
//
//        public networkConnect(Context c){
//            mContext = c;
//        }
//        //@Override
//        protected void onPostExecute(Wrapper w) {
//            Log.d("POST ", "inside");
//            super.onPostExecute(w);
//            Log.d("Got day values JSON:",w.dayWeather );
//            Log.d("Got hourly values JSON:",w.hourlyweather );
//            Log.d("Got timezone data:",w.localtimezone );
//            Log.d("Got localtime-UTC unix:",Long.toString(w.localtimetoUTCunix ));
//            Log.d("Got local+1-UTC unix:",Long.toString(w.tomorrowlocaldatetoUTCunix) );
//            String stream = w.dayWeather;
//            JSONObject object = null;
//            JSONObject localzonedata =null;
//            try {
//                object = new JSONObject(stream);
//
//                JSONArray weatherArray = object.getJSONArray("weather");
//                JSONObject  weatherObject = weatherArray.getJSONObject(0);
//                Log.d("SetValues",weatherObject.getString("main") );
//                mCity.setWeather(weatherObject.getString("main"));
//                JSONObject mainObject = object.getJSONObject("main");
//                String  min = mainObject.getString("temp_min");
//                Log.d("SetValues",min );
//                mCity.setTemp_min(Double.valueOf(min));
//                String  max = mainObject.getString("temp_max");
//                Log.d("SetValues",max );
//                mCity.setTemp_max(Double.valueOf(max));
//                String  tempp = mainObject.getString("temp");
//                Log.d("SetValues",tempp );
//                mCity.setTemp(Double.valueOf(tempp));
//
//                Log.d("TEST",mCity.getCityName());
//                mCityHeader = (TextView) v.findViewById(R.id.city_header_name);
//                mCityHeader.setText(mCity.getCityName());
//
//                mToday=  (TextView) v.findViewById(R.id.today);
//                //Get and set local date of the city chosen.
//                localzonedata = new JSONObject(w.localtimezone);
//
//                try {
//                    mToday.setText(Api.gettodayDate(localzonedata.getString("formatted")));
//                } catch (ParseException e) {
//                    Log.e("City:localtime", String.valueOf(e));
//                    e.printStackTrace();
//                }
//
//
//                mWeatherStatus = (TextView) v.findViewById(R.id.weatherStatus);
//                mWeatherStatus.setText(mCity.getWeather());
//                Log.d("SeetingLAyout",mCity.getWeather());
//                mMax = (TextView) v.findViewById(R.id.max);
//                mMax.setText(Double.toString(mCity.getTemp_max()));
//                Log.d("SeetingLAyout",Double.toString( mCity.getTemp_max()));
//                mMin = (TextView) v.findViewById(R.id.min);
//                mMin.setText(Double.toString(mCity.getTemp_min()));
//                Log.d("SeetingLAyout",Double.toString( mCity.getTemp_min()));
//
//
//                JSONObject fiveDays = new JSONObject(w.hourlyweather);
//                JSONArray fiveDaysArray = fiveDays.getJSONArray("list");
//                Log.d("FIVE DARYS", String.valueOf(fiveDaysArray));
//                futureForecastDataset = new ArrayList<DayForecast>();
//                SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date today = null;
//                try {
//                    today = newDateFormat.parse(localzonedata.getString("formatted"));
//                } catch (ParseException e) {
//                    Log.e("City:localtime in 5days", String.valueOf(e));
//                    e.printStackTrace();
//                }
//                int counter5days=0;
//
//                String localtoUTCincrementedstring =Api.utcToDate(w.tomorrowlocaldatetoUTCunix, "Antarctica/Troll");
//                Date localtoUTCincrementeddate= newDateFormat.parse(localtoUTCincrementedstring);
//                Log.d("Incremented utcdate00hr", String.valueOf(localtoUTCincrementeddate));
//                Calendar cal = Calendar.getInstance(); // creates calendar
//                cal.setTime(localtoUTCincrementeddate);// sets calendar time/date
//                cal.add(Calendar.HOUR_OF_DAY, 12); // adds 12 hour
//                String localtoUTCincrementednoonstring = newDateFormat.format(cal.getTime());
//                Log.d("local2utc's tmrw noon",localtoUTCincrementednoonstring);
//                Date localtoUTCincrementednoon= newDateFormat.parse(localtoUTCincrementednoonstring);
//                newDateFormat.applyPattern("HH");
//                int localToUTCnoon= Integer.parseInt(newDateFormat.format(localtoUTCincrementednoon));
//                String localToUTCnoonmatch = Api.getNoon(localToUTCnoon);
//                Log.d("Matched Noon:",localToUTCnoonmatch );
//                for(int i = 0 ; i < fiveDaysArray.length() ; i++){
//                    SimpleDateFormat newdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//                    try {
//                        Log.d(String.valueOf(i), fiveDaysArray.getJSONObject(i).getString("dt_txt"));
//                        Date MyDate = newdf.parse(fiveDaysArray.getJSONObject(i).getString("dt_txt"));
//                        Log.d("MYYDAte:", String.valueOf(MyDate));
//                        newdf.applyPattern("HH");
//                        String hr= String.valueOf(Integer.parseInt(newdf.format(MyDate)));
//
//                        Log.d("formating mydate", hr);
//                        Log.d("is match true?", String.valueOf(MyDate.after(localtoUTCincrementeddate)));
//
//                        if((hr.equals(localToUTCnoonmatch) && MyDate.after(localtoUTCincrementeddate))|| ((i== fiveDaysArray.length()-1) && counter5days==4)) {
//                            Log.d("DAte:", String.valueOf(MyDate));
//                            newdf.applyPattern("EEE");
//                            DayForecast dayForecast = new DayForecast(newdf.format(MyDate),
//                                    fiveDaysArray.getJSONObject(i).getJSONObject("main").getString("temp_max"),
//                                    fiveDaysArray.getJSONObject(i).getJSONObject("main").getString("temp_min"),
//                                    fiveDaysArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon"));
//                            futureForecastDataset.add(dayForecast);
//                            counter5days++;
//                        }
//                    } catch (ParseException e) {
//                        Log.e("parsing error","Error in parsing at hourly" );
//                        e.printStackTrace();
//                    }
//
//                }
//                // specify an adapter (see also next example)
//                mFutureForecastAdapter = new FutureForecastAdapter(getActivity(),futureForecastDataset);
//                mFutureForecastView.setAdapter(mFutureForecastAdapter);
//            } catch (JSONException e) {
//                Log.e("In parsing city detail", String.valueOf(e));
//                e.printStackTrace();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        @Override
//        protected Wrapper doInBackground(String... strings) {
//            Connection con = new Connection();
//            Wrapper w = new Wrapper();
//            w.dayWeather = con.getUrlData(Api.requestCityNow(mCity.getCityName() + "," + mCity.getIsoCountry()));
//            w.hourlyweather = con.getUrlData(Api.requestCity24Hrs(mCity.getCityName() + "," + mCity.getIsoCountry()));
//            try {
//                JSONObject object = new JSONObject(w.dayWeather);
//                JSONObject coord = object.getJSONObject("coord");
//                String lon = coord.getString("lon");
//                String lat = coord.getString("lat");
//                w.localtimezone = con.getUrlData(Api.requestTimeZoneData(lat,lon));
//                JSONObject object2 = new JSONObject(w.localtimezone);
//                SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date localtime= newDateFormat.parse(object2.getString("formatted"));
//                //w.localtimetoUTCunix=con.getUrlData(Api.convertLocalToUTC(object2.getString("zoneName"),newDateFormat.format(localtime)));
//                w.localtimetoUTCunix = Long.parseLong(object2.getString("timestamp")) -Long.parseLong( object2.getString("gmtOffset"));
//                Date todaytimereset= null;
//                try {
//                    todaytimereset = Api.incrementDate(localtime);
//                } catch (ParseException e) {
//                    Log.e("Time reset", String.valueOf(e));
//                    e.printStackTrace();
//                }
//                w.tomorrowlocaldatetoUTCunix= Api.datetoUnix(todaytimereset, object2.getString("zoneName"));;
//
//            }catch(Exception e){
//                Log.e("Error in Background", String.valueOf(e));
//            }
//            Log.d("Background", "before returning");
//            return w;
//        }
//    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(INTERVAL_UPDATE);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this.getActivity());
        settingsClient.checkLocationSettings(locationSettingsRequest);


        getFusedLocationProviderClient(getActivity()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        try {
                            onLocationChanged(locationResult.getLastLocation());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                Looper.myLooper());

    }



    public void onLocationChanged(Location location) throws IOException {

        Log.d("Location update", + location.getLatitude() + " - " + location.getLongitude());

        LatLng locationLatLonObj = new LatLng(location.getLatitude(), location.getLongitude());
        List<Address> addresses;
        //parsing lat long position to get address and extract into addresses
        addresses = geocoder.getFromLocation(locationLatLonObj.latitude, locationLatLonObj.longitude, 1);
        if (mCity.getCityName().equals(addresses.get(0).getLocality())){
            mCurrentLocation.setText("You are here");
        }
    }




    public void getLastLocation() {
        //Checking to see if ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION permission get granted
        // if not then won't do anything
        if (ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this.getActivity());
        locationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // GPS location can be null if GPS is switched off
                if (location != null) {
                    try {
                        onLocationChanged(location);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d("Last location", "Error to get last location");
                e.printStackTrace();
            }
        });

    }
}
