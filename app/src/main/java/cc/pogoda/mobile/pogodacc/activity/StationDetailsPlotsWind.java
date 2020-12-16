package cc.pogoda.mobile.pogodacc.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.activity.handler.WindPlotClickEvent;
import cc.pogoda.mobile.pogodacc.dao.LastStationDataDao;
import cc.pogoda.mobile.pogodacc.type.WeatherStation;
import cc.pogoda.mobile.pogodacc.type.web.ListOfStationData;
import cc.pogoda.mobile.pogodacc.type.web.StationData;

public class StationDetailsPlotsWind extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private LineChart chart;
    private SeekBar seekBarX;
    private TextView tvX;

    private LastStationDataDao lastStationDataDao;
    private WeatherStation station;

    private WindPlotClickEvent plotClickEvent;

    private class ValueFormatter extends com.github.mikephil.charting.formatter.ValueFormatter {

        private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH);


        @Override
        public String getFormattedValue(float value) {
            Date date;

            long millis = (long) value;
            date = new Date(millis);
            return mFormat.format(date);
        }

    }

    public StationDetailsPlotsWind() {
        lastStationDataDao = new LastStationDataDao();
        plotClickEvent = new WindPlotClickEvent();

        //station = stationSystemName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_details_plots_wind);

        station = (WeatherStation) getIntent().getSerializableExtra("station");

        Typeface tfLight = Typeface.MONOSPACE;

        setTitle("LineChartTime");

        tvX = findViewById(R.id.tvXMax);
        seekBarX = findViewById(R.id.seekBar1);
        chart = findViewById(R.id.chart1);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        chart.setBackgroundColor(Color.WHITE);
        chart.setViewPortOffsets(0f, 0f, 0f, 0f);

        // add data
        seekBarX.setProgress(100);
        seekBarX.setOnSeekBarChangeListener(this);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setTypeface(tfLight);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f); // one hour
        xAxis.setValueFormatter(new ValueFormatter());

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTypeface(tfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0.0f);
        leftAxis.setAxisMaximum(24.0f);
        leftAxis.setYOffset(0.0f);
        leftAxis.setTextColor(Color.rgb(255, 192, 56));

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        this.setData();
    }

    private void setData() {
        ListOfStationData data = lastStationDataDao.getLastStationData(station.getSystemName());

        ArrayList<Entry> values_wind_speed = new ArrayList<>();

        if (data instanceof ListOfStationData) {
            for (StationData d : data.listOfStationData) {
                values_wind_speed.add(new Entry(d.epoch * 1000, d.windspeed));
            }

            // create a dataset and give it a type
            LineDataSet set1 = new LineDataSet(values_wind_speed, "Wind Speed");
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(ColorTemplate.getHoloBlue());
            set1.setValueTextColor(ColorTemplate.getHoloBlue());
            set1.setLineWidth(3.5f);
            set1.setDrawCircles(true);
            set1.setDrawValues(true);
            set1.setFillAlpha(65);
            set1.setFillColor(ColorTemplate.getHoloBlue());
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setDrawCircleHole(false);

            // create a data object with the data sets
            LineData line_data = new LineData(set1);
            line_data.setValueTextColor(Color.WHITE);
            line_data.setValueTextSize(9f);

            // set data
            chart.setData(line_data);
            chart.setDoubleTapToZoomEnabled(true);
            chart.setOnChartValueSelectedListener(plotClickEvent);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        tvX.setText(String.valueOf(seekBarX.getProgress()));

        //setData(seekBarX.getProgress(), 50);

        // redraw
        chart.invalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}