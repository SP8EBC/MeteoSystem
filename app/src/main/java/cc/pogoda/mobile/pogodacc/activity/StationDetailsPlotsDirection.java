package cc.pogoda.mobile.pogodacc.activity;

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
import org.threeten.bp.format.FormatStyle;

import java.util.ArrayList;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.activity.handler.PlotClickEvent;
import cc.pogoda.mobile.pogodacc.dao.LastStationDataDao;
import cc.pogoda.mobile.pogodacc.dao.StationDataDao;
import cc.pogoda.mobile.pogodacc.type.StationDetailsPlot;
import cc.pogoda.mobile.pogodacc.type.WeatherStation;
import cc.pogoda.mobile.pogodacc.type.web.ListOfStationData;
import cc.pogoda.mobile.pogodacc.type.web.StationData;

public class StationDetailsPlotsDirection extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, StationDetailsPlot {

    private LineChart chart = null;
    private SeekBar seekBarX = null;
    private TextView textViewTimestamp = null;
    private TextView textViewSpeed = null;
    private TextView textViewGusts = null;

    private final LastStationDataDao lastStationDataDao;
    private final StationDataDao stationDataDao;
    private WeatherStation station;

    private PlotClickEvent plotClickEvent;

    private ArrayList<Entry> valuesWindDirection;

    private int dataLn = -2;

    private static final int twelve_hours = 3600 * 12;
    private static final int twenty_four_hours = 3600 * 24;
    private static final int three_days = 3600 * 24 * 3;


    private static class ValueFormatter extends com.github.mikephil.charting.formatter.ValueFormatter {

        @Override
        public String getFormattedValue(float value) {

            long millis = (long) value;

            // the web service and the plot always stores the entries as UTC. So first convert epoch timestamp to the LocalDateTime
            LocalDateTime utcDateTime = LocalDateTime.ofEpochSecond(millis / 1000, 0, ZoneOffset.UTC);

            // and then shift to the user timezone for convinient display
            ZonedDateTime localDateTime = utcDateTime.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault());

            /* format only the time to keep X axis clean */
            return DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(localDateTime);

            //return dt;
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        // first and last index to display on plot
        int first_index, last_index = 0;

        // display only 20% of the set at once
        int window_size = (int) (valuesWindDirection.size() * 0.2f);

        last_index =  (int) ((seekBarX.getProgress() / 100.0f) * valuesWindDirection.size());
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // get data length for this plot
        dataLn = (int)getIntent().getExtras().get("data_ln");

        setContentView(R.layout.activity_station_details_plots);

        station = (WeatherStation) getIntent().getSerializableExtra("station");

        // download data from web service
        this.downloadDataFromWebservice();

        Typeface tfLight = Typeface.MONOSPACE;

        setTitle(R.string.wind_direction_plots);

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
        xAxis.setValueFormatter(new StationDetailsPlotsDirection.ValueFormatter());

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTypeface(tfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0.0f);
        leftAxis.setAxisMaximum(360.0f);
        leftAxis.setYOffset(0.0f);
        leftAxis.setTextColor(Color.rgb(255, 192, 56));

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        int lastDataIndex = valuesWindDirection.size() - 1;

        // display only the last data (20% of newest data)
        this.setData((long) (0.8 * (lastDataIndex)), lastDataIndex, false);

        // set bar to maximum value
        seekBarX.setProgress(100);
    }

    /**
     * Updates labels placed at the top of the chart with values at the point selected by an user.
     * @param date  Date & time string in local timezone
     */
    public void updateLabels(String date, Entry entry) {
        float direction = 0.0f;

        // get a timestamp from the entry
        long timestamp = (long) entry.getX();

        // look for the windspeed coresponding to that timestamp
        for (Entry e : valuesWindDirection) {
            // if this is what we are looking for
            if (e.getX() == entry.getX()) {
                direction = e.getY();
            }
        }


        if (this.textViewSpeed != null && this.textViewTimestamp != null) {
            this.textViewTimestamp.setText(date);
            this.textViewSpeed.setText(getString(R.string.wind_direction_short) + String.format(": %d", (int)direction));
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

        // data set to be displayed on the plot
        LineDataSet set_wind_direction;


        if (valuesWindDirection.size() > 0) {

            if (from != 0 || to != 0) {

                // if 'from' and 'to' are the index values
                if (!index_or_timestamp) {
                    // make a sublist
                    narrowed_set = new ArrayList<>(valuesWindDirection.subList((int)from, (int)to));

                }
                else {
                    // get first and last entry from the set
                    Entry first = valuesWindDirection.get(0);
                    Entry last = valuesWindDirection.get(valuesWindDirection.size() - 1 );

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
                        valuesWindDirection.forEach((Entry e) -> {
                            if (e.getX() > (from * 1000) &&
                                    e.getX() < (to * 1000)) {
                                narrowed_set.add(e);
                            }
                        });

                    }

                }
                // and generate the set from it
                set_wind_direction = new LineDataSet(narrowed_set, "Wind Speed");
            }
            else {
                // use 'values_wind_speed' directly as a whole
                set_wind_direction = new LineDataSet(valuesWindDirection, "Wind Speed");
            }

            // create a dataset and give it a type
            set_wind_direction.setAxisDependency(YAxis.AxisDependency.LEFT);
            set_wind_direction.setColor(ColorTemplate.getHoloBlue());
            set_wind_direction.setValueTextColor(ColorTemplate.getHoloBlue());
            set_wind_direction.setLineWidth(3.5f);
            set_wind_direction.setDrawCircles(true);
            set_wind_direction.setDrawValues(true);
            set_wind_direction.setFillAlpha(65);
            set_wind_direction.setFillColor(ColorTemplate.getHoloBlue());
            set_wind_direction.setHighLightColor(Color.rgb(244, 117, 117));
            set_wind_direction.setDrawCircleHole(false);

            // create a data object with the data sets
            LineData line_data = new LineData();
            line_data.addDataSet(set_wind_direction);
            line_data.setValueTextColor(Color.WHITE);
            line_data.setValueTextSize(9f);

            // set data
            chart.setData(line_data);
            chart.setDoubleTapToZoomEnabled(false);
            chart.setOnChartValueSelectedListener(plotClickEvent);
        }

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

        valuesWindDirection = new ArrayList<>();

        if (data != null) {
            for (StationData d : data.list_of_station_data) {
                valuesWindDirection.add(new Entry(d.epoch * 1000, d.winddir));
            }
        }

    }

    public StationDetailsPlotsDirection() {
        lastStationDataDao = new LastStationDataDao();
        stationDataDao = new StationDataDao();
        plotClickEvent = new PlotClickEvent(this);

    }
}
