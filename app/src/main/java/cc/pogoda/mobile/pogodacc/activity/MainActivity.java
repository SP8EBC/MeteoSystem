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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.activity.handler.MainActImageButtonAllStationsClickEvent;
import cc.pogoda.mobile.pogodacc.activity.handler.MainActImageButtonExportClickEvent;
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

    private FavouritiesFile favouritiesFile;

    private List<WeatherStation> listOfAllStations;

    List<WeatherStation> favs;

    private ParceableStationsList parceableListOfAllStations;

    private ParceableStationsList parceableListOfFavStations;

    private MainActImageButtonFavouritesClickEvent mainActImageButtonFavouritesClickEvent = null;

    private ImageButton imageButtonFavourites;

    private ImageButton exportButton;

    public MainActivity() {

    }

    private void recreateListOfFavs() {

        // check if this is a first call after application start
        if (favs == null) {
            favs = favouritiesFile.loadFavourites();
        }

        // if favs is still null it means that favourites file doesn't even exists
        // so and user hasn't added any station to it yet
        if (favs == null) {
            favs = new ArrayList<>();
        }
        else {
            // update values for the fav list with listOfAllStations
            //for (WeatherStation f : favs) {
            for (int i = 0; i < favs.size(); i++) {

                //
                WeatherStation fromFavs = favs.get(i);

                // find an index of updated station
                int idx = listOfAllStations.indexOf(fromFavs);

                // get the station
                WeatherStation fromAllStations = listOfAllStations.get(idx);

                // update all parameters
                fromFavs.setAvailableParameters(fromAllStations.getAvailableParameters());
                fromFavs.setMoreInfo(fromAllStations.getMoreInfo());
                fromFavs.setImageAlign(fromAllStations.getImageAlign());
                fromFavs.setImageUrl(fromAllStations.getImageUrl());
                fromFavs.setSponsorUrl(fromAllStations.getSponsorUrl());
                fromFavs.setMoreInfo(fromAllStations.getMoreInfo());
                fromFavs.setLon(fromAllStations.getLon());
                fromFavs.setLat(fromAllStations.getLat());
                fromFavs.setDisplayedName(fromAllStations.getDisplayedName());
                fromFavs.setDisplayedLocation(fromAllStations.getDisplayedLocation());
                fromFavs.setTimezone(fromAllStations.getTimezone());

                // there is no need to delete and put object on the list once again
                // as a list does not make a copy of the object. It (ArrayList) keeps
                // only a reference to an object


            }
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

        favouritiesFile = new FavouritiesFile(fileNames);

        // download all stations from API
        listOfAllStations = new AllStationsDao().getAllStations();

        // convert this to parceable to exchange across intents
        parceableListOfAllStations = ParceableStationsList.createFromStdList(listOfAllStations);

        // recreate list of favorites
        recreateListOfFavs();

        ImageButton imageButtonAllStations = (ImageButton)findViewById(R.id.imageButtonAllStations);
        if (imageButtonAllStations != null)
            imageButtonAllStations.setOnClickListener(new MainActImageButtonAllStationsClickEvent(this, parceableListOfAllStations));

        imageButtonFavourites = (ImageButton)findViewById(R.id.imageButtonFavourites);

        // set an action for clicking on export data button
        exportButton = (ImageButton)findViewById(R.id.imageButtonExport);
        if (exportButton != null) {
            exportButton.setOnClickListener(new MainActImageButtonExportClickEvent(this, parceableListOfFavStations));
        }

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
                // check of list consist this station
                if (favs.contains(serviceEvent.getStation())) {
                    return;
                }

                // add favourites to list
                favs.add(serviceEvent.getStation());

                try {
                    // save the list into JSON file
                    favouritiesFile.persistFavourities(favs);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case DELETE:
                favs.remove(serviceEvent.getStation());
                break;
        }

        // recreate parceable object and pass it everywhere
        recreateListOfFavs();
        //Toast.makeText(this, intentServiceResult.getResultValue(), Toast.LENGTH_SHORT).show();
    }

}