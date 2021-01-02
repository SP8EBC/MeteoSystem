package cc.pogoda.mobile.pogodacc.activity.updater;

import android.os.Handler;

import cc.pogoda.mobile.pogodacc.dao.SummaryDao;
import cc.pogoda.mobile.pogodacc.type.StationActivityElements;
import cc.pogoda.mobile.pogodacc.type.StationSummaryActElements;
import cc.pogoda.mobile.pogodacc.type.web.Summary;



/**
 * Class used to update the content of Wind Rose Activity
 */
public class StationDetailsValuesUpdater implements Runnable {

    StationActivityElements elements = null;

    Handler handler = null;

    SummaryDao dao = null;

    Summary station_summary = null;

    String station_name;

    public StationDetailsValuesUpdater(StationActivityElements elems, Handler h, String s) {
        elements = elems;
        handler = h;
        station_name = s;

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
            elements.updateFromSummary(station_summary);

            handler.postDelayed(this, 90000);
        }

    }
}
