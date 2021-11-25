package cc.pogoda.mobile.pogodacc.activity.updater;

import android.os.Handler;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import cc.pogoda.mobile.pogodacc.dao.SummaryDao;
import cc.pogoda.mobile.pogodacc.type.web.QualityFactor;
import cc.pogoda.mobile.pogodacc.type.web.Summary;

/**
 * This class is used to update entries on Favourites list
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
     * Used to get data from web service
     */
    private SummaryDao dao = null;

    /**
     * Not sure if this is really required but just to be sure that updater won't be started
     * after the activity had been torn down.
     */
    private boolean enabled;

    public FavouritesStationDetailsUpdater(Handler _handler) {
        handler = _handler;
        dao = new SummaryDao();
        stationsToUpdate = new HashMap<>();
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

    @Override
    public void run() {

        if (enabled && stationsToUpdate != null && stationsToUpdate.size() > 0) {

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
                Summary summary = dao.getStationSummary(stationSystemName);

                // if data has been collected
                if (summary != null) {
                    String str;

                    // check if this station transmits wind information
                    if (summary.wind_qf_native.equals(QualityFactor.FULL) || summary.wind_qf_native.equals(QualityFactor.DEGRADED)) {

                        // check if station transmits humidity
                        if (summary.humidity_qf_native.equals(QualityFactor.FULL) || summary.humidity_qf_native.equals(QualityFactor.DEGRADED)) {
                            str = String.format("%d°C %d%% %s %3.1f m/s max %3.1f m/s", Math.round(summary.avg_temperature), summary.humidity, summary.getWindDirStr(), summary.average_speed, summary.gusts);
                        }
                        else {
                            str = String.format("%d°C %s %3.1f m/s max %3.1f m/s", Math.round(summary.avg_temperature), summary.getWindDirStr(), summary.average_speed, summary.gusts);
                        }
                    }
                    else {
                        if (summary.humidity_qf_native.equals(QualityFactor.FULL) || summary.humidity_qf_native.equals(QualityFactor.DEGRADED)) {
                            str = String.format("%d°C %d%%", Math.round(summary.avg_temperature), summary.humidity);
                        }
                        else {
                            str = String.format("%d°C", Math.round(summary.avg_temperature));

                        }
                    }

                    // update text view on the favourites list
                    toUpdate.setText(str);
                }
            }

            handler.postDelayed(this, 30000);
        }
    }
}