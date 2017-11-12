package com.example.t0u000c.lab2;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.ArrayList;

/**
 * Created by t0u000c on 10/21/17.
 *
 * Allow saving and loading cities and C/F settings
 */

public class CityListSingleton {
    private ArrayList<City> mCities;
    private ArrayList<Setting> mSettings;
    private boolean degreeCelcius;
    private static CityListSingleton cityListSingleton;
    private Context mContext;

    private static final String TAG = "CitiListSingleton";
    private static final String FILENAME = "cities.json";
    private static final String SETTING_FILE_NAME = "setting.json";
    private CityIntentJSONSerializer mSerializer;
    private SettingIntentJSONSerializer mSerializer1;

    private CityListSingleton(Context context){
        this.mContext = context;
        this.degreeCelcius = false;
        mSerializer = new CityIntentJSONSerializer(mContext, FILENAME)  ;
        mSerializer1 = new SettingIntentJSONSerializer(mContext, SETTING_FILE_NAME);
        try {
            mCities = mSerializer.loadCities();
            mSettings = mSerializer1.loadSettings();
            if(mSettings.size() > 0 ){
                this.degreeCelcius = mSettings.get(0).isTempType();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            mCities = new ArrayList<City>();
            mSettings = new ArrayList<Setting>();
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

    public boolean saveSettings(){
        try {
            if(mSettings.size() == 0 )
                mSettings.add(new Setting(this.degreeCelcius));
            else
                mSettings.get(0).setTempType(this.degreeCelcius);
            mSerializer1.saveSettings(mSettings);
            Log.d(TAG, "settings saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving settings: ", e);
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

    public boolean isDegreeCelcius() {
        return degreeCelcius;
    }

    public void setDegreeCelcius(boolean degreeCelcius) {
        this.degreeCelcius = degreeCelcius;
    }
}
