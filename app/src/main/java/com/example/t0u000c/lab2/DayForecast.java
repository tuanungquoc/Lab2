package com.example.t0u000c.lab2;

/**
 * Created by t0u000c on 10/21/17.
 */

public class DayForecast {
    private String mDay;
    private String mAveTemp;
    private String mPhotoSrc;

    public DayForecast(String mDay, String mAveTemp, String mPhotoSrc) {
        this.mDay = mDay;
        this.mAveTemp = mAveTemp;
        this.mPhotoSrc = mPhotoSrc;
    }

    public String getPhotoSrc() {
        return mPhotoSrc;
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

    public String getAveTemp() {
        return mAveTemp;
    }

    public void setAveTemp(String mAveTemp) {
        this.mAveTemp = mAveTemp;
    }
}
