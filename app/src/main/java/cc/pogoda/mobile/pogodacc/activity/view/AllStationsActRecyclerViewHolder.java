package cc.pogoda.mobile.pogodacc.activity.view;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.activity.handler.AllStationsActRecyclerViewButtonClickEvent;

public class AllStationsActRecyclerViewHolder extends RecyclerView.ViewHolder {

    public TextView textView;
    public Button button;

    public AllStationsActRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        textView = itemView.findViewById(R.id.station_name);
        button = itemView.findViewById(R.id.station_button);

    }
}
