package com.example.t0u000c.lab2;

import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.*;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by t0u000c on 10/21/17.
 * Enables adding of city using Google API
 * Allows listing/deletion of cities, load/refresh current weather and time data
 */

public class CityListFragment extends ListFragment {
        private ArrayList<City> mCities;
        private static final String TAG = "CityListFragment";
        private static final int REQUEST_CITY = 1;
        private static final int REQUEST_SETTING = 4;
        private static final int REQUEST_CITY_DETAIL = 2;
        private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 3;

        private class CityAdapter extends ArrayAdapter<City> {
            public CityAdapter(ArrayList<City> cities) {
                super(getActivity(), 0, cities);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent){

                if (convertView == null) {
                    convertView = getActivity().getLayoutInflater()
                            .inflate(R.layout.list_item_city, null);
                }
                // Configure the view for this Crime
                City c = getItem(position);
                TextView cityNameTextView =
                        (TextView)convertView.findViewById(R.id.city_name_list_item);
                cityNameTextView.setText(c.getCityName());

                TextView cityTempTextView =
                        (TextView)convertView.findViewById(R.id.city_list_item_temp);
                if(!CityListSingleton.get(getActivity()).isDegreeCelcius())
                    cityTempTextView.setText(c.getTemp() +"" +  (char) 0x00B0);
                else
                    cityTempTextView.setText(Api.getFarenheit(c.getTemp())+"" +  (char) 0x00B0);

                TextView timeTextView =
                        (TextView) convertView.findViewById(R.id.city_time_list_item);
                timeTextView.setText(c.getCurrentTime());
                return convertView;
            }
        }

        @Override
        public void onResume() {

            super.onResume();
            mCities = CityListSingleton.get(getActivity()).getmCities();
            //calling city weather to update
            for(int i = 0 ; i < mCities.size(); i++){
                final City mCity = mCities.get(i);
                executeTempRequest(mCity);
                executeTimeRequest(mCity);

            }
            ((ArrayAdapter<City>)getListAdapter()).notifyDataSetChanged();

        }

        public void executeTimeRequest(final City mCity){
            try {
                mCity.setCurrentTime(Api.getCurrentTime1(mCity.getZoneTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        public void executeTempRequest(final City mCity){
            if(!mCity.getIsoCountry().equals("")) {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                        Api.requestCityNow(mCity.getCityName() + "," + mCity.getIsoCountry()), new JSONObject(),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject object = new JSONObject(response.toString());

                                    JSONArray weatherArray = object.getJSONArray("weather");
                                    JSONObject weatherObject = weatherArray.getJSONObject(0);
                                    Log.d("SetValues", weatherObject.getString("main"));
                                    mCity.setWeather(weatherObject.getString("main"));
                                    JSONObject mainObject = object.getJSONObject("main");
                                    String min = mainObject.getString("temp_min");
                                    Log.d("SetValues", min);
                                    mCity.setTemp_min(Double.valueOf(min));
                                    String max = mainObject.getString("temp_max");
                                    Log.d("SetValues", max);
                                    mCity.setTemp_max(Double.valueOf(max));
                                    String tempp = mainObject.getString("temp");
                                    Log.d("SetValues", tempp);
                                    mCity.setTemp(Double.valueOf(tempp));
                                    mCity.setIcon(weatherObject.getString("icon"));
                                    ((ArrayAdapter<City>) getListAdapter()).notifyDataSetChanged();
                                } catch (Exception ex) {
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getActivity(), "there is an err api in this city", Toast.LENGTH_LONG).show();
                            }
                        });
                NetworkSingleton.get(getActivity()).addRequest(jsonObjectRequest, "City View Header Current Date");
            }

        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getActivity().setTitle(R.string.app_name);
            setHasOptionsMenu(true);
            mCities = CityListSingleton.get(getActivity()).getmCities();

            //calling city weather to update
            for(int i = 0 ; i < mCities.size(); i++){
                final City mCity = mCities.get(i);
                executeTempRequest(mCity);
                executeTimeRequest(mCity);

            }
           // ArrayAdapter<City> adapter = new ArrayAdapter<City>(getActivity(),android.R.layout.simple_list_item_1,mCities);
            CityAdapter adapter = new CityAdapter(mCities);
            setListAdapter(adapter);

        }



        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            City c = (City)(getListAdapter()).getItem(position);
            Log.d(TAG,c.getCityName() + " is clicked");
            Intent i = new Intent(getActivity(), CityDetailPagerActivity.class);
            i.putExtra(CityAddFragment.EXTRA_CITY_ID, c.getmId());

            startActivityForResult(i, REQUEST_CITY_DETAIL);

        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.fragment_city_list, menu);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu,View v, ContextMenu.ContextMenuInfo menuInfo){
            getActivity().getMenuInflater().inflate(R.menu.city_list_item_context,menu);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
            View v = super.onCreateView(inflater, parent, savedInstanceState);
            ListView listView = (ListView) v.findViewById(android.R.id.list);
            registerForContextMenu(listView);
            return v;
        }

        @Override
        public boolean onContextItemSelected(MenuItem item) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
            int position = info.position;
            ArrayAdapter<City> adapter = (ArrayAdapter<City>) getListAdapter();
            City citySelected = adapter.getItem(position);
            switch (item.getItemId()) {
                case R.id.menu_item_delete_city:
                    CityListSingleton.get(getActivity()).deleteCity(citySelected);
                    adapter.notifyDataSetChanged();
                    return true;
            }
            return super.onContextItemSelected(item);
        }

        @Override
        public void onPause() {
            super.onPause();
            CityListSingleton.get(getActivity()).saveCrimes();
            CityListSingleton.get(getActivity()).saveSettings();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_item_new_city:
                    City c = new City();
                    CityListSingleton.get(getActivity()).addCity(c);
//                    Intent i = new Intent(getActivity(), CityAddActivity.class);
//

//                    startActivityForResult(i, REQUEST_CITY);

                    try {
                        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                                .build();
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                        .setFilter(typeFilter)
                                        .build(getActivity());
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                    return true;
                case R.id.menu_item_setting:
                    Intent i = new Intent(getActivity(), SettingActivity.class);
                    startActivityForResult(i, REQUEST_SETTING);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                    String cityName = place.getName().toString();
                    double lat = place.getLatLng().latitude;
                    double lon = place.getLatLng().longitude;
                    String[] address = (place.getAddress()+"").split(",");
                    //Log.d("In autocomple", address[0]+","+address[1]+","+address[2]);
                    String country = "";
                    String state = "";
                    if(address.length == 2){
                        country = address[1].trim();
                    }else if(address.length == 3){
                        state = address[1].trim();
                        country = address[2].trim();
                    }
                    Log.i(TAG, "Place: " + cityName);

                    final City mCity = CityListSingleton.get(getActivity()).getmCities().get(CityListSingleton.get(getActivity()).getmCities().size()-1);
                    int pos = CityListSingleton.get(getActivity()).getCityByName(cityName);
                    if (pos == -1) {
                        mCity.setCityName(cityName);
                        mCity.setLat(lat);
                        mCity.setLon(lon);
                        mCity.setCountry(country);
                        //mCity.setIsoCountry(country);
                        mCity.setState(state);
                        try {
                            JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET,
                                    Api.requestTimeZoneData(mCity.getLat()+"",mCity.getLon()+""), new JSONObject(),
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            //Get and set local date of the city chosen.
                                            final JSONObject localzonedata;
                                            try {
                                                localzonedata = new JSONObject(response.toString());
                                                mCity.setZoneTime(localzonedata.getString("zoneName"));
                                                mCity.setIsoCountry(localzonedata.getString("countryCode"));
                                                mCity.setCurrentTime(Api.getCurrentTime1(mCity.getZoneTime()));
                                                Log.d("TEST", "");
                                                String temp = mCity.getCityName() + "," + mCity.getIsoCountry();
                                                ((ArrayAdapter<City>)getListAdapter()).notifyDataSetChanged();
                                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                                                        Api.requestCityNow(temp), new JSONObject(),
                                                        new Response.Listener<JSONObject>()
                                                        {
                                                            @Override
                                                            public void onResponse(JSONObject response)
                                                            {
                                                                try {
                                                                    String test = response.toString();
                                                                    JSONObject object = new JSONObject(response.toString());

                                                                    JSONArray weatherArray = object.getJSONArray("weather");
                                                                    JSONObject weatherObject = weatherArray.getJSONObject(0);
                                                                    Log.d("SetValues", weatherObject.getString("main"));
                                                                    mCity.setWeather(weatherObject.getString("main"));
                                                                    JSONObject mainObject = object.getJSONObject("main");
                                                                    String min = mainObject.getString("temp_min");
                                                                    Log.d("SetValues", min);
                                                                    mCity.setTemp_min(Double.valueOf(min));
                                                                    String max = mainObject.getString("temp_max");
                                                                    Log.d("SetValues", max);
                                                                    mCity.setTemp_max(Double.valueOf(max));
                                                                    String tempp = mainObject.getString("temp");
                                                                    Log.d("SetValues", tempp);
                                                                    mCity.setTemp(Double.valueOf(tempp));
                                                                    mCity.setIcon(weatherObject.getString("icon"));
                                                                    ((ArrayAdapter<City>)getListAdapter()).notifyDataSetChanged();
                                                                }catch(Exception ex){}
                                                            }
                                                        },
                                                        new Response.ErrorListener()
                                                        {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error)
                                                            {
                                                                Toast.makeText(getActivity(), "The weather API throws error for"+mCity.getCityName()+". Removing", Toast.LENGTH_LONG).show();
                                                                CityListSingleton.get(getActivity()).deleteCity(mCity);
                                                                ((ArrayAdapter<City>)getListAdapter()).notifyDataSetChanged();
                                                            }
                                                        });
                                                NetworkSingleton.get(getActivity()).addRequest(jsonObjectRequest,"City View Header Current Date");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener()
                                    {
                                        @Override
                                        public void onErrorResponse(VolleyError error)
                                        {
                                            // Deal with the error here
                                            Toast.makeText(getActivity(), "there is something wrong with the timezone", Toast.LENGTH_LONG).show();
                                        }
                                    });

                            NetworkSingleton.get(getActivity()).addRequest(jsonObjectRequest1,"Getting local time");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        CityListSingleton.get(getActivity()).getmCities().remove(CityListSingleton.get(getActivity()).getmCities().size() - 1);
                        Toast.makeText(getActivity(), cityName + " already added", Toast.LENGTH_LONG).show();
                    }

                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                    // TODO: Handle the error.
                    Log.i(TAG, status.getStatusMessage());
                    CityListSingleton.get(getActivity()).getmCities().remove(CityListSingleton.get(getActivity()).getmCities().size()-1);

                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                    CityListSingleton.get(getActivity()).getmCities().remove(CityListSingleton.get(getActivity()).getmCities().size()-1);
                }
                ((ArrayAdapter<City>)getListAdapter()).notifyDataSetChanged();
            }
        }
       


}
