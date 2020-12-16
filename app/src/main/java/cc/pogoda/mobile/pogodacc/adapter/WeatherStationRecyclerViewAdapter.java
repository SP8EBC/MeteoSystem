package cc.pogoda.mobile.pogodacc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.activity.handler.AllStationsActRecyclerViewButtonClickEvent;
import cc.pogoda.mobile.pogodacc.activity.view.AllStationsActRecyclerViewHolder;
import cc.pogoda.mobile.pogodacc.type.WeatherStation;

public class WeatherStationRecyclerViewAdapter extends RecyclerView.Adapter<AllStationsActRecyclerViewHolder> {

    private List<WeatherStation> stations;

    AppCompatActivity activity;

    public WeatherStationRecyclerViewAdapter(List<WeatherStation> stations, AppCompatActivity parentActivity) {
        this.stations = stations;
        this.activity = parentActivity;
    }

    @NonNull
    @Override
    public AllStationsActRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.activity_all_stations_linear_layout, parent, false);

        // Return a new holder instance
        AllStationsActRecyclerViewHolder viewHolder = new AllStationsActRecyclerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AllStationsActRecyclerViewHolder holder, int position) {
        TextView textView = holder.textView;
        Button button = holder.button;

        WeatherStation station = stations.get(position);

        if (station != null) {
            textView.setText(station.getDisplayedName());
            button.setText(R.string.select_station);

            button.setOnClickListener(new AllStationsActRecyclerViewButtonClickEvent(station, activity));
        }

    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

}
