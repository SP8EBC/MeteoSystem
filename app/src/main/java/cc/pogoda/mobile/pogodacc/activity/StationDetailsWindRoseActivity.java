package cc.pogoda.mobile.pogodacc.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.activity.updater.StationDetailsValuesUpdater;
import cc.pogoda.mobile.pogodacc.dao.SummaryDao;
import cc.pogoda.mobile.pogodacc.type.StationWindRoseActElements;
import cc.pogoda.mobile.pogodacc.type.WeatherStation;
import cc.pogoda.mobile.pogodacc.type.web.Summary;

public class StationDetailsWindRoseActivity extends AppCompatActivity {

    WeatherStation station;

    Summary summary;

    StationDetailsValuesUpdater updater = null;

    Handler handler = null;

    StationWindRoseActElements elements;

    public StationDetailsWindRoseActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_details_wind_rose);

        station = (WeatherStation) getIntent().getSerializableExtra("station");

        elements = new StationWindRoseActElements();
        elements.windArrow = findViewById(R.id.imageViewWindRoseArrow);
        elements.windSpeed = findViewById(R.id.textViewWindRoseWindSpeed);
        elements.windGusts = findViewById(R.id.textViewWindRoseWindGusts);
        elements.windDirection = findViewById(R.id.textViewWindRoseWindDirection);
        elements.temperature = findViewById(R.id.textViewWindRoseTemperatura);
        elements.setActivity(this);

        // create the hanlder which will update the screen in background
        handler = new Handler();

        SummaryDao summary_dao = new SummaryDao();

        // get the set of current values to preconfigure all elements on this activity
        summary = summary_dao.getStationSummary(station.getSystemName());

        // update parameters (like turn the wind direction arrow)
        elements.updateFromSummary(summary);

        handler = new Handler();
        updater = new StationDetailsValuesUpdater(elements, handler, station.getSystemName());

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