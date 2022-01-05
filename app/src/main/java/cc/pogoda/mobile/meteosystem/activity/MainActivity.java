package cc.pogoda.mobile.meteosystem.activity;

// https://www.softicons.com/web-icons/vector-stylish-weather-icons-by-bartosz-kaszubowski/sun-rays-cloud-icon#google_vignette

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.strictmode.Violation;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cc.pogoda.mobile.meteosystem.Main;
import cc.pogoda.mobile.meteosystem.R;
import cc.pogoda.mobile.meteosystem.activity.handler.MainActImageButtonAllStationsClickEvent;
import cc.pogoda.mobile.meteosystem.activity.handler.MainActImageButtonExportClickEvent;
import cc.pogoda.mobile.meteosystem.activity.handler.MainActImageButtonFavouritesClickEvent;
import cc.pogoda.mobile.meteosystem.activity.handler.MainActImageButtonSettingsClickEvent;
import cc.pogoda.mobile.meteosystem.config.AppConfiguration;
import cc.pogoda.mobile.meteosystem.dao.AllStationsDao;
import cc.pogoda.mobile.meteosystem.file.ConfigurationFile;
import cc.pogoda.mobile.meteosystem.file.FavouritiesFile;
import cc.pogoda.mobile.meteosystem.file.FileNames;
import cc.pogoda.mobile.meteosystem.type.ParceableStationsList;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;
import cc.pogoda.mobile.meteosystem.type.WeatherStationListEvent;

public class MainActivity extends AppCompatActivity {

    private Main main;

    private Context baseContext;

    private MainActImageButtonFavouritesClickEvent mainActImageButtonFavouritesClickEvent = null;

    private ImageButton imageButtonFavourites;

    private ImageButton exportButton;

    private ImageButton settingsButton;

    public MainActivity() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Called when a user goes back to the main screen
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        main = (Main) getApplication();

        baseContext = getApplicationContext();

        // create an event handler fired when a user click 'favourites' button
        mainActImageButtonFavouritesClickEvent = new MainActImageButtonFavouritesClickEvent(this);

        // assign on click listener
        if (imageButtonFavourites != null) {
            imageButtonFavourites.setOnClickListener(mainActImageButtonFavouritesClickEvent);
        }

        setContentView(R.layout.activity_main);

        ImageButton imageButtonAllStations = (ImageButton)findViewById(R.id.imageButtonAllStations);
        if (imageButtonAllStations != null)
            imageButtonAllStations.setOnClickListener(new MainActImageButtonAllStationsClickEvent(this));

        imageButtonFavourites = (ImageButton)findViewById(R.id.imageButtonFavourites);
        if (imageButtonFavourites != null) {
            imageButtonFavourites.setOnClickListener(new MainActImageButtonFavouritesClickEvent(this));
        }

        // set an action for clicking on export data button
        exportButton = (ImageButton)findViewById(R.id.imageButtonExport);
        if (exportButton != null) {
            exportButton.setOnClickListener(new MainActImageButtonExportClickEvent(this));
        }

        settingsButton = (ImageButton) findViewById(R.id.imageButtonSettings);
        if (settingsButton != null) {
            settingsButton.setOnClickListener(new MainActImageButtonSettingsClickEvent(this, main.getConfFile()));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //return super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.menu_item_translation_authors: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("ENG: Mateusz Lubecki\r\n" +
                        "CZE: Sylwiusz Pachel\r\n" +
                        "GER: Jakub Fiałek\r\n" +
                        "LAT: Andris Stikáns\r\n" +
                        "UKR, RUS: Влад Поливач \r\n(Wład Polywacz)\r\n\r\nProgram Icon: Bartosz Kaszubowski");
                builder.setPositiveButton(R.string.ok, (DialogInterface var1, int var2) -> {
                    var1.dismiss();
                });
                builder.create();
                builder.show();
            }
        }

        return true;
    }

    public boolean listOfAllStationsReady() {
        if (main != null) {
            return main.listOfAllStationsReady();
        }
        else {
            return false;
        }
    }

    public boolean listOfAllFavsReady() {
        if (main != null) {
            return main.listOfFavsReady();
        }
        else {
            return false;
        }
    }

}