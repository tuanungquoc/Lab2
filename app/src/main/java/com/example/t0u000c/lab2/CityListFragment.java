package com.example.t0u000c.lab2;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.*;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by t0u000c on 10/21/17.
 */

public class CityListFragment extends ListFragment {
        private ArrayList<City> mCities;
        private static final String TAG = "CityListFragment";
        private static final int REQUEST_CITY = 1;
        private static final int REQUEST_CITY_DETAIL = 2;
        private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 3;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getActivity().setTitle(R.string.app_name);
            setHasOptionsMenu(true);
            mCities = CityListSingleton.get(getActivity()).getmCities();
            ArrayAdapter<City> adapter = new ArrayAdapter<City>(getActivity(),android.R.layout.simple_list_item_1,mCities);
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
                    Log.i(TAG, "Place: " + cityName);

                    City mCity = CityListSingleton.get(getActivity()).getmCities().get(CityListSingleton.get(getActivity()).getmCities().size()-1);
                    int pos = CityListSingleton.get(getActivity()).getCityByName(cityName);
                    if (pos == -1) {
                        mCity.setCityName(cityName);

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
        @Override
        public void onResume() {
            super.onResume();
            ((ArrayAdapter<City>)getListAdapter()).notifyDataSetChanged();
        }


}
