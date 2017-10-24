package com.example.t0u000c.lab2;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by VimmiSwami on 10/23/2017.
 */

public class Api {


    public static String key= "4d24bc6be2371dad87666ac843e640ad";
    public static String linkNow= "http://api.openweathermap.org/data/2.5/weather";
    public static String linkForecastHourly= "http://api.openweathermap.org/data/2.5/forecast";
    public static String linkForecastDays= "http://api.openweathermap.org/data/2.5/forecast/daily";

    public static String requestCityNow(String city){
        StringBuilder sb= new StringBuilder(linkNow);
        sb.append(String.format("?q=%s&APPID=%s&units=metric",city,key));
        Log.d("NOW", sb.toString() );
        return sb.toString();
    }

    public static String requestCity10Days(String city){
        StringBuilder sb= new StringBuilder(linkForecastDays);
        sb.append(String.format("?q=%s&APPID=%s&units=metric&cnt=%s",city,key,"11"));
        Log.d("10days", sb.toString() );
        return sb.toString();
    }

    public static String requestCity24Hrs(String city){
        StringBuilder sb= new StringBuilder(linkForecastHourly);
        sb.append(String.format("?q=%s&APPID=%s&units=metric",city,key));
        Log.d("24HRS", sb.toString() );
        return sb.toString();
    }

    public static String timeConversion(double unixTime){
        DateFormat df= new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setTime((long) unixTime*1000);
        return df.format(date);

    }

    public static String gettodayDate(){
        DateFormat df=  new SimpleDateFormat("EEEE MMM d");
        Date date = new Date();
        return df.format(date);
    }
}
