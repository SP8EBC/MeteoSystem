package cc.pogoda.mobile.meteosystem.activity.updater;

import static cc.pogoda.mobile.meteosystem.config.ConstAppConfiguration.DETAILS_ON_FAVS_LIST_DEFAULT_UPDATE;
import static cc.pogoda.mobile.meteosystem.config.ConstAppConfiguration.DETAILS_ON_FAVS_LIST_REUPDATE;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.widget.TextView;

import org.tinylog.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import cc.pogoda.mobile.meteosystem.activity.updater.thread.FavouritesStationSummaryUpdaterThread;
import cc.pogoda.mobile.meteosystem.dao.AvailableParametersDao;
import cc.pogoda.mobile.meteosystem.type.AvailableParameters;
import cc.pogoda.mobile.meteosystem.type.web.AvailableParametersWeb;
import cc.pogoda.mobile.meteosystem.type.web.QualityFactor;
import cc.pogoda.mobile.meteosystem.type.web.Summary;

/**
 * This class is used to update entries (TextView) on Favourites list using HashMap
 * which is updated by {@link FavouritesStationSummaryUpdaterThread}
 */
public class FavouritesStationDetailsOnListUpdater implements Runnable {

    /**
     * Handler is used by Android to put a Runnable into MessageQueue handler by the Looper. This
     * runnable can be scheduled to be serviced at certain point of time
     */
    private Handler handler;

    /**
     * A collection which holds
     */
    private HashMap<String, TextView> stationsToUpdate;

    /**
     *
     */
    //private AvailableParametersDao availableParametersDao = null;
    private HashMap<String, AvailableParameters> availParams;

    /**
     * This map comes from 'Main' class and it is shared with @link{{@link FavouritesStationSummaryUpdaterThread}}
     */
    HashMap<String, Summary> stationNameSummary = null;

    /**
     * Not sure if this is really required but just to be sure that updater won't be started
     * after the activity had been torn down.
     */
    private boolean enabled;

    public FavouritesStationDetailsOnListUpdater(Handler _handler, HashMap<String, Summary> _station_system_name_to_summary, HashMap<String, AvailableParameters> _avail_params) {
        handler = _handler;
        stationsToUpdate = new HashMap<>();
        availParams = _avail_params;
        stationNameSummary = _station_system_name_to_summary;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void addNewStation(String _station_system_name, TextView _tv) {
        stationsToUpdate.put(_station_system_name, _tv);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void run() {

        int nextExecutionDelay = DETAILS_ON_FAVS_LIST_DEFAULT_UPDATE;

        if (stationNameSummary != null && enabled && stationsToUpdate != null && stationsToUpdate.size() > 0) {

            // get a set of all elements stored in the map
            Set<Map.Entry<String, TextView>> entries = stationsToUpdate.entrySet();

            // create something iterable from the set. the set itself doesn't guarantee the same order than
            // objects were put in, but in this case it isn't a problem.
            Vector<Map.Entry<String, TextView>> vectorOfEntries = new Vector<>(entries);

            for (Map.Entry<String, TextView> e : vectorOfEntries) {

                // extract data from pair
                String stationSystemName = e.getKey();
                TextView toUpdate = e.getValue();

                // query web service for station data
                Summary summary = stationNameSummary.get(stationSystemName);

                // query for available parameters
                AvailableParameters params = availParams.get(stationSystemName);

                // if data has been collected
                if (summary != null && params != null) {
                    Logger.debug("[stationSystemName = " + stationSystemName +"][summary.last_timestamp = " + summary.last_timestamp +"]");

                    String str;

                    // check if this station transmits wind information
                    if (params.windSpeed) {

                        // check if station transmits humidity
                        if (params.humidity) {
                            str = String.format("%s  %d%%  %s  %s max %s", summary.getTemperatureStr(false, true), summary.humidity, summary.getWindDirStr(), summary.getWindspeedStr(false), summary.getWindgustsStr(false));
                        }
                        else {
                            str = String.format("%s  %s  %s max %s", summary.getTemperatureStr(false, true), summary.getWindDirStr(), summary.getWindspeedStr(false), summary.getWindgustsStr(false));
                        }
                    }
                    else {
                        if (params.humidity) {
                            str = String.format("%s  %d%%", summary.getTemperatureStr(false, true), summary.humidity);
                        }
                        else {
                            str = String.format("%s", summary.getTemperatureStr(false, true));

                        }
                    }

                    // update text view on the favourites list
                    toUpdate.setText(str);

                    if (    (params.humidity && summary.humidity_qf_native.equals(QualityFactor.NOT_AVALIABLE)) ||
                            (summary.temperature_qf_native.equals(QualityFactor.NOT_AVALIABLE)) ||
                            (params.windSpeed && summary.wind_qf_native.equals(QualityFactor.NOT_AVALIABLE)))
                    {
                        toUpdate.setTextColor(Color.RED);
                    }
                    else {
                        toUpdate.setTextColor(androidx.activity.R.color.secondary_text_default_material_light);
                    }
                }
                else {
                    Logger.error("[stationSystemName = " + stationSystemName + "][summary object is null!! Maybe the API responds exceptionally slow?]");
                    nextExecutionDelay = DETAILS_ON_FAVS_LIST_REUPDATE;
                }
            }

            handler.postDelayed(this, nextExecutionDelay);
        }
    }
}
