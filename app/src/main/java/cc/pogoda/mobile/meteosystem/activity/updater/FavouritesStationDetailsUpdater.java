package cc.pogoda.mobile.meteosystem.activity.updater;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.widget.TextView;

import org.tinylog.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import cc.pogoda.mobile.meteosystem.dao.AvailableParametersDao;
import cc.pogoda.mobile.meteosystem.dao.SummaryDao;
import cc.pogoda.mobile.meteosystem.type.web.AvailableParametersWeb;
import cc.pogoda.mobile.meteosystem.type.web.QualityFactor;
import cc.pogoda.mobile.meteosystem.type.web.Summary;

/**
 * This class is used to update entries (TextView) on Favourites list using HashMap
 * which is updated by {@link FavouritesStationSummaryUpdater}
 */
public class FavouritesStationDetailsUpdater implements Runnable {

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
    private AvailableParametersDao availableParametersDao = null;

    /**
     * This map comes from 'Main' class and it is shared with @link{{@link FavouritesStationSummaryUpdater}}
     */
    HashMap<String, Summary> stationNameSummary = null;

    /**
     * Not sure if this is really required but just to be sure that updater won't be started
     * after the activity had been torn down.
     */
    private boolean enabled;

    public FavouritesStationDetailsUpdater(Handler _handler, HashMap<String, Summary> _station_system_name_to_summary) {
        handler = _handler;
        stationsToUpdate = new HashMap<>();
        availableParametersDao = new AvailableParametersDao();
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

                Logger.debug("[FavouritesStationDetailsUpdater][run][stationSystemName = " + stationSystemName +"][summary.last_timestamp = " + summary.last_timestamp +"]");

                // query for available parameters
                AvailableParametersWeb params = availableParametersDao.getAvaliableParamsByStationName(stationSystemName);

                // if data has been collected
                if (summary != null) {
                    String str;

                    // check if this station transmits wind information
                    if (params.hasWind) {

                        // check if station transmits humidity
                        if (params.hasHumidity) {
                            str = String.format("%s  %d%%  %s  %s max %s", summary.getTemperatureStr(false, true), summary.humidity, summary.getWindDirStr(), summary.getWindspeedStr(false), summary.getWindgustsStr(false));
                        }
                        else {
                            str = String.format("%s  %s  %s max %s", summary.getTemperatureStr(false, true), summary.getWindDirStr(), summary.getWindspeedStr(false), summary.getWindgustsStr(false));
                        }
                    }
                    else {
                        if (params.hasHumidity) {
                            str = String.format("%s  %d%%", summary.getTemperatureStr(false, true), summary.humidity);
                        }
                        else {
                            str = String.format("%s", summary.getTemperatureStr(false, true));

                        }
                    }

                    // update text view on the favourites list
                    toUpdate.setText(str);

                    if (    (params.hasHumidity && summary.humidity_qf_native.equals(QualityFactor.NOT_AVALIABLE)) ||
                            (summary.temperature_qf_native.equals(QualityFactor.NOT_AVALIABLE)) ||
                            (params.hasWind && summary.wind_qf_native.equals(QualityFactor.NOT_AVALIABLE)))
                    {
                        toUpdate.setTextColor(Color.RED);
                    }
                    else {
                        toUpdate.setTextColor(androidx.activity.R.color.secondary_text_default_material_light);
                    }
                }
            }

            handler.postDelayed(this, 60000);
        }
    }
}
