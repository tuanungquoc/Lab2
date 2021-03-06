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

import static java.lang.Math.round;

/**
 * Created by Vimmi Swami on 10/23/2017.
 *
 * This Class define various methods to build URL for APIs and other time conversions related methods.
 */

public class Api {


    public static String key= "ac0b419a5c23e4909e174e473bc4a03c";
    public static String linkNow= "http://api.openweathermap.org/data/2.5/weather";
    public static String linkForecastHourly= "http://api.openweathermap.org/data/2.5/forecast";
    public static String linkForecastDays= "http://api.openweathermap.org/data/2.5/forecast/daily";
    public static String localTimeZone="http://api.timezonedb.com/v2/get-time-zone?key=MRKZ336VAE4Y&format=json&by=position";
    public static String localUnixTime="http://api.timezonedb.com/v2/convert-time-zone?key=MRKZ336VAE4Y&format=json&from=Antarctica/Troll";
    public static String localToUTCUnixTime="http://api.timezonedb.com/v2/convert-time-zone?key=MRKZ336VAE4Y&format=json&to=Antarctica/Troll";

    // To build URL for API to fetch current weather data for a place
    public static String requestCityNow(String city){
        StringBuilder sb= new StringBuilder(linkNow);
        sb.append(String.format("?q=%s&APPID=%s&units=metric",city,key));
        Log.d("NOW", sb.toString() );
        return sb.toString();
    }

    // To build URL for API to fetch 6days weather data for a given place
    public static String requestCity6Days(String city){
        StringBuilder sb= new StringBuilder(linkForecastDays);
        sb.append(String.format("?q=%s&APPID=%s&units=metric&cnt=%s",city,key,"6"));
        Log.d("10days", sb.toString() );
        return sb.toString();
    }

    // To fetch local time, timezone and iso country code for a given coordinates
    public static String requestTimeZoneData(String lat, String lng) throws JSONException {
        StringBuilder sb= new StringBuilder(localTimeZone);
        sb.append(String.format("&lat=%s&lng=%s",lat,lng));

        Log.d("local data fetched:", sb.toString() );
        return sb.toString();
    }

    // To build URL for timezoneDb API to fetch local time of a timezone from UTC time
    public static String convertUTCToLocal(String timezone, String UTC) throws JSONException {
        StringBuilder sb= new StringBuilder(localUnixTime);
        sb.append(String.format("&to=%s&time=%s",timezone,UTC));
        Connection con = new Connection();
        JSONObject object = new JSONObject(con.getUrlData(sb.toString()));
        return utcToDate(Long.parseLong(object.getString("toTimestamp")), timezone);

    }

    // To build URL for timezoneDb API to fetch UTC unix time from given time and timezone
    public static String convertLocalToUTC(String timezone, String localtime) throws JSONException {
        StringBuilder sb= new StringBuilder(localToUTCUnixTime);
        sb.append(String.format("&from=%s&time=%s",timezone,localtime));
        Log.d("Local to UTC json:", sb.toString());
        return sb.toString();

    }

    // To fetch URL for 5days3Hrs API based on provided place
    public static String requestCity24Hrs(String city){
        StringBuilder sb= new StringBuilder(linkForecastHourly);
        sb.append(String.format("?q=%s&APPID=%s&units=metric",city,key));
        Log.d("24HRS", sb.toString() );
        return sb.toString();
    }

    //To convert unix time to Hour and minute format
    public static String timeConversion(double unixTime){
        DateFormat df= new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setTime((long) unixTime*1000);
        return df.format(date);

    }

    // To fetch only date from date and time
    public static Date dateWithoutTime(Date date) throws ParseException {
        DateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat dfwotime= new SimpleDateFormat("yyyy-MM-dd");
        String temp = df.format(date);
        return dfwotime.parse(temp);

    }

    // To increment day in a date
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

    // To convert given unix time to given timezone date
    public static String utcToDate(double unixTime, String timezone){
        DateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone(timezone));
        Date date = new Date();
        date.setTime((long) unixTime*1000);
        Log.d("time unix utc converted", df.format(date));
        return df.format(date);

    }

    // To convert given timezone data to unix time
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

    // To fetch day, month and date from given time
    public static String gettodayDate(String stringDate) throws ParseException {
        DateFormat stringformat=  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = stringformat.parse(stringDate);
        DateFormat df=  new SimpleDateFormat("EEEE MMM d");
        return df.format(date);
    }

    // To fetch Hour and minutes from given time
    public static String getCurrentTime(String stringDate) throws ParseException {
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outFormat = new SimpleDateFormat("HH:mm");
        String time24 = outFormat.format(inFormat.parse(stringDate));
        return time24;
    }

    // To fetch current local time for a given timezone
    public static String getCurrentTime1(String timeZone) throws ParseException {

        Date date = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        sdf.setTimeZone(tz);

        String sDateInAmerica = sdf.format(date); // Convert to String first
        SimpleDateFormat inFormat = new SimpleDateFormat("dd-m-yyyy hh:mm:ss a");
        SimpleDateFormat outFormat = new SimpleDateFormat("hh:mm a");
        String time24 = outFormat.format(inFormat.parse(sDateInAmerica));
        return time24;
    }

    // To get closest match to UTC Hour number with respect to given number
    public static String getMatch(float myNumber) {
        int[]numbers={0,3,6,9,12,15,18,21,24};
        float distance = Math.abs(numbers[0] - myNumber);
        int idx = 0;
        for(int c = 1; c < numbers.length; c++){
            float cdistance = Math.abs(numbers[c] - myNumber);
            if(cdistance <= distance){
                idx = c;
                distance = cdistance;
            }
        }
        Log.d("UTC MATCH", String.valueOf(numbers[idx]));
        if (idx == 8){
            return String.valueOf(0);
        }else{
            return String.valueOf(numbers[idx]);
        }

    }

    // To convert C to F
    public static double getFarenheit(double celcius){
        return round((9.0/5.0)*celcius + 32);
    }

    // To convert F to C
    public static double getCelcius(double farenheit){
        return (5.0/9.0)*(farenheit - 32);
    }
}

