package cc.pogoda.mobile.pogodacc.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageButton;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Locale;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.activity.handler.MainActImageButtonAllStationsClickEvent;
import cc.pogoda.mobile.pogodacc.activity.handler.MainActImageButtonFavouritesClickEvent;
import cc.pogoda.mobile.pogodacc.dao.AllStationsDao;
import cc.pogoda.mobile.pogodacc.file.FavouritiesFile;
import cc.pogoda.mobile.pogodacc.file.FileNames;
import cc.pogoda.mobile.pogodacc.type.ParceableStationsList;
import cc.pogoda.mobile.pogodacc.type.WeatherStation;
import cc.pogoda.mobile.pogodacc.type.WeatherStationListEvent;

public class MainActivity extends AppCompatActivity {

    private Context baseContext;

    private FileNames fileNames;

    private List<WeatherStation> listOfAllStations;

    List<WeatherStation> favs;

    private ParceableStationsList parceableListOfAllStations;

    private ParceableStationsList parceableListOfFavStations;

    private MainActImageButtonFavouritesClickEvent mainActImageButtonFavouritesClickEvent = null;

    private ImageButton imageButtonFavourites;

    public MainActivity() {

    }

    private void recreateListOfFavs() {

        // check if this is a first call after application start
        if (favs == null) {
            favs = new FavouritiesFile(fileNames).loadFavourites();
        }

        // update values for the fav list with listOfAllStations
        for (WeatherStation f : favs) {

            // find an index of updated station
            int idx = listOfAllStations.indexOf(f);

            // get the station
            WeatherStation fromAllStations = listOfAllStations.get(idx);

            // update all parameters
            f.setAvailableParameters(fromAllStations.getAvailableParameters());
            f.setMoreInfo(fromAllStations.getMoreInfo());
            f.setImageAlign(fromAllStations.getImageAlign());
            f.setImageUrl(fromAllStations.getImageUrl());
            f.setSponsorUrl(fromAllStations.getSponsorUrl());
            f.setMoreInfo(fromAllStations.getMoreInfo());
        }

        parceableListOfFavStations = ParceableStationsList.createFromStdList(favs);

        // create an event handler fired when a user click 'favourites' button
        mainActImageButtonFavouritesClickEvent = new MainActImageButtonFavouritesClickEvent(this, parceableListOfFavStations);

        // assign on click listener
        if (imageButtonFavourites != null) {
            imageButtonFavourites.setOnClickListener(mainActImageButtonFavouritesClickEvent);
        }
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
        recreateListOfFavs();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // register to Event bus to receive events when a station is added od removed from favourites
        EventBus.getDefault().register(this);

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

        // download all stations from API
        listOfAllStations = new AllStationsDao().getAllStations();

        // convert this to parceable to exchange across intents
        parceableListOfAllStations = ParceableStationsList.createFromStdList(listOfAllStations);

        ImageButton imageButtonAllStations = (ImageButton)findViewById(R.id.imageButtonAllStations);
        if (imageButtonAllStations != null)
            imageButtonAllStations.setOnClickListener(new MainActImageButtonAllStationsClickEvent(this, parceableListOfAllStations));

        imageButtonFavourites = (ImageButton)findViewById(R.id.imageButtonFavourites);

        // recreate list of favorites
        recreateListOfFavs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void weatherStationListHandler(WeatherStationListEvent serviceEvent) {
        System.out.println(serviceEvent.toString());

        switch (serviceEvent.getEventReason()) {

            case ADD:
                favs.add(serviceEvent.getStation());
                break;
            case DELETE:
                favs.remove(serviceEvent.getStation());
                break;
        }

        recreateListOfFavs();
        //Toast.makeText(this, intentServiceResult.getResultValue(), Toast.LENGTH_SHORT).show();
    }

}