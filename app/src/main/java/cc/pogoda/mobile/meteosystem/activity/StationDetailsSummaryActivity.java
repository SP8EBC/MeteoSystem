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
import cc.pogoda.mobile.meteosystem.activity.updater.StationDetailsValuesOnActivityFromFavsUpdater;
import cc.pogoda.mobile.meteosystem.activity.updater.StationDetailsValuesOnActivityUpdater;
import cc.pogoda.mobile.meteosystem.activity.updater.thread.StationSummaryUpdaterThread;
import cc.pogoda.mobile.meteosystem.config.AppConfiguration;
import cc.pogoda.mobile.meteosystem.dao.SummaryDao;
import cc.pogoda.mobile.meteosystem.type.StationSummaryActElements;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;
import cc.pogoda.mobile.meteosystem.type.web.Summary;

public class StationDetailsSummaryActivity extends AppCompatActivity {

    StationSummaryActElements elems = null;

    WeatherStation station = null;

    StationSummaryUpdaterThread updaterThread = null;

    StationDetailsValuesOnActivityUpdater valuesOnActUpdater = null;

    StationDetailsValuesOnActivityFromFavsUpdater valuesFromFavsSummaryUpdater = null;

    Handler handler = null;

    Main main = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        main = (Main)getApplication();

        elems = new StationSummaryActElements();

        int color = main.getThemeColours().colorOnSecondary;

        Summary summary = null;
        SummaryDao summary_dao = new SummaryDao();

        super.onCreate(savedInstanceState);

        station = (WeatherStation) getIntent().getSerializableExtra("station");

        Logger.info("[station.getSystemName() = " + station.getSystemName() +"]");

        if (AppConfiguration.locale != null && !AppConfiguration.locale.equals("default") ) {
            Logger.debug("[AppConfiguration.locale = " + AppConfiguration.locale +  "]");
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

        // create a handler to update station data in background
        handler = new Handler();

        // check if this station is on favourites list
        boolean onFavs = main.checkIsOnFavsList(station.getSystemName());

        if (onFavs) {
            valuesFromFavsSummaryUpdater = new StationDetailsValuesOnActivityFromFavsUpdater(elems, handler, station, main.getHashmapFavStationSystemNameToSummary());

            if (handler != null && valuesFromFavsSummaryUpdater != null) {
                handler.post(valuesFromFavsSummaryUpdater);
            }
        }
        else {
            updaterThread = new StationSummaryUpdaterThread(station.getSystemName());

            // create a copy of updater class for this station
            valuesOnActUpdater = new StationDetailsValuesOnActivityUpdater(elems, handler, updaterThread, station);

            if (handler != null && valuesOnActUpdater != null) {
                updaterThread.start(50);

                handler.postDelayed(valuesOnActUpdater, 500);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (updaterThread != null) {
            updaterThread.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (updaterThread != null) {
            updaterThread.start(50);
        }
    }

    @Override
    protected void onStop() {

        if (handler != null && valuesOnActUpdater != null) {
            handler.removeCallbacks(valuesOnActUpdater);
        }

        if (updaterThread != null) {
            updaterThread.stop();
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        if (updaterThread != null) {
            updaterThread.stop();
        }

        super.onDestroy();
    }
}