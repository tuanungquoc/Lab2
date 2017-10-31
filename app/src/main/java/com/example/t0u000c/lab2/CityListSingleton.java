package com.example.t0u000c.lab2;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.UUID;
import java.util.ArrayList;

/**
 * Created by t0u000c on 10/21/17.
 */

public class CityListSingleton {
    private ArrayList<City> mCities;

    private static CityListSingleton cityListSingleton;
    private Context mContext;

    private static final String TAG = "CitiListSingleton";
    private static final String FILENAME = "cities.json";
    private CityIntentJSONSerializer mSerializer;

    private CityListSingleton(Context context){
        this.mContext = context;
        mSerializer = new CityIntentJSONSerializer(mContext, FILENAME)  ;
        try {
            mCities = mSerializer.loadCities();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            mCities = new ArrayList<City>();
            Log.e(TAG,"Error loading cities: " , e);
        }
        //

    }

    public boolean saveCrimes() {
        try {
            mSerializer.saveCities(mCities);
            Log.d(TAG, "cities saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving cities: ", e);
            return false;
        }
    }


    public static CityListSingleton get(Context c){
        if(cityListSingleton == null){
            cityListSingleton = new CityListSingleton(c.getApplicationContext());
        }
        return cityListSingleton;
    }

    public ArrayList<City> getmCities(){
        return mCities;
    }

    public void addCity(City c) {
        mCities.add(c);
    }

    public void deleteCity(City c){
        mCities.remove(c);
    }

    public City getCity(UUID id){
        for(City c: mCities){
            if(c.getmId().equals(id))
                return c;
        }
        return null;
    }

    public int getCityByName(String name){
        int i = 0;
        for(City c: mCities){
            if(c.getCityName().equals(name))
                return i;
            i++;
        }
        return -1;
    }
}
