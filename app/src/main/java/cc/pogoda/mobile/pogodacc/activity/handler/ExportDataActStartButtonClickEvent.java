package cc.pogoda.mobile.pogodacc.activity.handler;

import android.view.View;

import cc.pogoda.mobile.pogodacc.activity.ExportDataActivity;
import cc.pogoda.mobile.pogodacc.dao.StationDataDao;
import cc.pogoda.mobile.pogodacc.type.WeatherStation;
import cc.pogoda.mobile.pogodacc.type.web.ListOfStationData;

public class ExportDataActStartButtonClickEvent implements View.OnClickListener{

    ExportDataActivity activity;

    public ExportDataActStartButtonClickEvent(ExportDataActivity act) {
        activity = act;
    }

    @Override
    public void onClick(View view) {

        StationDataDao stationDataDao;

        WeatherStation toExport = activity.getStationToExport();

        if (toExport != null) {
            long timestampStart = activity.getStartTimestamp();
            long timestampStop = timestampStart + activity.getExportLnInHours() * 3600;

            stationDataDao = new StationDataDao();

            ListOfStationData stationData = stationDataDao.getLastStationData(toExport.getSystemName(), timestampStart, timestampStop);

        }

    }
}
