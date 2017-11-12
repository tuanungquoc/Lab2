package com.example.t0u000c.lab2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by t0u000c on 10/21/17.
 *
 * Adapter to hold a city data for 24 hours and allow horizontal scrolling
 */

public class DailyForecastAdapter<T> extends Adapter<DailyForecastAdapter.ViewHolder> {

    private ArrayList<String> myDataSet;
    private Context context;

    public DailyForecastAdapter(Context context,ArrayList<String> myDataSet) {
        this.myDataSet = myDataSet;
        this.context=context;
    }

    @Override
    public DailyForecastAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dailyforecast_fragment, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(DailyForecastAdapter.ViewHolder holder, int position) {
        String[] temp = myDataSet.get(position).split("\n");
        Log.d("SPLITING", String.valueOf(temp[1]));
        holder.mTitle.setText(temp[0]);
        holder.mTemp.setText(temp[2] +  "" + (char) 0x00B0);
        Picasso.with(context).load(temp[1]).into(holder.mDayTemperaturePhoto);

    }

    @Override
    public int getItemCount() {
        return myDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitle;
        public ImageView mDayTemperaturePhoto;
        public TextView mTemp;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mDayTemperaturePhoto = (ImageView) itemView.findViewById(R.id.day_temperature_photo);
            mTemp = (TextView) itemView.findViewById(R.id.temp);
        }
    }
}
