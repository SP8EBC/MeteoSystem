package cc.pogoda.mobile.pogodacc.activity.updater;

import android.os.Handler;

import cc.pogoda.mobile.pogodacc.dao.SummaryDao;
import cc.pogoda.mobile.pogodacc.type.StationSummaryActElements;
import cc.pogoda.mobile.pogodacc.type.web.Summary;

public class StationDetailsSummaryValUpdater implements Runnable {

    StationSummaryActElements elements = null;

    Handler handler = null;

    SummaryDao dao = null;

    Summary station_summary = null;

    String station_name;

    public  StationDetailsSummaryValUpdater(StationSummaryActElements elems, Handler h, String s) {
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
            station_summary = dao.getStationSummary(station_name);

            elements.updateFromSummary(station_summary);

            handler.postDelayed(this, 90000);
        }

    }
}
