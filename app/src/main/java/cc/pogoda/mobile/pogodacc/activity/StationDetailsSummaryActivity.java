package cc.pogoda.mobile.pogodacc.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.activity.updater.StationDetailsValuesUpdater;
import cc.pogoda.mobile.pogodacc.dao.SummaryDao;
import cc.pogoda.mobile.pogodacc.type.StationSummaryActElements;
import cc.pogoda.mobile.pogodacc.type.WeatherStation;
import cc.pogoda.mobile.pogodacc.type.web.Summary;

public class StationDetailsSummaryActivity extends AppCompatActivity {

    StationSummaryActElements elems = null;

    WeatherStation station = null;

    StationDetailsValuesUpdater updater = null;

    Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        elems = new StationSummaryActElements();

        Summary summary = null;
        SummaryDao summary_dao = new SummaryDao();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_details_summary);

        station = (WeatherStation) getIntent().getSerializableExtra("station");

        elems.title = findViewById(R.id.textViewStationDetailsSummaryTitle);
        elems.title.setText(station.getDisplayedName());

        elems.wind_dir_val = findViewById(R.id.textViewWinddirValue);
        elems.wind_gusts_val = findViewById(R.id.textViewWindGustsValue);
        elems.wind_speed_val = findViewById(R.id.textViewWindSpeedValue);
        elems.temperature_val = findViewById(R.id.textViewTemperatureValue);
        elems.qnh_val = findViewById(R.id.textViewQnhVaue);

        summary = summary_dao.getStationSummary(station.getSystemName());

        elems.updateFromSummary(summary);

        handler = new Handler();
        updater = new StationDetailsValuesUpdater(elems, handler, station.getSystemName());

        if (handler != null && updater != null) {
            handler.post(updater);
        }


    }

    @Override
    protected void onStop() {

        if (handler != null && updater != null) {
            handler.removeCallbacks(updater);
        }

        super.onStop();
    }
}