package cc.pogoda.mobile.pogodacc.activity.view;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.activity.handler.AllStationsActRecyclerViewButtonClickEvent;
import cc.pogoda.mobile.pogodacc.type.ParceableFavsCallReason;

public class AllStationsActRecyclerViewHolder extends RecyclerView.ViewHolder {

    public TextView textView;
    public Button button;
    public TextView textViewData;

    public AllStationsActRecyclerViewHolder(@NonNull View itemView, ParceableFavsCallReason.Reason callReason) {
        super(itemView);

        if (callReason.equals(ParceableFavsCallReason.Reason.EXPORT_SELECT) || callReason.equals(ParceableFavsCallReason.Reason.ALL_STATIONS)) {
            textView = itemView.findViewById(R.id.station_name);
            button = itemView.findViewById(R.id.station_button);
            textViewData = null;
        }
        else {
            textView = itemView.findViewById(R.id.station_name_fav);
            button = itemView.findViewById(R.id.station_button_fav);
            textViewData = itemView.findViewById(R.id.station_data_fav);
        }



    }
}
