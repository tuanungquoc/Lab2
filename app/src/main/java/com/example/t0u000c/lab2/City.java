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
    private static final String JSON_LAT = "lat";
    private static final String JSON_LON = "lon";
    private static final String JSON_STATE = "state";
    private static final String JSON_COUNTRY  = "country";
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

    private double lat;
    private double lon;

    private String state;
    private String country;


    public  City(){
        mId = UUID.randomUUID();
        cityName = "";
        lat = lon = 0;
        country = "";
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }


    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }



    public City(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        cityName = json.getString(JSON_NAME);
        lat = json.getDouble(JSON_LAT);
        lon = json.getDouble(JSON_LON);
        state = json.getString(JSON_STATE);
        country = json.getString(JSON_COUNTRY);

    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mId.toString());
        json.put(JSON_NAME, cityName);
        json.put(JSON_LAT,lat);
        json.put(JSON_LON,lon);
        json.put(JSON_STATE,state);
        json.put(JSON_COUNTRY,country);
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
