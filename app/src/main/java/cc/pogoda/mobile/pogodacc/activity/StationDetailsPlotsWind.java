package cc.pogoda.mobile.pogodacc.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

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
import org.threeten.bp.format.FormatStyle;

import java.util.ArrayList;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.activity.handler.PlotClickEvent;
import cc.pogoda.mobile.pogodacc.config.AppConfiguration;
import cc.pogoda.mobile.pogodacc.dao.LastStationDataDao;
import cc.pogoda.mobile.pogodacc.dao.StationDataDao;
import cc.pogoda.mobile.pogodacc.type.StationDetailsPlot;
import cc.pogoda.mobile.pogodacc.type.WeatherStation;
import cc.pogoda.mobile.pogodacc.type.web.ListOfStationData;
import cc.pogoda.mobile.pogodacc.type.web.StationData;

public class StationDetailsPlotsWind extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, StationDetailsPlot {

    private LineChart chart = null;
    private SeekBar seekBarX = null;
    private TextView textViewTimestamp = null;
    private TextView textViewSpeed = null;
    private TextView textViewGusts = null;

    private final LastStationDataDao lastStationDataDao;
    private final StationDataDao stationDataDao;
    private WeatherStation station;

    private PlotClickEvent plotClickEvent;

    private ArrayList<Entry> valuesWindSpeed;
    private ArrayList<Entry> valuesWindGusts;

    private int dataLn = -2;

    private static final int twelve_hours = 3600 * 12;
    private static final int twenty_four_hours = 3600 * 24;
    private static final int three_days = 3600 * 24 * 3;

    public ArrayList<Entry> getValuesWindSpeed() {
        return valuesWindSpeed;
    }

    private class ValueFormatter extends com.github.mikephil.charting.formatter.ValueFormatter {

        @Override
        public String getFormattedValue(float value) {

            long millis = (long) value;

            // the web service and the plot always stores the entries as UTC. So first convert epoch timestamp to the LocalDateTime
            LocalDateTime utcDateTime = LocalDateTime.ofEpochSecond(millis / 1000, 0, ZoneOffset.UTC);

            // and then shift to the user timezone for convinient display
            ZonedDateTime localDateTime = utcDateTime.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault());

            // format only the time to keep X axis clean
            String dt = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(localDateTime);

            return dt;
        }

    }

    public StationDetailsPlotsWind() {
        lastStationDataDao = new LastStationDataDao();
        stationDataDao = new StationDataDao();
        plotClickEvent = new PlotClickEvent(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int lastDataIndex = 0;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_details_plots);

        // get data length for this plot
        dataLn = (int)getIntent().getExtras().get("data_ln");

        station = (WeatherStation) getIntent().getSerializableExtra("station");

        // download data from web service
        this.downloadDataFromWebservice();

        Typeface tfLight = Typeface.MONOSPACE;

        setTitle(R.string.wind_speed_plots);

        textViewTimestamp = findViewById(R.id.textViewPlotsWindTimestamp);
        textViewSpeed = findViewById(R.id.textViewPlotsWindMean);
        textViewGusts = findViewById(R.id.textViewPlotsWindGusts);
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
        leftAxis.setAxisMaximum(this.findMaxValueForPlotScale());
        leftAxis.setYOffset(0.0f);
        leftAxis.setTextColor(Color.rgb(255, 192, 56));

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        lastDataIndex = valuesWindSpeed.size() - 1;

        // display only the last data (20% of newest data)
        this.setData((long) (0.8 * (lastDataIndex)), lastDataIndex, false);

        // set bar to maximum value
        seekBarX.setProgress(100);
    }

    /**
     * This method looks through 'valuesWindGusts' for the maximum value
     * @return
     */
    protected float findMaxValueForPlotScale() {
        float out = 0.0f;

        for (Entry e : valuesWindGusts) {
            if (e.getY() > out) {
                out = e.getY();
            }
        }

        out = (float) Math.ceil(out * 1.3f);

        return out;
    }

    /**
     * Downloads the data from the web service and stores it as entries ready to be displayed on the
     * plot. Web Service gives the data with the epoch timestamp in second resolution, but the plot
     * shows the data in millisecond resolution
     */
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

        valuesWindSpeed = new ArrayList<>();
        valuesWindGusts = new ArrayList<>();

        if (data instanceof  ListOfStationData) {
            for (StationData d : data.listOfStationData) {
                valuesWindSpeed.add(new Entry(d.epoch * 1000, d.windspeed));
                valuesWindGusts.add(new Entry(d.epoch * 1000, d.windgusts));
            }
        }

    }

    /**
     * Updates labels placed at the top of the chart with values at the point selected by an user.
     * @param date  Date & time string in local timezone
     */
    public void updateLabels(String date, Entry entry) {
        float mean = 0.0f;
        float gusts = 0.0f;

        String unit;

        if (AppConfiguration.replaceMsWithKnots) {
            unit = getString(R.string.knots);
        }
        else {
            unit = getString(R.string.meters_per_second);
        }

        // get a timestamp from the entry
        long timestamp = (long) entry.getX();

        // look for the windspeed coresponding to that timestamp
        for (Entry e : valuesWindSpeed) {
            // if this is what we are looking for
            if (e.getX() == entry.getX()) {
                mean = e.getY();
            }
        }

        for (Entry e : valuesWindGusts) {

            if (e.getX() == entry.getX()) {
                gusts = e.getY();
            }
        }

        if (this.textViewGusts != null && this.textViewSpeed != null && this.textViewTimestamp != null) {
            this.textViewTimestamp.setText(date);
            this.textViewSpeed.setText(getString(R.string.mean_value_short) + String.format(": %.1f%s", mean, unit));
            this.textViewGusts.setText(getString(R.string.wind_gust_short) + String.format(": %.1f%s", gusts, unit));
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
        LineDataSet set_windspeed, set_windgusts;


        if (valuesWindSpeed.size() > 0) {

            if (from != 0 || to != 0) {

                // if 'from' and 'to' are the index values
                if (!index_or_timestamp) {
                    // make a sublist
                    narrowed_set = new ArrayList<>(valuesWindSpeed.subList((int)from, (int)to));
                    narrowed_set_gusts = new ArrayList<>(valuesWindGusts.subList((int)from, (int)to));

                }
                else {
                    // get first and last entry from the set
                    Entry first = valuesWindSpeed.get(0);
                    Entry last = valuesWindSpeed.get(valuesWindSpeed.size() - 1 );

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
                        valuesWindSpeed.forEach((Entry e) -> {
                            if (e.getX() > (from * 1000) &&
                                e.getX() < (to * 1000)) {
                                    narrowed_set.add(e);
                            }
                        });

                        valuesWindGusts.forEach((Entry e) -> {
                            if (e.getX() > (from * 1000) &&
                                    e.getX() < (to * 1000)) {
                                narrowed_set_gusts.add(e);
                            }
                        });


                    }

                }
                // and generate the set from it
                set_windspeed = new LineDataSet(narrowed_set, "Wind Speed");
                set_windgusts = new LineDataSet(narrowed_set_gusts, "Wind Gusts");
            }
            else {
                // use 'values_wind_speed' directly as a whole
                set_windspeed = new LineDataSet(valuesWindSpeed, "Wind Speed");
                set_windgusts = new LineDataSet(valuesWindGusts, "Wind Gusts");
            }

            // create a dataset and give it a type
            set_windspeed.setAxisDependency(YAxis.AxisDependency.LEFT);
            set_windspeed.setColor(ColorTemplate.getHoloBlue());
            set_windspeed.setValueTextColor(ColorTemplate.getHoloBlue());
            set_windspeed.setLineWidth(3.5f);
            set_windspeed.setDrawCircles(true);
            set_windspeed.setDrawValues(true);
            set_windspeed.setFillAlpha(65);
            set_windspeed.setFillColor(ColorTemplate.getHoloBlue());
            set_windspeed.setHighLightColor(Color.rgb(244, 117, 117));
            set_windspeed.setDrawCircleHole(false);

            set_windgusts.setAxisDependency(YAxis.AxisDependency.LEFT);
            set_windgusts.setColor(ColorTemplate.rgb("#C70039"));
            set_windgusts.setCircleColor(ColorTemplate.rgb("#C70039"));
            set_windgusts.setValueTextColor(ColorTemplate.getHoloBlue());
            set_windgusts.setLineWidth(3.5f);
            set_windgusts.setDrawCircles(true);
            set_windgusts.setDrawValues(true);
            set_windgusts.setFillAlpha(65);
            set_windgusts.setFillColor(ColorTemplate.rgb("#C70039"));
            set_windgusts.setHighLightColor(ColorTemplate.rgb("#C70039"));
            set_windgusts.setDrawCircleHole(false);

            // create a data object with the data sets
            LineData line_data = new LineData();
            line_data.addDataSet(set_windspeed);
            line_data.addDataSet(set_windgusts);
            line_data.setValueTextColor(Color.WHITE);
            line_data.setValueTextSize(9f);

            // set data
            chart.setData(line_data);
            chart.setDoubleTapToZoomEnabled(false);
            chart.setOnChartValueSelectedListener(plotClickEvent);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }

    /**
     * Called when user changes the value of seek bar
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        // first and last index to display on plot
        int first_index = 0, last_index = 0;

        // display only 20% of the set at once
        int window_size = (int) (valuesWindSpeed.size() * 0.2f);

        last_index =  (int) ((seekBarX.getProgress() / 100.0f) * valuesWindSpeed.size());
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
}