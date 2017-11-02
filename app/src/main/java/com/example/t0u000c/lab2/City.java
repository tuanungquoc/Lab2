package com.example.t0u000c.lab2;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * Created by t0u000c on 10/21/17.
 */

public class City {

    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private UUID mId;
    private String cityName =null;

    public String getIsoCountry() {
        return isoCountry;
    }

    public void setIsoCountry(String isoCountry) {
        this.isoCountry = isoCountry;
    }

    private String isoCountry ="US";
    private String weather;
    private double temp;
    private double temp_min;
    private double temp_max;

    public  City(){
        mId = UUID.randomUUID();
        cityName = "";
    }

    public City(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        cityName = json.getString(JSON_NAME);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mId.toString());
        json.put(JSON_NAME, cityName);
        return json;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public double getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(double temp_min) {
        this.temp_min = temp_min;
    }

    public double getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(double temp_max) {
        this.temp_max = temp_max;
    }



    public UUID getmId() {
        return mId;
    }


    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String toString(){
        return this.cityName;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

}
