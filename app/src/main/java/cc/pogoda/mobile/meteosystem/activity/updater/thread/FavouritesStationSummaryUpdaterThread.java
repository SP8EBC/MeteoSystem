package cc.pogoda.mobile.meteosystem.activity.updater.thread;

import static cc.pogoda.mobile.meteosystem.config.ConstAppConfiguration.REUPDATE_VALUES_ON_ACTIVITY_ON_FAIL;

import android.os.Handler;
import android.telephony.SubscriptionManager;

import org.tinylog.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cc.pogoda.mobile.meteosystem.dao.AvailableParametersDao;
import cc.pogoda.mobile.meteosystem.dao.SummaryDao;
import cc.pogoda.mobile.meteosystem.type.AvailableParameters;
import cc.pogoda.mobile.meteosystem.type.web.AvailableParametersWeb;
import cc.pogoda.mobile.meteosystem.type.web.Summary;

/**
 * This class is a runnable executed from background Thread by ScheduledExecuter
 * which periodically download current Summary for all stations stored on favourites list
 */
public class FavouritesStationSummaryUpdaterThread implements Runnable {

    private HashMap<String, Summary> map;

    private HashMap<String, AvailableParameters> availableParametersHashMap;

    private SummaryDao summaryDao;

    private AvailableParametersDao availableParametersDao;

    private ScheduledExecutorService executor;

    private ScheduledFuture scheduledTask;

    private boolean enabled = false;

    /**
     * Set to true when a update is forced in case of a removal or adding new entry to the map
     */
    private boolean forceUpdate = false;

    public FavouritesStationSummaryUpdaterThread(HashMap<String, Summary> _out_map, HashMap<String, AvailableParameters> _avail_params_map) {
        map = _out_map;
        availableParametersHashMap = _avail_params_map;

        summaryDao = new SummaryDao();
        availableParametersDao = new AvailableParametersDao();

        executor = Executors.newScheduledThreadPool(5);
    }

    @Override
    public void run() {
        // check if map was set so something
        if (map != null && map.size() > 0) {

            Logger.info("[map.size() = " + map.size() +"]");

            // get a set of all stations from favourites
            Set<Map.Entry<String, Summary>> _set_of_stations_names = map.entrySet();

            // get an iterator to the set
            Iterator<Map.Entry<String, Summary>> it = _set_of_stations_names.iterator();

            while (it.hasNext()) {
                // get currently processed entry
                Map.Entry<String, Summary> entry = it.next();

                // get station name
                String station_name = entry.getKey();

                Summary summary = summaryDao.getStationSummary(station_name);

                // check if summary was returned (as it will not in case on HTTP 500 or something else)
                if (summary != null) {
                    Logger.info("[station_name = " + station_name + "][summary.last_timestamp = " + summary.last_timestamp + "]");

                    // put the summary back into the map
                    map.put(station_name, summary);
                }

                AvailableParameters parameters = AvailableParameters.fromWebData(availableParametersDao.getAvaliableParamsByStationName(station_name));

                if (parameters != null) {
                    availableParametersHashMap.put(station_name, parameters);
                }
            }


        }
        else {
            // no station to update may be caused by two reasons
            //  1. there is no weather station to update
            //  2. API responds very slow or there is a problem with internet connection
            Logger.info("[no station to update]");

            stop();
            start(REUPDATE_VALUES_ON_ACTIVITY_ON_FAIL);
        }

        if (forceUpdate) {
            forceUpdate = false;

            start(66000);
        }
    }

    public void updateImmediately() {
        forceUpdate = true;
        stop();

        Thread t = new Thread(this);
        t.start();
    }

    public void start(int _initial_delay) {

        Logger.debug("[_initial_delay = " + _initial_delay +"]");

        if (enabled) {
            stop();
        }

        scheduledTask = executor.scheduleAtFixedRate(this, _initial_delay, 123000, TimeUnit.MILLISECONDS);
        enabled = true;
    }

    public void stop() {
        if (enabled) {
            scheduledTask.cancel(true);
            enabled = false;
        }
    }
}
