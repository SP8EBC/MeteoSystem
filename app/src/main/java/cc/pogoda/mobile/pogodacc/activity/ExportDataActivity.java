package cc.pogoda.mobile.pogodacc.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
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

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.activity.handler.ExportDataActStartButtonClickEvent;
import cc.pogoda.mobile.pogodacc.type.ParceableFavsCallReason;
import cc.pogoda.mobile.pogodacc.type.ParceableStationsList;
import cc.pogoda.mobile.pogodacc.type.WeatherStation;

public class ExportDataActivity extends AppCompatActivity {

    private Spinner formatSpinner;

    private Button selectStationButton;

    private Button startExportButton;

    private Spinner outputFormat;

    AppCompatActivity act;

    TextView stationNameToExport;

    EditText exportLn;

    DatePicker datePicker;

    WeatherStation stationToExport = null;

    ExportDataActStartButtonClickEvent exportEvent;

    public WeatherStation getStationToExport() {
        return stationToExport;
    }

    public int getExportLnInHours() {
        int out = 0;

        Editable text = exportLn.getText();

        try {
            Integer ln = Integer.valueOf(text.toString());

            if (ln > 100) {
                out = 100;
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
            case "MS Excel XLSX": return 2;
        }

        return -1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        String s;

        if (requestCode == 123 && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            s = uri.getPath();

            FileUtils.

            System.out.println(s);
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

        startExportButton.setOnClickListener(new ExportDataActStartButtonClickEvent(this));

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