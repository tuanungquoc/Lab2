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
 *
 * This Class defines functionality to fetch APIs weather and timezone data, clean , convert and populate
 * data to respective layout parameters.
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

    private final String photourl= "http://openweathermap.org/img/w/";

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

    // Call APIs:- timezoneDB for current time , timezone and ISO country data, and weather API to fetch weather data for 5days3hrs
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
                            //Get and set local date and ISO country info of the city chosen.
                            final JSONObject localzonedata;
                            try {
                                localzonedata = new JSONObject(response.toString());
                                String test = Api.getCurrentTime(localzonedata.getString("formatted"));
                                mToday.setText(Api.gettodayDate(localzonedata.getString("formatted")));
                                city.setIsoCountry(localzonedata.getString("countryCode"));
                                SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                localtime= newDateFormat.parse(localzonedata.getString("formatted"));

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

                                                    // Array to save 24 Hr data
                                                    dailyForecastDataset = new ArrayList<String>();
                                                    dailyForecastDataset.add( "Now"+System.getProperty("line.separator")+ photourl+mCity.getIcon()+".png"+System.getProperty("line.separator")+
                                                            (CityListSingleton.get(getActivity()).isDegreeCelcius() ? Api.getFarenheit(mCity.getTemp()) : mCity.getTemp()));


                                                    //Array to save next 4 days Noon time weather info
                                                    futureForecastDataset = new ArrayList<DayForecast>();
                                                    SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                    Date today = null;
                                                    try {
                                                        today = newDateFormat.parse(localzonedata.getString("formatted"));
                                                    } catch (ParseException e) {
                                                        Log.e("City:localtime in 5days", String.valueOf(e));
                                                        e.printStackTrace();
                                                    }
                                                    int counter5days=0;//To count 4 days data processing

                                                    // To fetch local hour and minute corresponding UTC Hour match for weather data
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

                                                    // To fetch local tomorrow's Noon time to corresponding UTC hour match for next 4 days weather
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
                                                    int localtimeHR= Integer.parseInt(newDateFormat.format(localtime));
                                                    Log.d("LOCAL TIME HR:",String.valueOf(localtimeHR));
                                                    int temp =localtimeHR;
                                                    int counter24hours =0;//counts 24 hour data
                                                    //Loop through fetched weather data
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
                                                            // To fetch 24 hour (3 hour interval) data by comparing current UTC time to weather data
                                                            if((weatherdatenowunix > localtimetoUTCunix) && counter24hours <8 ){
                                                                Log.d("INSIDE HOURLY", fiveDaysArray.getJSONObject(i).getString("dt_txt" ));

                                                                if(counter24hours ==0 && (Integer.parseInt(hr) == Integer.parseInt(UTCnowhourmatch))){
                                                                    dailyForecastDataset.set(0,"Now"+ System.getProperty("line.separator")+ photourl+fiveDaysArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon")+".png"+System.getProperty("line.separator")+
                                                                            (CityListSingleton.get(getActivity()).isDegreeCelcius() ? Api.getFarenheit( Double.parseDouble(fiveDaysArray.getJSONObject(i).getJSONObject("main").getString("temp"))) :
                                                                                    fiveDaysArray.getJSONObject(i).getJSONObject("main").getString("temp")));
                                                                    counter24hours--;
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
                                                                    dailyForecastDataset.add(ampm+System.getProperty("line.separator")+ photourl+fiveDaysArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon")+".png"+System.getProperty("line.separator")+
                                                                            (CityListSingleton.get(getActivity()).isDegreeCelcius() ? Api.getFarenheit( Double.parseDouble(fiveDaysArray.getJSONObject(i).getJSONObject("main").getString("temp"))) :
                                                                                    Double.parseDouble(fiveDaysArray.getJSONObject(i).getJSONObject("main").getString("temp"))));

                                                                    ;

                                                                }
                                                                Log.d("coubnter24hr:", String.valueOf(counter24hours));
                                                                counter24hours++;
                                                            }
                                                            // To fetch next 4 days data based on matched Noon time
                                                            if((hr.equals(localToUTCnoonmatch) && MyDate.after(localtoUTCincrementeddate)  && counter5days < 4)|| ((i== fiveDaysArray.length()-1) && counter5days==3)) {
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
                                                    //Setting 24 Hr and 4 days related adapters
                                                    mDailyForecastAdapter = new DailyForecastAdapter(getActivity(),dailyForecastDataset);
                                                    mDailyForecastView.setAdapter(mDailyForecastAdapter);

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
        //If F chosen, convert C to F
        double tempMax = CityListSingleton.get(getActivity()).isDegreeCelcius() ? Api.getFarenheit( mCity.getTemp_max()):
                mCity.getTemp_max();
        mMax.setText(tempMax + "" + (char) 0x00B0);
        Log.d("SeetingLAyout",Double.toString( tempMax));
        mMin = (TextView) v.findViewById(R.id.min);
        double tempMin = CityListSingleton.get(getActivity()).isDegreeCelcius() ? Api.getFarenheit( mCity.getTemp_min()):
                mCity.getTemp_min();
        mMin.setText(tempMin +  "" + (char) 0x00B0);
        Log.d("SeetingLAyout",Double.toString(tempMin));

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

    // To  verify and seek permission to track location
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
