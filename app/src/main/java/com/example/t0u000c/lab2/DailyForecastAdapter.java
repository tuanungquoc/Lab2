package com.example.t0u000c.lab2;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by t0u000c on 10/21/17.
 */

public class DailyForecastAdapter<T> extends Adapter<DailyForecastAdapter.ViewHolder> {

    private ArrayList<String> myDataSet;

    public DailyForecastAdapter(ArrayList<String> myDataSet) {
        this.myDataSet = myDataSet;
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
        holder.mTitle.setText(myDataSet.get(position));

    }

    @Override
    public int getItemCount() {
        return myDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitle;
        public ViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
