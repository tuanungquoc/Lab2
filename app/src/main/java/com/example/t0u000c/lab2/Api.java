package com.example.t0u000c.lab2;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by VimmiSwami on 10/23/2017.
 */

public class Api {


    public static String key= "4d24bc6be2371dad87666ac843e640ad";
    public static String linkNow= "http://api.openweathermap.org/data/2.5/weather";
    public static String linkForecastHourly= "http://api.openweathermap.org/data/2.5/forecast";
    public static String linkForecastDays= "http://api.openweathermap.org/data/2.5/forecast/daily";
    public static String localTimeZone="http://api.timezonedb.com/v2/get-time-zone?key=MRKZ336VAE4Y&format=json&by=position";
    public static String localUnixTime="http://api.timezonedb.com/v2/convert-time-zone?key=MRKZ336VAE4Y&format=json&from=Antarctica/Troll";
    public static String localToUTCUnixTime="http://api.timezonedb.com/v2/convert-time-zone?key=MRKZ336VAE4Y&format=json&to=Antarctica/Troll";

    public static String requestCityNow(String city){
        StringBuilder sb= new StringBuilder(linkNow);
        sb.append(String.format("?q=%s&APPID=%s&units=metric",city,key));
        Log.d("NOW", sb.toString() );
        return sb.toString();
    }

    public static String requestCity6Days(String city){
        StringBuilder sb= new StringBuilder(linkForecastDays);
        sb.append(String.format("?q=%s&APPID=%s&units=metric&cnt=%s",city,key,"6"));
        Log.d("10days", sb.toString() );
        return sb.toString();
    }

    public static String requestTimeZoneData(String lat, String lng) throws JSONException {
        StringBuilder sb= new StringBuilder(localTimeZone);
        sb.append(String.format("&lat=%s&lng=%s",lat,lng));

        Log.d("local data fetched:", sb.toString() );
        return sb.toString();
        //return sb.toString();
        //JSONObject object = new JSONObject(sb.toString());
        //return object.getString("zoneName");
    }

    public static String convertUTCToLocal(String timezone, String UTC) throws JSONException {
        StringBuilder sb= new StringBuilder(localUnixTime);
        sb.append(String.format("&to=%s&time=%s",timezone,UTC));
        Connection con = new Connection();
        Log.d("UTC to local json:", sb.toString());
        JSONObject object = new JSONObject(con.getUrlData(sb.toString()));
        return utcToDate(Long.parseLong(object.getString("toTimestamp")), timezone);

    }

    public static String convertLocalToUTC(String timezone, String localtime) throws JSONException {
        StringBuilder sb= new StringBuilder(localToUTCUnixTime);
        sb.append(String.format("&from=%s&time=%s",timezone,localtime));
        Log.d("Local to UTC json:", sb.toString());
        return sb.toString();

        //return utcToDate(Double.parseDouble(object.getString("toTimestamp")), "Antarctica/Troll");

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

    public static Date dateWithoutTime(Date date) throws ParseException {
        DateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat dfwotime= new SimpleDateFormat("yyyy-MM-dd");
        String temp = df.format(date);
        return dfwotime.parse(temp);

    }

    public static Date incrementDate (Date date) throws ParseException {
        date = dateWithoutTime(date);
        DateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat dfwotime= new SimpleDateFormat("yyyy-MM-dd");
        String dt = dfwotime.format(date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(sdf.parse(dt));
        c.add(Calendar.DATE, 1);  // number of days to add
        dt = sdf.format(c.getTime());  // dt is now the new date
        Log.d("incremented date", dt);
        return dfwotime.parse(dt);

    }

    public static String utcToDate(double unixTime, String timezone){
        DateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone(timezone));
        Date date = new Date();
        date.setTime((long) unixTime*1000);
        Log.d("time unix utc converted", df.format(date));
        return df.format(date);

    }

    public static long datetoUnix(Date date, String timezone){
        DateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dates= df.format(date);
        df.setTimeZone(TimeZone.getTimeZone(timezone));
        long unixtime = 0;
        try {
            unixtime = df.parse(dates).getTime();
            Log.d("unix time inside method",String.valueOf(unixtime) );
        } catch (ParseException e) {
            Log.e("UNIX conversion failed",String.valueOf(e));
            e.printStackTrace();
        }
        unixtime=unixtime/1000;
        Log.d("date to unix converted", String.valueOf(unixtime));
        return unixtime;

    }

    public static String gettodayDate(String stringDate) throws ParseException {
        DateFormat stringformat=  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = stringformat.parse(stringDate);
        DateFormat df=  new SimpleDateFormat("EEEE MMM d");
        return df.format(date);
    }
    public static String getNoon(int myNumber) {
        int[]numbers={0,3,6,9,12,15,18,21};
        int distance = Math.abs(numbers[0] - myNumber);
        int idx = 0;
        for(int c = 1; c < numbers.length; c++){
            int cdistance = Math.abs(numbers[c] - myNumber);
            if(cdistance < distance){
                idx = c;
                distance = cdistance;
            }
        }
        Log.d("UTC of local Noon", String.valueOf(numbers[idx]));
        return String.valueOf(numbers[idx]);
    }
}

