package com.example.t0u000c.lab2;

import android.content.Context;
import java.util.UUID;
import java.util.ArrayList;

/**
 * Created by t0u000c on 10/21/17.
 */

public class CityListSingleton {
    private ArrayList<City> mCities;

    private static CityListSingleton cityListSingleton;
    private Context mContext;

    private CityListSingleton(Context context){
        this.mContext = context;
        mCities = new ArrayList<City>();

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
