package cc.pogoda.mobile.meteosystem.activity.updater;

import static cc.pogoda.mobile.meteosystem.config.ConstAppConfiguration.NORMAL_UPDATE_VALUES_ON_ACTIVITY;
import static cc.pogoda.mobile.meteosystem.config.ConstAppConfiguration.REUPDATE_VALUES_ON_ACTIVITY_ON_FAIL;

import android.os.Handler;

import org.tinylog.Logger;

import java.util.HashMap;

import cc.pogoda.mobile.meteosystem.activity.updater.thread.FavouritesStationSummaryUpdaterThread;
import cc.pogoda.mobile.meteosystem.type.StationActivityElements;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;
import cc.pogoda.mobile.meteosystem.type.web.Summary;

/**
 * This class uses external HashMap updated by @link{{@link FavouritesStationSummaryUpdaterThread}}
 */
public class StationDetailsValuesOnActivityFromFavsUpdater implements Runnable {

    HashMap<String, Summary> mapWithSummary;

    StationActivityElements elementsToUpdate;

    Handler handler;

    WeatherStation stationToUpdate;

    public StationDetailsValuesOnActivityFromFavsUpdater(StationActivityElements elems, Handler h, WeatherStation station, HashMap<String, Summary> _map_with_summary_data) {
        stationToUpdate = station;
        handler = h;
        elementsToUpdate = elems;
        mapWithSummary = _map_with_summary_data;
    }

    @Override
    public void run() {

        if (mapWithSummary != null && elementsToUpdate != null) {

            Logger.info("[stationToUpdate.getSystemName() = " + stationToUpdate.getSystemName() +"]");

            Summary summary = mapWithSummary.get(stationToUpdate.getSystemName());

            if (summary != null) {
                elementsToUpdate.updateFromSummary(summary, stationToUpdate.getAvailableParameters());
                handler.postDelayed(this, NORMAL_UPDATE_VALUES_ON_ACTIVITY);
            }
            else {
                handler.postDelayed(this, REUPDATE_VALUES_ON_ACTIVITY_ON_FAIL);
            }

        }
    }
}
