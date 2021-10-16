package cc.pogoda.mobile.pogodacc.activity.handler;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import cc.pogoda.mobile.pogodacc.activity.ExportDataActivity;
import cc.pogoda.mobile.pogodacc.dao.StationDataDao;
import cc.pogoda.mobile.pogodacc.file.ExcelExport;
import cc.pogoda.mobile.pogodacc.type.WeatherStation;
import cc.pogoda.mobile.pogodacc.type.web.ListOfStationData;

public class ExportDataActStartButtonClickEvent implements View.OnClickListener{

    ExportDataActivity activity;

    public ExportDataActStartButtonClickEvent(ExportDataActivity act) {
        activity = act;
    }

    public void openDirectory(Uri uriToLoad) {
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uriToLoad);

        activity.startActivityForResult(intent, 123);
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

            int format = activity.getOutputFormat();

            openDirectory(null);

            if (format == 2) {
                ExcelExport.exportToExcel(stationData, toExport, activity.getApplicationContext());

            }


        }

    }
}
