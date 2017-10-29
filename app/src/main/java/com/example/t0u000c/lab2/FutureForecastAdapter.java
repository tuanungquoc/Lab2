package com.example.t0u000c.lab2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by t0u000c on 10/21/17.
 */

public class FutureForecastAdapter extends Adapter<FutureForecastAdapter.ViewHolder> {

    private ArrayList<DayForecast> myDataSet;
    private Context context;

    public FutureForecastAdapter(Context context, ArrayList<DayForecast> myDataSet) {
        this.myDataSet = myDataSet;
        this.context=context;
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
        holder.mMaxTemperature.setText(myDataSet.get(position).getmMaxTemp());
        holder.mMinTemperature.setText(myDataSet.get(position).getmMinTemp());
        Picasso.with(context).load(myDataSet.get(position).getPhotoSrc()).into(holder.mDayTemperaturePhoto);
    }

    @Override
    public int getItemCount() {
        return myDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mDayTemperaturePhoto;
        public TextView mFutureDay;
        public TextView mMaxTemperature;
        public TextView mMinTemperature;

        public ViewHolder(View itemView) {
            super(itemView);
            mDayTemperaturePhoto = (ImageView) itemView.findViewById(R.id.day_temperature_photo);
            mFutureDay = (TextView) itemView.findViewById(R.id.future_day);
            mMaxTemperature = (TextView) itemView.findViewById(R.id.max_temp);
            mMinTemperature = (TextView) itemView.findViewById(R.id.min_temp);
        }
    }
}
