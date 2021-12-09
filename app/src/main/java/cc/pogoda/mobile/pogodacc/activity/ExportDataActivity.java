package cc.pogoda.mobile.pogodacc.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.UriPermission;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.dao.StationDataDao;
import cc.pogoda.mobile.pogodacc.file.CsvExport;
import cc.pogoda.mobile.pogodacc.file.ExcelExport;
import cc.pogoda.mobile.pogodacc.type.ParceableFavsCallReason;
import cc.pogoda.mobile.pogodacc.type.ParceableStationsList;
import cc.pogoda.mobile.pogodacc.type.WeatherStation;
import cc.pogoda.mobile.pogodacc.type.web.ListOfStationData;

public class ExportDataActivity extends AppCompatActivity {

    private Spinner formatSpinner;

    private Button selectStationButton;

    private Button startExportButton, outputFileButton;

    private Spinner outputFormat;

    ExportDataActivity act;

    TextView stationNameToExport;

    EditText exportLn;

    DatePicker datePicker;

    WeatherStation stationToExport = null;

    Uri exportUri = null;

    public WeatherStation getStationToExport() {
        return stationToExport;
    }

    public void openDirectory(String fn, int outputFormat) {

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (outputFormat == 1) {
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_TITLE, fn + ".csv");

        }
        if (outputFormat == 2) {
            intent.setType("application/vnd.ms-excel");
            intent.putExtra(Intent.EXTRA_TITLE, fn + ".xls");
        }

        startActivityForResult(intent, 123);

    }

    public int getExportLnInHours() {
        int out = 0;

        Editable text = exportLn.getText();

        try {
            Integer ln = Integer.valueOf(text.toString());

            if (ln > 199) {
                out = 199;
            }
            else {
                out = ln;
            }
        }
        catch (NumberFormatException ee) {
            out = -1;
        }

        return out;
    }

    public long getStartTimestamp() {
        long out = 0;

        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();

        ZonedDateTime dateTime = ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneId.systemDefault());

        ZonedDateTime utc = dateTime.withZoneSameInstant(ZoneId.of("UTC"));

        out = utc.toEpochSecond();

        return out;
    }

    public int getOutputFormat() {

        String selected = outputFormat.getSelectedItem().toString();

        switch (selected) {
            case "CSV" : return 1;
            case "MS Excel XLS": return 2;
        }

        return -1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 123 && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            exportUri = uri;

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_data);

        EventBus.getDefault().register(this);

        act = this;

        ParceableStationsList favs = getIntent().getParcelableExtra("favs");

        formatSpinner = findViewById(R.id.spinnerOutputFormat);
        selectStationButton = findViewById(R.id.buttonSelectStationExport);
        stationNameToExport = findViewById(R.id.textViewStationToExport);
        startExportButton = findViewById(R.id.buttonExportStart);
        exportLn = findViewById(R.id.editTextNumberExport);
        datePicker = findViewById(R.id.datePickerExportStartDate);
        outputFormat = findViewById(R.id.spinnerOutputFormat);
        outputFileButton = findViewById(R.id.buttonExportTarget);

        outputFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fn;

                if (stationToExport != null) {
                    fn = stationToExport.getSystemName() + "_" + datePicker.getDayOfMonth() + "-" + String.format("%02d", datePicker.getMonth()) + "-" + datePicker.getYear() +"_" + act.getExportLnInHours() + "hrs";
                }
                else {
                    fn = "export";
                }

                openDirectory(fn, act.getOutputFormat());

            }
        });

        selectStationButton.setOnClickListener(new View.OnClickListener() {

            Intent intent;

            @Override
            public void onClick(View view) {
                intent = new Intent(act, FavouritesActivity.class);
                intent.putExtra("favs", favs);

                ParceableFavsCallReason callReason = new ParceableFavsCallReason(ParceableFavsCallReason.Reason.EXPORT_SELECT);
                intent.putExtra("callReason", callReason);

                act.startActivity(intent);
            }
        });

        startExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StationDataDao stationDataDao;

                WeatherStation toExport = act.getStationToExport();

                if (toExport != null && exportUri != null) {
                    long timestampStart = act.getStartTimestamp();
                    long timestampStop = timestampStart + act.getExportLnInHours() * 3600;

                    stationDataDao = new StationDataDao();

                    ListOfStationData stationData = stationDataDao.getLastStationData(toExport.getSystemName(), timestampStart, timestampStop);

                    int format = act.getOutputFormat();

                    if (format == 2) {
                        try {
                            if (ExcelExport.exportToExcel(stationData, toExport, act.getApplicationContext(), getContentResolver().openOutputStream(exportUri))) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(act);
                                builder.setMessage(R.string.success);
                                builder.setPositiveButton(R.string.ok, (DialogInterface var1, int var2) -> {
                                    var1.dismiss();
                                });
                                builder.create();
                                builder.show();
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(act);
                                builder.setMessage(R.string.failure);
                                builder.setPositiveButton(R.string.ok, (DialogInterface var1, int var2) -> {
                                    var1.dismiss();
                                });
                                builder.create();
                                builder.show();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }

                    else if (format == 1) {
                        try {
                            if (CsvExport.exportToCsv(stationData, toExport, act.getApplicationContext(), getContentResolver().openOutputStream(exportUri))) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(act);
                                builder.setMessage(R.string.success);
                                builder.setPositiveButton(R.string.ok, (DialogInterface var1, int var2) -> {
                                    var1.dismiss();
                                });
                                builder.create();
                                builder.show();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }


                }
                else if (toExport == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setMessage(R.string.select_station_export);
                    builder.setPositiveButton(R.string.ok, (DialogInterface var1, int var2) -> {
                        var1.dismiss();
                    });
                    builder.create();
                    builder.show();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setMessage(R.string.select_output_file);
                    builder.setPositiveButton(R.string.ok, (DialogInterface var1, int var2) -> {
                        var1.dismiss();
                    });
                    builder.create();
                    builder.show();
                }
            }
        });

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.export_formats, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        formatSpinner.setAdapter(adapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    @Subscribe(threadMode =  ThreadMode.MAIN)
    public void stationToExportEvent(WeatherStation wx) {
        stationNameToExport.setText(wx.getDisplayedName());

        stationToExport = wx;
    }
}