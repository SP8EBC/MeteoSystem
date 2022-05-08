package cc.pogoda.mobile.meteosystem.activity.updater;

import static cc.pogoda.mobile.meteosystem.config.ConstAppConfiguration.NORMAL_UPDATE_VALUES_ON_ACTIVITY;
import static cc.pogoda.mobile.meteosystem.config.ConstAppConfiguration.REUPDATE_VALUES_ON_ACTIVITY_ON_FAIL;

import android.os.Handler;

import org.tinylog.Logger;

import cc.pogoda.mobile.meteosystem.activity.updater.thread.StationSummaryUpdaterThread;
import cc.pogoda.mobile.meteosystem.dao.SummaryDao;
import cc.pogoda.mobile.meteosystem.type.StationActivityElements;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;
import cc.pogoda.mobile.meteosystem.type.web.Summary;



/**
 * Class used to update the content of StationDetailsSummaryActivity and
 * StationDetailsWindRoseActivity
 */
public class StationDetailsValuesOnActivityUpdater implements Runnable {

    StationActivityElements elements = null;

    Handler handler = null;

    SummaryDao dao = null;

    StationSummaryUpdaterThread updater_thread;

    Summary station_summary = null;

    String station_name;

    WeatherStation station;

    public StationDetailsValuesOnActivityUpdater(StationActivityElements elems, Handler h, StationSummaryUpdaterThread updaterThread, WeatherStation station) {
        elements = elems;
        handler = h;
        updater_thread = updaterThread;
        this.station_name = station.getSystemName();
        this.station = station;

        dao = new SummaryDao();
    }


    @Override
    public void run() {

        if (elements == null || updater_thread == null) {
            Logger.error("[something is null even if it shouldn't!!!!]");
            return;
        }
        else {
            // get the current data from the Web Service
            station_summary = updater_thread.getSummary();

            Logger.debug("[station_name = " + station_name +"]");

            if (station_summary != null) {
                // null check is done inside this call
                elements.updateFromSummary(station_summary, station.getAvailableParameters());
                handler.postDelayed(this, NORMAL_UPDATE_VALUES_ON_ACTIVITY);
            }
            else {
                // 'station_summary' might be null if internet connection is poor and background
                // thread wasn't able to download a summary on time
                handler.postDelayed(this, REUPDATE_VALUES_ON_ACTIVITY_ON_FAIL);
            }
        }

    }
}
