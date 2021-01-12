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

        // find all elements in the xml layout file and set the references in a holding object
        elements = new StationWindRoseActElements();
        elements.windArrow = findViewById(R.id.imageViewWindRoseArrow);
        elements.windSpeed = findViewById(R.id.textViewWindRoseWindSpeed);
        elements.windGusts = findViewById(R.id.textViewWindRoseWindGusts);
        elements.windDirection = findViewById(R.id.textViewWindRoseWindDirection);
        elements.temperature = findViewById(R.id.textViewWindRoseTemperatura);
        elements.maxGust = findViewById(R.id.textViewWindRoseMaxHourGust);
        elements.minAverage = findViewById(R.id.textViewWindRoseMinHourSpeed);
        elements.pressure = findViewById(R.id.textViewWindRosePressure);
        elements.setActivity(this);

        // create the handler which will update the screen in background
        handler = new Handler();

        SummaryDao summary_dao = new SummaryDao();

        // get the set of current values to preconfigure all elements on this activity
        summary = summary_dao.getStationSummary(station.getSystemName());

        // update parameters (like turn the wind direction arrow)
        elements.updateFromSummary(summary, station.getAvailableParameters());

        handler = new Handler();
        updater = new StationDetailsValuesUpdater(elements, handler, station.getSystemName(), station);

        if (handler != null && updater != null) {
            // start the handler to update the wind rose activity in background
            handler.post(updater);
        }

    }

    @Override
    protected void onStop() {

        // remove and stop background callback
        if (handler != null && updater != null) {
            handler.removeCallbacks(updater);
        }

        super.onStop();
    }
}