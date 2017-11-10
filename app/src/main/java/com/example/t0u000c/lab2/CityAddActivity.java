package com.example.t0u000c.lab2;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.places.Places;


import java.util.UUID;

/**
 * Created by t0u000c on 10/21/17.
 */

public class CityAddActivity extends SingleFragmentActivity implements GoogleApiClient.OnConnectionFailedListener  {

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected android.support.v4.app.Fragment createFragment() {
        UUID cityId = (UUID) getIntent().getSerializableExtra(CityAddFragment.EXTRA_CITY_ID);
        return CityAddFragment.newInstance(cityId);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
