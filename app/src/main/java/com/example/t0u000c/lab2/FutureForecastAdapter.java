package com.example.t0u000c.lab2;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by t0u000c on 10/21/17.
 */

public class FutureForecastAdapter extends Adapter<FutureForecastAdapter.ViewHolder> {

    private ArrayList<DayForecast> myDataSet;

    public FutureForecastAdapter(ArrayList<DayForecast> myDataSet) {
        this.myDataSet = myDataSet;
    }

    @Override
    public FutureForecastAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.futureforecast_fragment, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(FutureForecastAdapter.ViewHolder holder, int position) {
        holder.mFutureDay.setText(myDataSet.get(position).getDay());
        holder.mDayTemperature.setText(myDataSet.get(position).getAveTemp());
        holder.mDayTemperaturePhoto.setBackgroundResource(R.drawable.tempphoto);
    }

    @Override
    public int getItemCount() {
        return myDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mDayTemperaturePhoto;
        public TextView mFutureDay;
        public TextView mDayTemperature;

        public ViewHolder(View itemView) {
            super(itemView);
            mDayTemperaturePhoto = (ImageView) itemView.findViewById(R.id.day_temperature_photo);
            mFutureDay = (TextView) itemView.findViewById(R.id.future_day);
            mDayTemperature = (TextView) itemView.findViewById(R.id.day_temperature);
        }
    }
}
