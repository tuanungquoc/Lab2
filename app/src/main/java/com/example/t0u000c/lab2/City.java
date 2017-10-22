package com.example.t0u000c.lab2;

import java.util.UUID;

/**
 * Created by t0u000c on 10/21/17.
 */

public class City {

    private UUID mId;
    private String cityName;

    public  City(){
        mId = UUID.randomUUID();
        cityName = "";
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
}
