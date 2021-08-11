package cc.pogoda.mobile.pogodacc.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageButton;

import com.jakewharton.threetenabp.AndroidThreeTen;

import java.io.File;
import java.util.List;
import java.util.Locale;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.activity.handler.MainActImageButtonAllStationsClickEvent;
import cc.pogoda.mobile.pogodacc.activity.handler.MainActImageButtonFavouritesClickEvent;
import cc.pogoda.mobile.pogodacc.config.AppConfiguration;
import cc.pogoda.mobile.pogodacc.dao.AllStationsDao;
import cc.pogoda.mobile.pogodacc.file.FavouritiesFile;
import cc.pogoda.mobile.pogodacc.file.FileNames;
import cc.pogoda.mobile.pogodacc.type.Favourites;
import cc.pogoda.mobile.pogodacc.type.ParceableStationsList;
import cc.pogoda.mobile.pogodacc.type.WeatherStation;

public class MainActivity extends AppCompatActivity {

    private Context baseContext;

    private FileNames fileNames;

    private List<WeatherStation> listOfAllStations;

    private ParceableStationsList parceableListOfAllStations;

    private ParceableStationsList parceableListOfFavStations;

    public MainActivity() {

        //AppConfiguration.favourites = new Favourites(fileNames);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidThreeTen.init(this);

        Locale locale = new Locale("pl");
        Locale.setDefault(locale);
        Resources resources = this.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        setContentView(R.layout.activity_main);

        baseContext = getApplicationContext();

        fileNames = new FileNames(baseContext);

        listOfAllStations = new AllStationsDao().getAllStations();

        parceableListOfAllStations = ParceableStationsList.createFromStdList(listOfAllStations);

        List<WeatherStation> favs = new FavouritiesFile(fileNames).loadFavourites();

        ImageButton imageButtonAllStations = (ImageButton)findViewById(R.id.imageButtonAllStations);
        if (imageButtonAllStations != null)
            imageButtonAllStations.setOnClickListener(new MainActImageButtonAllStationsClickEvent(this, parceableListOfAllStations));

        ImageButton imageButtonFavourites = (ImageButton)findViewById(R.id.imageButtonFavourites);
        if (imageButtonFavourites != null) {
            imageButtonFavourites.setOnClickListener(new MainActImageButtonFavouritesClickEvent(this, parceableListOfFavStations));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

}