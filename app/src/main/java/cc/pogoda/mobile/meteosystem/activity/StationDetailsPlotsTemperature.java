package cc.pogoda.mobile.meteosystem.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;

import cc.pogoda.mobile.meteosystem.R;
import cc.pogoda.mobile.meteosystem.activity.handler.PlotClickEvent;
import cc.pogoda.mobile.meteosystem.dao.LastStationDataDao;
import cc.pogoda.mobile.meteosystem.dao.StationDataDao;
import cc.pogoda.mobile.meteosystem.type.StationDetailsPlot;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;
import cc.pogoda.mobile.meteosystem.type.web.ListOfStationData;
import cc.pogoda.mobile.meteosystem.type.web.StationData;

public class StationDetailsPlotsTemperature extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, StationDetailsPlot {

    WeatherStation station = null;

    private LineChart chart = null;
    private SeekBar seekBarX = null;
    private TextView textViewTimestamp = null;
    private TextView textViewSpeed = null;

    private ArrayList<Entry> valuesTemperature;

    private final LastStationDataDao lastStationDataDao;
    private final StationDataDao stationDataDao;

    PlotClickEvent clickEvent;

    private int dataLn = -2;

    private static final int twelve_hours = 3600 * 12;
    private static final int twenty_four_hours = 3600 * 24;
    private static final int three_days = 3600 * 24 * 3;

    private class ValueFormatter extends com.github.mikephil.charting.formatter.ValueFormatter {

        @Override
        public String getFormattedValue(float value) {

            long millis = (long) value;

            // the web service and the plot always stores the entries as UTC. So first convert epoch timestamp to the LocalDateTime
            LocalDateTime utcDateTime = LocalDateTime.ofEpochSecond(millis / 1000, 0, ZoneOffset.UTC);

            // and then shift to the user timezone for convinient display
            ZonedDateTime localDateTime = utcDateTime.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault());

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");

            /* format only the time to keep X axis clean */
            return fmt.format(localDateTime);
        }

    }

    public StationDetailsPlotsTemperature() {
        valuesTemperature = new ArrayList<Entry>();
        lastStationDataDao = new LastStationDataDao();
        stationDataDao = new StationDataDao();
    }

    private void downloadDataFromWebservice() {
        ListOfStationData data = null;

        // utc time
        ZonedDateTime utcTime = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

        // utc timestamp
        long utcTimestamp = utcTime.toEpochSecond();

        if (this.dataLn < 0 || this.dataLn > 2) {
            // last 2000 points of data, regardless the timescale
            data = lastStationDataDao.getLastStationData(station.getSystemName());
        }
        else if (dataLn == 0) {
            // 12 hours
            data = stationDataDao.getLastStationData(station.getSystemName(), utcTimestamp - twelve_hours, utcTimestamp);
        }
        else if (dataLn == 1) {
            // 24 hours
            data = stationDataDao.getLastStationData(station.getSystemName(), utcTimestamp - twenty_four_hours, utcTimestamp);
        }
        else if (dataLn == 2) {
            // 3 days
            data = stationDataDao.getLastStationData(station.getSystemName(), utcTimestamp - three_days, utcTimestamp);
        }

        if (data instanceof  ListOfStationData) {
            for (StationData d : data.list_of_station_data) {
                valuesTemperature.add(new Entry(d.epoch * 1000, d.temperature));
            }
        }

    }

    /**
     * Updates labels placed at the top of the chart with values at the point selected by an user.
     * @param date  Date & time string in local timezone
     */
    public void updateLabels(String date, Entry entry) {
        float temperature = 0.0f;
        // get a timestamp from the entry
        long timestamp = (long) entry.getX();

        // look for the windspeed coresponding to that timestamp
        for (Entry e : valuesTemperature) {
            // if this is what we are looking for
            if (e.getX() == entry.getX()) {
                temperature = e.getY();
            }
        }

        // use wind speed label (on the left) to display the temperature
        if (this.textViewSpeed != null && this.textViewTimestamp != null) {
            this.textViewTimestamp.setText(date);
            this.textViewSpeed.setText(getString(R.string.temperature) + String.format(": %.1f°C", temperature).replace(',', '.'));
        }
        else {
            return;
        }
    }

    /**
     *
     * @param from
     * @param to
     * @param index_or_timestamp if set to false 'to' and 'from' are treated as an index, if they are set
     *                           to true this method will use it as epoch timestamps (in seconds)
     */
    public void setData(long from, long to, boolean index_or_timestamp) {

        // if only some part of input set needs to be displayed use this intermediate buffer
        ArrayList<Entry> narrowed_set, narrowed_set_gusts;

        // data set to be
        LineDataSet set_temperature;


        if (valuesTemperature.size() > 0) {

            if (from != 0 || to != 0) {

                // if 'from' and 'to' are the index values
                if (!index_or_timestamp) {
                    // make a sublist
                    narrowed_set = new ArrayList<>(valuesTemperature.subList((int)from, (int)to));

                }
                else {
                    // get first and last entry from the set
                    Entry first = valuesTemperature.get(0);
                    Entry last = valuesTemperature.get(valuesTemperature.size() - 1 );

                    // check if 'from' and 'to' timestamp epoch covers any data from the input set
                    if (    (long)first.getX() > (to * 1000) ||
                            (long)last.getX() < (from * 1000)) {

                        // if there is no data to display exit from an function
                        return;
                    }
                    else {
                        narrowed_set = new ArrayList<>();
                        narrowed_set_gusts = new ArrayList<>();

                        // if not copy matching elements to narrowed set
                        valuesTemperature.forEach((Entry e) -> {
                            if (e.getX() > (from * 1000) &&
                                    e.getX() < (to * 1000)) {
                                narrowed_set.add(e);
                            }
                        });


                    }

                }
                // and generate the set from it
                set_temperature = new LineDataSet(narrowed_set, getString(R.string.temperature));
            }
            else {
                // use 'values_wind_speed' directly as a whole
                set_temperature = new LineDataSet(valuesTemperature, getString(R.string.temperature));
            }

            // create a dataset and give it a type
            set_temperature.setMode(LineDataSet.Mode.CUBIC_BEZIER); // filled area instead of only line with points
            set_temperature.setCubicIntensity(0.2f);
            set_temperature.setDrawFilled(true);
            set_temperature.setAxisDependency(YAxis.AxisDependency.LEFT);
            set_temperature.setColor(ColorTemplate.getHoloBlue());
            set_temperature.setValueTextColor(ColorTemplate.getHoloBlue());
            set_temperature.setLineWidth(3.5f);
            set_temperature.setDrawCircles(false);
            set_temperature.setDrawValues(true);
            set_temperature.setFillAlpha(65);
            set_temperature.setFillColor(ColorTemplate.getHoloBlue());
            set_temperature.setHighLightColor(Color.rgb(244, 117, 117));
            set_temperature.setDrawCircleHole(false);
            set_temperature.setFillFormatter((dataSet, dataProvider) -> 0.0f);  // set this to draw an area between
                                                                                // the line and zero axis


            // create a data object with the data sets
            LineData line_data = new LineData();
            line_data.addDataSet(set_temperature);
            line_data.setValueTextColor(Color.WHITE);
            line_data.setValueTextSize(9f);

            // set data
            chart.setData(line_data);
            chart.setDoubleTapToZoomEnabled(false);
            chart.setOnChartValueSelectedListener(clickEvent);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // this activity layout is common for all plots
        setContentView(R.layout.activity_station_details_plots);

        // get data length for this plot
        dataLn = (int)getIntent().getExtras().get("data_ln");

        station = (WeatherStation) getIntent().getSerializableExtra("station");

        // exit from the function if station object hasn't been added to the intent
        if (station == null) {
            return;
        }

        // create click event class
        clickEvent = new PlotClickEvent(this);

        this.downloadDataFromWebservice();

        // window title
        setTitle(R.string.wind_direction_plots);

        textViewTimestamp = findViewById(R.id.textViewPlotsWindTimestamp);
        textViewSpeed = findViewById(R.id.textViewPlotsWindMean);
        seekBarX = findViewById(R.id.seekBarPlotsWind);
        chart = findViewById(R.id.chartPlotsWind);

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
        xAxis.setTypeface(Typeface.MONOSPACE);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(R.color.design_default_color_primary_dark);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f); // one hour
        xAxis.setValueFormatter(new StationDetailsPlotsTemperature.ValueFormatter());
        xAxis.setTextSize(123.0f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setLabelRotationAngle(45.0f);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTypeface(Typeface.MONOSPACE);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(-30.0f);
        leftAxis.setAxisMaximum(40.0f);
        leftAxis.setYOffset(0.0f);
        leftAxis.setTextColor(R.color.design_default_color_primary_dark);
        leftAxis.setTextSize(123.0f);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        int lastDataIndex = valuesTemperature.size() - 1;

        // display only the last data (20% of newest data)
        this.setData((long) (0.8 * (lastDataIndex)), lastDataIndex, false);

        // set bar to maximum value
        seekBarX.setProgress(100);


        //lastDataIndex = valuesWindSpeed.size() - 1;

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        // first and last index to display on plot
        int first_index = 0, last_index = 0;

        // display only 20% of the set at once
        int window_size = (int) (valuesTemperature.size() * 0.2f);

        last_index =  (int) ((seekBarX.getProgress() / 100.0f) * valuesTemperature.size());
        first_index = last_index - window_size;

        if (first_index < 0) {
            first_index = 0;
            last_index = window_size;
        }

        this.setData(first_index, last_index, false);

        // redraw
        chart.invalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
