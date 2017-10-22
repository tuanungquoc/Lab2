package com.example.t0u000c.lab2;

import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.*;
/**
 * Created by t0u000c on 10/21/17.
 */

public class CityListFragment extends ListFragment {
        private ArrayList<City> mCities;
        private static final String TAG = "CityListFragment";
        private static final int REQUEST_CITY = 1;
        private static final int REQUEST_CITY_DETAIL = 2;
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
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_item_new_city:
                    City c = new City();
                    CityListSingleton.get(getActivity()).addCity(c);
                    Intent i = new Intent(getActivity(), CityAddActivity.class);
                    i.putExtra(CityAddFragment.EXTRA_CITY_ID, c.getmId());
                    startActivityForResult(i, REQUEST_CITY);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            ((ArrayAdapter<City>)getListAdapter()).notifyDataSetChanged();
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent intend){
            if (requestCode == REQUEST_CITY) {
            // Handle result
        }
}

}
