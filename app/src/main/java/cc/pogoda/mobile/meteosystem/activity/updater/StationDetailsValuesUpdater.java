package cc.pogoda.mobile.meteosystem.activity.updater;

import android.os.Handler;

import cc.pogoda.mobile.meteosystem.dao.SummaryDao;
import cc.pogoda.mobile.meteosystem.type.StationActivityElements;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;
import cc.pogoda.mobile.meteosystem.type.web.Summary;



/**
 * Class used to update the content of StationDetailsSummaryActivity and
 * StationDetailsWindRoseActivity
 */
public class StationDetailsValuesUpdater implements Runnable {

    StationActivityElements elements = null;

    Handler handler = null;

    SummaryDao dao = null;

    Summary station_summary = null;

    String station_name;

    WeatherStation station;

    public StationDetailsValuesUpdater(StationActivityElements elems, Handler h, String station_name, WeatherStation station) {
        elements = elems;
        handler = h;
        this.station_name = station_name;
        this.station = station;

        dao = new SummaryDao();
    }


    @Override
    public void run() {

        if (elements == null) {
            return;
        }
        else {
            // get the current data from the Web Service
            station_summary = dao.getStationSummary(station_name);

            // null check is done inside this call
            elements.updateFromSummary(station_summary, station.getAvailableParameters());

            handler.postDelayed(this, 90000);
        }

    }
}
