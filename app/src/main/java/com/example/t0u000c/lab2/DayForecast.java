package com.example.t0u000c.lab2;

/**
 * Created by t0u000c on 10/21/17.
 */

public class DayForecast {
    private String mDay;
    private String mMaxTemp;
    private String mMinTemp;
    private String mPhotoSrc;

    public DayForecast(String mDay, String mMaxTemp, String mMinTemp,String mPhotoSrc) {
        this.mDay = mDay;
        this.mMaxTemp = mMaxTemp;
        this.mMinTemp = mMinTemp;
        this.mPhotoSrc = mPhotoSrc;
    }

    public String getPhotoSrc() {
        return "http://openweathermap.org/img/w/"+mPhotoSrc+".png";
    }

    public void setPhotoSrc(String mPhotoSrc) {
        this.mPhotoSrc = mPhotoSrc;
    }

    public String getDay() {

        return mDay;
    }

    public void setDay(String mDay) {
        this.mDay = mDay;
    }

    public String getmMaxTemp() {
        return mMaxTemp;
    }

    public void setmMaxTemp(String mMaxTemp) {
        this.mMaxTemp = mMaxTemp;
    }

    public String getmMinTemp() {
        return mMinTemp;
    }

    public void setmMinTemp(String mMinTemp) {
        this.mMinTemp = mMinTemp;
    }

}
