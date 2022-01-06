package cc.pogoda.mobile.meteosystem.activity.updater;

import android.os.Handler;

import org.tinylog.Logger;

import java.util.HashMap;

import cc.pogoda.mobile.meteosystem.type.StationActivityElements;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;
import cc.pogoda.mobile.meteosystem.type.web.Summary;

/**
 * This class uses external HashMap updated by @link{{@link FavouritesStationSummaryUpdater}}
 */
public class StationDetailsValuesOnActivityFromSummaryUpdater implements Runnable {

    HashMap<String, Summary> mapWithSummary;

    StationActivityElements elementsToUpdate;

    Handler handler;

    WeatherStation stationToUpdate;

    public StationDetailsValuesOnActivityFromSummaryUpdater(StationActivityElements elems, Handler h, WeatherStation station, HashMap<String, Summary> _map_with_summary_data) {
        stationToUpdate = station;
        handler = h;
        elementsToUpdate = elems;
        mapWithSummary = _map_with_summary_data;
    }

    @Override
    public void run() {

        if (mapWithSummary != null && elementsToUpdate != null) {

            Logger.info("[StationDetailsValuesOnActivityFromSummaryUpdater][run][stationToUpdate.getSystemName() = " + stationToUpdate.getSystemName() +"]");

            elementsToUpdate.updateFromSummary(mapWithSummary.get(stationToUpdate.getSystemName()), stationToUpdate.getAvailableParameters());

            handler.postDelayed(this, 90000);
        }
    }
}
