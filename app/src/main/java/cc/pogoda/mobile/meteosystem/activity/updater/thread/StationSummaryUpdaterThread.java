package cc.pogoda.mobile.meteosystem.activity.updater.thread;

import static cc.pogoda.mobile.meteosystem.config.ConstAppConfiguration.REUPDATE_VALUES_ON_ACTIVITY_ON_FAIL;

import androidx.annotation.NonNull;

import org.tinylog.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import cc.pogoda.mobile.meteosystem.dao.SummaryDao;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;
import cc.pogoda.mobile.meteosystem.type.web.Summary;

/**
 * This class is simmilar to {@link FavouritesStationSummaryUpdaterThread}, but it
 * downloads summary data for any defined weather station
 */
public class StationSummaryUpdaterThread implements Runnable {

    public boolean isEnabled() {
        return enabled;
    }

    boolean enabled = false;

    @NonNull
    String systemName;

    ScheduledExecutorService executorService;

    ScheduledFuture scheduledTask;

    Summary summary = null;

    public Summary getSummary() {
        return summary;
    }

    public StationSummaryUpdaterThread(@NonNull String stationSystemName) {
        systemName = stationSystemName;

        executorService = Executors.newScheduledThreadPool(3);
    }

    @Override
    public void run() {
        if (systemName != null) {
            Logger.info("[StationSummaryUpdaterThread][run][systemName = " + systemName +"]");

            SummaryDao summaryDao = new SummaryDao();

            summary = summaryDao.getStationSummary(systemName);

            if (summary == null) {
                // no ssummary data may be caused by two reasons
                //  1. there is no weather station to update
                //  2. API responds very slow or there is a problem with internet connection
                Logger.info("[no station to update]");

                stop();
                start(REUPDATE_VALUES_ON_ACTIVITY_ON_FAIL);
            }
        }
    }

    public void start(int _initial_delay) {

        Logger.info("[_initial_delay = " + _initial_delay +"]");

        if (enabled) {
            stop();
        }

        scheduledTask = executorService.scheduleAtFixedRate(this, _initial_delay, 60000, TimeUnit.MILLISECONDS);
        enabled = true;
    }

    public void stop() {
        if (enabled) {
            Logger.info("[systemName = " + systemName +"]");
            scheduledTask.cancel(true);
            enabled = false;
        }
    }
}
