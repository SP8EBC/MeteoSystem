package cc.pogoda.mobile.pogodacc.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.Serializable;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.activity.handler.StationDetailsActPlotsButtonClickEvent;
import cc.pogoda.mobile.pogodacc.activity.handler.StationDetailsActSummaryButtonClickEvent;
import cc.pogoda.mobile.pogodacc.type.WeatherStation;

public class StationDetailsActivity extends AppCompatActivity {

    WeatherStation station;

    TextView stationName = null;
    TextView stationLocation = null;
    TextView stationLatLon = null;
    TextView stationSponsorUrl = null;

    ImageButton summaryButton = null;
    ImageButton plotsButton = null;

    StationDetailsActSummaryButtonClickEvent summaryClickEvent = null;
    StationDetailsActPlotsButtonClickEvent plotsClickEvent = null;

    public StationDetailsActivity() {
        stationName = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        float station_lat = 0.0f;   // szerokość    W - E
        float station_lon = 0.0f;   // długość      S - N

        StringBuilder sb = new StringBuilder();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_station_details);

        station = (WeatherStation) getIntent().getSerializableExtra("station");

        stationName = findViewById(R.id.textViewStationName);
        stationLocation = findViewById(R.id.textViewLocalization);
        stationLatLon = findViewById(R.id.textViewLatLon);
        stationSponsorUrl = findViewById(R.id.textViewSponsorUrl);

        if (station != null && stationName != null) {

            summaryClickEvent = new StationDetailsActSummaryButtonClickEvent(station, this);
            plotsClickEvent = new StationDetailsActPlotsButtonClickEvent(station, this);

            summaryButton = findViewById(R.id.imageButtonCurrent);
            summaryButton.setOnClickListener(summaryClickEvent);

            plotsButton = findViewById(R.id.imageButtonPlots);
            plotsButton.setOnClickListener(plotsClickEvent);

            stationName.setText(station.getDisplayedName());
            stationLocation.setText(station.getDisplayedLocation());

            station_lat = station.getLat();
            station_lon = station.getLon();

            stationSponsorUrl.setText(station.getSponsorUrl());

            if (station_lat > 0.0f && station_lon > 0.0f) {
                // europe
                sb.append(station_lon);
                sb.append(" N / ");
                sb.append(station_lat);
                sb.append(" E");

                stationLatLon.setText(sb.toString());
            } else if (station_lat < 0.0f && station_lon > 0.0f) {
                // usa
                sb.append(station_lon);
                sb.append(" N / ");
                sb.append(-station_lat);
                sb.append(" W");

                stationLatLon.setText(sb.toString());
            } else if (station_lat < 0.0f && station_lon < 0.0f) {
                // brazil
                sb.append(-station_lon);
                sb.append(" S / ");
                sb.append(-station_lat);
                sb.append(" W");

                stationLatLon.setText(sb.toString());
            } else if (station_lat > 0.0f && station_lat > 0.0f) {
                // australia
                sb.append(-station_lon);
                sb.append(" S / ");
                sb.append(station_lat);
                sb.append(" E");

                stationLatLon.setText(sb.toString());
            }


        }
    }
}