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
import cc.pogoda.mobile.pogodacc.dao.SummaryDao;
import cc.pogoda.mobile.pogodacc.type.ParceableFavsCallReason;
import cc.pogoda.mobile.pogodacc.type.WeatherStation;
import cc.pogoda.mobile.pogodacc.type.web.QualityFactor;
import cc.pogoda.mobile.pogodacc.type.web.Summary;

public class WeatherStationRecyclerViewAdapter extends RecyclerView.Adapter<AllStationsActRecyclerViewHolder> {

    private List<WeatherStation> stations;

    AppCompatActivity activity;

    ParceableFavsCallReason.Reason reason;

    SummaryDao summaryDao;

    public WeatherStationRecyclerViewAdapter(List<WeatherStation> stations, AppCompatActivity parentActivity, ParceableFavsCallReason.Reason callReason) {
        this.stations = stations;
        this.activity = parentActivity;
        this.reason = callReason;
        this.summaryDao = new SummaryDao();
    }

    @NonNull
    @Override
    public AllStationsActRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view;

        // check the call reason
        if (reason.equals(ParceableFavsCallReason.Reason.FAVOURITES)) {
            // inflate custom layout
            view = inflater.inflate(R.layout.activity_favourites_linear_layout_data, parent, false);
        }
        else {
            // Inflate the custom layout without current data
            view = inflater.inflate(R.layout.activity_all_stations_linear_layout, parent, false);
        }

        // Return a new holder instance
        AllStationsActRecyclerViewHolder viewHolder = new AllStationsActRecyclerViewHolder(view, reason);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AllStationsActRecyclerViewHolder holder, int position) {
        TextView textView = holder.textView;
        TextView textViewData = holder.textViewData;
        Button button = holder.button;

        WeatherStation station = stations.get(position);

        if (station != null) {
            textView.setText(station.getDisplayedName());
            button.setText(R.string.select_station);

            button.setOnClickListener(new AllStationsActRecyclerViewButtonClickEvent(station, activity, reason));
        }

        if (textViewData != null) {
            Summary summary = summaryDao.getStationSummary(station.getSystemName());

            if (summary != null) {
                String str;
                if (summary.wind_qf_native.equals(QualityFactor.FULL) || summary.wind_qf_native.equals(QualityFactor.DEGRADED)) {
                    if (summary.humidity_qf_native.equals(QualityFactor.FULL) || summary.humidity_qf_native.equals(QualityFactor.DEGRADED)) {
                        str = String.format("%d°C %d%% %s %3.1f m/s max %3.1f m/s", Math.round(summary.avg_temperature), summary.humidity, summary.getWindDirStr(), summary.average_speed, summary.gusts);
                    }
                    else {
                        str = String.format("%d°C %s %3.1f m/s max %3.1f m/s", Math.round(summary.avg_temperature), summary.getWindDirStr(), summary.average_speed, summary.gusts);
                    }
                }
                else {
                    if (summary.humidity_qf_native.equals(QualityFactor.FULL) || summary.humidity_qf_native.equals(QualityFactor.DEGRADED)) {
                        str = String.format("%d°C %d%%", Math.round(summary.avg_temperature), summary.humidity);
                    }
                    else {
                        str = String.format("%d°C", Math.round(summary.avg_temperature));

                    }
                }

                textViewData.setText(str);
            }
        }

    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

}
