package cc.pogoda.mobile.meteosystem;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.pogoda.mobile.meteosystem.activity.updater.thread.FavouritesStationSummaryUpdaterThread;

import cc.pogoda.mobile.meteosystem.file.ConfigurationFile;
import cc.pogoda.mobile.meteosystem.file.FavouritiesFile;
import cc.pogoda.mobile.meteosystem.file.FileNames;
import cc.pogoda.mobile.meteosystem.service.GetAllStationsService;
import cc.pogoda.mobile.meteosystem.type.AllStationsReceivedEvent;
import cc.pogoda.mobile.meteosystem.type.AvailableParameters;
import cc.pogoda.mobile.meteosystem.type.ThemeColours;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;
import cc.pogoda.mobile.meteosystem.type.WeatherStationListEvent;
import cc.pogoda.mobile.meteosystem.type.web.Summary;

public class Main extends Application {
    private static String TAG = Main.class.getSimpleName();

    private File directory;

    private File directoryForLogs;

    private Context ctx;

    private ConfigurationFile confFile;

    public FileNames getFileNames() {
        return fileNames;
    }

    private FileNames fileNames;

    private FavouritiesFile favouritiesFile;

    public List<WeatherStation> getListOfAllStations() {
        return listOfAllStations;
    }

    private List<WeatherStation> listOfAllStations;

    public List<WeatherStation> getFavs() {
        return favs;
    }

    private List<WeatherStation> favs;

    /**
     * This download summary for all stations stored on favourites list and stores results
     * in 'HashMap<String, Summary> stationSystemNameToSummary'
     */
    private FavouritesStationSummaryUpdaterThread favsSummaryUpdater = null;

    /**
     * This map stores summary for all favourites station
     */
    private HashMap<String, Summary> hashmapFavStationSystemNameToSummary = null;

    /**
     * This hash map stores available parameters for all stations defined in the system
     */
    private HashMap<String, AvailableParameters> hashmapAllStationSystemNameToAvailParameters = null;

    public ThemeColours getThemeColours() {
        return themeColours;
    }

    /**
     * Used everywhere, where a colour of any element is set programatically (not globally from the theme)
     */
    private ThemeColours themeColours;

    public File getDirectory() {
        return directory;
    }

    public File getDirectoryForLogs() {
        return directoryForLogs;
    }

    public ConfigurationFile getConfFile() {
        return confFile;
    }

    public HashMap<String, Summary> getHashmapFavStationSystemNameToSummary() {
        return hashmapFavStationSystemNameToSummary;
    }

    public HashMap<String, AvailableParameters> getHashmapAllStationSystemNameToAvailParameters() {
        return hashmapAllStationSystemNameToAvailParameters;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ctx = this.getApplicationContext();

        confFile = new ConfigurationFile(ctx);

        directory = getApplicationContext().getDir("files", Context.MODE_PRIVATE);

        directoryForLogs = new File(directory.getAbsolutePath() + "/logs/");

        System.setProperty("tinylog.directory", directoryForLogs.getAbsolutePath());

        Logger.info("Application starting...");

//        StrictMode.VmPolicy.Builder b = new StrictMode.VmPolicy.Builder();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            StrictMode.VmPolicy policy = b.detectAll().detectNonSdkApiUsage().penaltyListener((Runnable r) -> r.run(), (Violation v) -> {
//                v.printStackTrace();
//                Logger.warn("[StrictMode.VmPolicy][penaltyListener][Violation][v.getLocalizedMessage() = " + v.getLocalizedMessage() + "]");
//            }).build();
//            StrictMode.setVmPolicy(policy);
//        }

        AndroidThreeTen.init(this);

        EventBus.getDefault().register(this);

        ConfigurationFile confFile = new ConfigurationFile(ctx);

        confFile.restoreFromFile();

        hashmapFavStationSystemNameToSummary = new HashMap<>();

        hashmapAllStationSystemNameToAvailParameters = new HashMap<>();

        fileNames = new FileNames(ctx);

        favouritiesFile = new FavouritiesFile(fileNames);

        themeColours = new ThemeColours();

        // Download all stations from API in background via JobIntentService. Results are send
        //back with Broadcast receiver.
        startGetAllStationsService();

        // recreate list of favorites
        recreateListOfFavs();

        favsSummaryUpdater = new FavouritesStationSummaryUpdaterThread(hashmapFavStationSystemNameToSummary, hashmapAllStationSystemNameToAvailParameters);

        favsSummaryUpdater.start(50);

//        if (AppConfiguration.locale != null && !AppConfiguration.locale.equals("default") ) {
//            Logger.debug("[Main][onCreate][AppConfiguration.locale = " + AppConfiguration.locale +  "]");
//            Locale locale = new Locale(AppConfiguration.locale);
//            Locale.setDefault(locale);
//            Resources resources = this.getResources();
//            Configuration config = resources.getConfiguration();
//            config.setLocale(locale);
//            Logger.debug("[Main][onCreate][locale = " + locale.toLanguageTag() +  "]");
//            resources.updateConfiguration(config, resources.getDisplayMetrics());
//        }
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        EventBus.getDefault().unregister(this);
    }

    private void recreateListOfFavs() {

        if(listOfAllStations == null) {
            Logger.info("[listOfAllStations=null]");
            return;
        }

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
                fromFavs.setCallsignSsid(fromAllStations.getCallsignSsid());
                fromFavs.setStationNameTextColor(fromAllStations.getStationNameTextColor());

                // there is no need to delete and put object on the list once again
                // as a list does not make a copy of the object. It (ArrayList) keeps
                // only a reference to an object

                hashmapFavStationSystemNameToSummary.put(fromAllStations.getSystemName(), null);

            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void weatherStationListHandler(WeatherStationListEvent serviceEvent) {
        Logger.info("[serviceEvent = " + serviceEvent +  "]");

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

                try {
                    // save the list into JSON file
                    favouritiesFile.persistFavourities(favs);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
        }

        // recreate parceable object and pass it everywhere
        recreateListOfFavs();

        favsSummaryUpdater.updateImmediately();

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void allStationsEventHandler(@NonNull AllStationsReceivedEvent event) {
        Logger.info("[event = " + event.toString() +"]");
        this.listOfAllStations = event.getStations();
        recreateListOfFavs();
    }

    public boolean listOfAllStationsReady() {
        return listOfAllStations != null && listOfAllStations.size() > 0;
    }

    public boolean listOfFavsReady() {
        /* && favs.size() > 0*/
        return favs != null;
    }

    public boolean checkIsOnFavsList(String _system_name) {
        boolean out = false;

        for (WeatherStation wx : favs) {
            if (wx.getSystemName().equals(_system_name)) {
                out = true;
                break;
            }
        }

        return out;
    }


    public void startGetAllStationsService () {
        Intent mIntent = new Intent(this, GetAllStationsService.class);
        GetAllStationsService.enqueueWork(this, mIntent);
    }

}
