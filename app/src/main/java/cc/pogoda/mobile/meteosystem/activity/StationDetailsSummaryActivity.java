package cc.pogoda.mobile.meteosystem.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;

import org.tinylog.Logger;

import java.util.Locale;

import cc.pogoda.mobile.meteosystem.Main;
import cc.pogoda.mobile.meteosystem.R;
import cc.pogoda.mobile.meteosystem.activity.updater.StationDetailsValuesOnActivityFromSummaryUpdater;
import cc.pogoda.mobile.meteosystem.activity.updater.StationDetailsValuesOnActivityUpdater;
import cc.pogoda.mobile.meteosystem.config.AppConfiguration;
import cc.pogoda.mobile.meteosystem.dao.SummaryDao;
import cc.pogoda.mobile.meteosystem.type.StationSummaryActElements;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;
import cc.pogoda.mobile.meteosystem.type.web.Summary;

public class StationDetailsSummaryActivity extends AppCompatActivity {

    StationSummaryActElements elems = null;

    WeatherStation station = null;

    StationDetailsValuesOnActivityUpdater valuesOnActUpdater = null;

    StationDetailsValuesOnActivityFromSummaryUpdater valuesFromSummaryUpdater = null;

    Handler handler = null;

    Main main = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        main = (Main)getApplication();

        elems = new StationSummaryActElements();

        int color = ContextCompat.getColor(this, android.R.color.secondary_text_light);

        Summary summary = null;
        SummaryDao summary_dao = new SummaryDao();

        super.onCreate(savedInstanceState);

        station = (WeatherStation) getIntent().getSerializableExtra("station");

        Logger.info("[StationDetailsSummaryActivity][onCreate][station.getSystemName() = " + station.getSystemName() +"]");

        if (AppConfiguration.locale != null && !AppConfiguration.locale.equals("default") ) {
            Logger.debug("[StationDetailsPlotsHumidity][onCreate][AppConfiguration.locale = " + AppConfiguration.locale +  "]");
            Locale locale = new Locale(AppConfiguration.locale);
            Locale.setDefault(locale);
            Resources resources = this.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }

        setContentView(R.layout.activity_station_details_summary);

        elems.title = findViewById(R.id.textViewStationDetailsSummaryTitle);
        elems.title.setText(station.getDisplayedName());

        if (station.getDisplayedName().length() < 18) {
            elems.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 38);
        }
        else {
            elems.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        }

        elems.wind_dir_val = findViewById(R.id.textViewWinddirValue);
        elems.wind_gusts_val = findViewById(R.id.textViewWindGustsValue);
        elems.wind_speed_val = findViewById(R.id.textViewWindSpeedValue);
        elems.temperature_val = findViewById(R.id.textViewTemperatureValue);
        elems.qnh_val = findViewById(R.id.textViewQnhVaue);
        elems.humidity_val = findViewById(R.id.textViewHumidityValue);
        elems.message = findViewById(R.id.textViewSummaryMessage);

        elems.goodColor = color;
        elems.badColor = Color.RED;

        // get the summary data for this station
        summary = summary_dao.getStationSummary(station.getSystemName());

        elems.updateFromSummary(summary, station.getAvailableParameters());

        // create a handler to update station data in background
        handler = new Handler();

        // check if this station is on favourites list
        boolean onFavs = main.checkIsOnFavsList(station.getSystemName());

        if (onFavs) {
            valuesFromSummaryUpdater = new StationDetailsValuesOnActivityFromSummaryUpdater(elems, handler, station, main.getHashmapStationSystemNameToSummary());

            if (handler != null && valuesFromSummaryUpdater != null) {
                handler.post(valuesFromSummaryUpdater);
            }
        }
        else {
            // create a copy of updater class for this station
            valuesOnActUpdater = new StationDetailsValuesOnActivityUpdater(elems, handler, station.getSystemName(), station);

            if (handler != null && valuesOnActUpdater != null) {
                handler.post(valuesOnActUpdater);
            }
        }




    }

    @Override
    protected void onStop() {

        if (handler != null && valuesOnActUpdater != null) {
            handler.removeCallbacks(valuesOnActUpdater);
        }

        super.onStop();
    }
}