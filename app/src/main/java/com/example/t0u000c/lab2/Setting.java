package com.example.t0u000c.lab2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;


/**
 * Created by t0u000c on 11/11/17.
 *
 * To allow setting for choosing C/F degree in Temperatures
 */

public class Setting {

    private boolean tempType;
    private static final String JSON_TEMPTYPE = "tempType";

    public Setting(boolean tempType){
        this.tempType = tempType;
    }


    public boolean isTempType() {
        return tempType;
    }

    public void setTempType(boolean tempType) {
        this.tempType = tempType;
    }

    public Setting(JSONObject json) throws JSONException {
        tempType = json.getBoolean(JSON_TEMPTYPE);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_TEMPTYPE, tempType);
        return json;
    }
}
