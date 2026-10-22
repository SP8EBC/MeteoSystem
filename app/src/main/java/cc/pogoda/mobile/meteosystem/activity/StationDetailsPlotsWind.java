package cc.pogoda.mobile.meteosystem.activity;


import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
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
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.Locale;

import cc.pogoda.mobile.meteosystem.R;
import cc.pogoda.mobile.meteosystem.activity.plot.CrossLineDataSet;
import cc.pogoda.mobile.meteosystem.activity.plot.CrossMarkerLineChartRenderer;
import cc.pogoda.mobile.meteosystem.activity.plot.HourlyXAxisRenderer;
import cc.pogoda.mobile.meteosystem.activity.handler.PlotClickEvent;
import cc.pogoda.mobile.meteosystem.config.AppConfiguration;
import cc.pogoda.mobile.meteosystem.dao.LastStationDataDao;
import cc.pogoda.mobile.meteosystem.dao.StationDataDao;
import cc.pogoda.mobile.meteosystem.type.StationDetailsPlot;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;
import cc.pogoda.mobile.meteosystem.type.web.ListOfStationData;
import cc.pogoda.mobile.meteosystem.type.web.StationData;

public class StationDetailsPlotsWind extends ToolbarActivity implements SeekBar.OnSeekBarChangeListener, StationDetailsPlot {

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

    // exact epoch timestamps (in seconds) of the measurements, one per entry in 'valuesWindSpeed'.
    // X values of the entries are floats and in milliseconds, so they are only accurate to about two minutes
    private ArrayList<Long> valuesEpoch;

    // moving average of 'valuesWindSpeed' and 'valuesWindGusts', one entry per each measurement
    private ArrayList<Entry> trendWindSpeed;
    private ArrayList<Entry> trendWindGusts;

    // width of the time window used to calculate the trend, centered on each measurement
    private static final long TREND_WINDOW_MILLIS = 3600 * 1000;

    private int dataLn = -2;

    private static final int twelve_hours = 3600 * 12;
    private static final int twenty_four_hours = 3600 * 24;
    private static final int three_days = 3600 * 24 * 3;

    public ArrayList<Entry> getValuesWindSpeed() {
        return valuesWindSpeed;
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

        // get data length for this plot
        dataLn = (int)getIntent().getExtras().get("data_ln");

        station = (WeatherStation) getIntent().getSerializableExtra("station");

        Logger.info("[station.getSystemName() = " + station.getSystemName() +"][dataLn = " + dataLn +"]");

        if (AppConfiguration.locale != null && !AppConfiguration.locale.equals("default") ) {
            Logger.debug("[AppConfiguration.locale = " + AppConfiguration.locale +  "]");
            Locale locale = new Locale(AppConfiguration.locale);
            Locale.setDefault(locale);
            Resources resources = this.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }

        setContentView(R.layout.activity_station_details_plots);

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
        xAxis.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(R.color.design_default_color_primary_dark);
        xAxis.setTextSize(16.0f);    // library limits text size to 24dp
        xAxis.setLabelRotationAngle(0.0f);

        // major steps at the top of each hour
        HourlyXAxisRenderer.install(chart, true);

        // draws wind gusts as small crosses
        CrossMarkerLineChartRenderer.install(chart);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTypeface(tfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0.0f);
        leftAxis.setAxisMaximum(this.findMaxValueForPlotScale());
        leftAxis.setYOffset(0.0f);
        leftAxis.setTextColor(R.color.design_default_color_primary_dark);
        leftAxis.setTextSize(123.0f);

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

        Logger.debug("[station.getSystemName() = " + station.getSystemName() +"]");

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
        valuesEpoch = new ArrayList<>();

        if (data instanceof  ListOfStationData) {

            Logger.debug("[data.list_of_station_data.length = " + data.list_of_station_data.length +"]");

            for (StationData d : data.list_of_station_data) {
                valuesWindSpeed.add(new Entry(d.epoch * 1000, d.windspeed));
                valuesWindGusts.add(new Entry(d.epoch * 1000, d.windgusts));
                valuesEpoch.add(d.epoch);
            }
        }

        trendWindSpeed = calculateTrend(valuesWindSpeed);
        trendWindGusts = calculateTrend(valuesWindGusts);
    }

    /**
     * Calculates the trend as a moving average over 'TREND_WINDOW_MILLIS' centered on each entry.
     * @param values input entries, sorted by the timestamp
     * @return list of the same size as the input, with the same X values
     */
    private static ArrayList<Entry> calculateTrend(ArrayList<Entry> values) {
        ArrayList<Entry> out = new ArrayList<>(values.size());

        // indexes of the first and one past the last entry within the window, and the sum of values between them
        int windowStart = 0, windowEnd = 0;
        double sum = 0.0;

        for (Entry e : values) {
            double x = e.getX();

            while (windowEnd < values.size() && values.get(windowEnd).getX() <= x + TREND_WINDOW_MILLIS / 2.0) {
                sum += values.get(windowEnd).getY();
                windowEnd++;
            }

            while (values.get(windowStart).getX() < x - TREND_WINDOW_MILLIS / 2.0) {
                sum -= values.get(windowStart).getY();
                windowStart++;
            }

            out.add(new Entry(e.getX(), (float) (sum / (windowEnd - windowStart))));
        }

        return out;
    }

    /**
     * Finds an index of the measurement corresponding to the entry selected by an user. The entry may come
     * from any of the data sets displayed on the plot, including trend lines.
     * @param entry selected entry
     * @return index in 'valuesWindSpeed', 'valuesWindGusts' and 'valuesEpoch', or -1 if there is no data
     */
    private int findMeasurementIndex(Entry entry) {
        int closest = -1;
        float closestDistance = Float.MAX_VALUE;

        for (int i = 0; i < valuesWindSpeed.size(); i++) {
            // all data sets share the same order, so the exact entry identifies the measurement
            if (valuesWindSpeed.get(i) == entry || valuesWindGusts.get(i) == entry ||
                    trendWindSpeed.get(i) == entry || trendWindGusts.get(i) == entry) {
                return i;
            }

            float distance = Math.abs(valuesWindSpeed.get(i).getX() - entry.getX());

            if (distance < closestDistance) {
                closestDistance = distance;
                closest = i;
            }
        }

        return closest;
    }

    /**
     * Updates the box placed at the top of the chart with values at the point selected by an user. Values
     * are always taken from the measurements, never from the trend lines.
     * @param date  Date & time string in local timezone
     */
    public void updateLabels(String date, Entry entry) {
        String unit;

        if (AppConfiguration.replaceMsWithKnots) {
            unit = getString(R.string.knots);
        }
        else {
            unit = getString(R.string.meters_per_second);
        }

        int index = findMeasurementIndex(entry);

        if (index < 0 || this.textViewGusts == null || this.textViewSpeed == null || this.textViewTimestamp == null) {
            return;
        }

        float mean = valuesWindSpeed.get(index).getY();
        float gusts = valuesWindGusts.get(index).getY();

        // format the exact timestamp of the measurement in the user timezone, the same way as 'PlotClickEvent' does
        ZonedDateTime localDateTime = LocalDateTime.ofEpochSecond(valuesEpoch.get(index), 0, ZoneOffset.UTC)
                .atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault());
        date = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT).format(localDateTime);

        this.textViewTimestamp.setText(date);
        this.textViewSpeed.setText(withColorDot(ColorTemplate.getHoloBlue(),
                getString(R.string.mean_value_short) + String.format(": %.1f%s", mean, unit).replace(',', '.')));
        this.textViewGusts.setText(withColorDot(ColorTemplate.rgb("#C70039"),
                getString(R.string.wind_gust_short) + String.format(": %.1f%s", gusts, unit).replace(',', '.')));
        this.textViewSpeed.setVisibility(View.VISIBLE);
        this.textViewGusts.setVisibility(View.VISIBLE);
    }

    /**
     * Prepends a dot in the colour of the data set on the plot, so it works as a legend
     */
    private static CharSequence withColorDot(int color, String text) {
        SpannableStringBuilder out = new SpannableStringBuilder("\u25CF ");
        out.setSpan(new ForegroundColorSpan(color), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        out.append(text);

        return out;
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
        ArrayList<Entry> narrowed_set, narrowed_set_gusts, narrowed_trend, narrowed_trend_gusts;

        // data set to be: measurements displayed as dots and trends displayed as lines
        LineDataSet set_windspeed, set_windgusts, set_windspeed_trend, set_windgusts_trend;


        if (valuesWindSpeed.size() > 0) {

            if (from != 0 || to != 0) {

                // if 'from' and 'to' are the index values
                if (!index_or_timestamp) {
                    // make a sublist
                    narrowed_set = new ArrayList<>(valuesWindSpeed.subList((int)from, (int)to));
                    narrowed_set_gusts = new ArrayList<>(valuesWindGusts.subList((int)from, (int)to));
                    narrowed_trend = new ArrayList<>(trendWindSpeed.subList((int)from, (int)to));
                    narrowed_trend_gusts = new ArrayList<>(trendWindGusts.subList((int)from, (int)to));

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
                        narrowed_trend = new ArrayList<>();
                        narrowed_trend_gusts = new ArrayList<>();

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

                        trendWindSpeed.forEach((Entry e) -> {
                            if (e.getX() > (from * 1000) &&
                                    e.getX() < (to * 1000)) {
                                narrowed_trend.add(e);
                            }
                        });

                        trendWindGusts.forEach((Entry e) -> {
                            if (e.getX() > (from * 1000) &&
                                    e.getX() < (to * 1000)) {
                                narrowed_trend_gusts.add(e);
                            }
                        });


                    }

                }
                // and generate the set from it
                set_windspeed = new LineDataSet(narrowed_set, "Wind Speed");
                set_windgusts = new CrossLineDataSet(narrowed_set_gusts, "Wind Gusts");
                set_windspeed_trend = new LineDataSet(narrowed_trend, "Wind Speed trend");
                set_windgusts_trend = new LineDataSet(narrowed_trend_gusts, "Wind Gusts trend");
            }
            else {
                // use 'values_wind_speed' directly as a whole
                set_windspeed = new LineDataSet(valuesWindSpeed, "Wind Speed");
                set_windgusts = new CrossLineDataSet(valuesWindGusts, "Wind Gusts");
                set_windspeed_trend = new LineDataSet(trendWindSpeed, "Wind Speed trend");
                set_windgusts_trend = new LineDataSet(trendWindGusts, "Wind Gusts trend");
            }

            // measurements as single, unconnected dots. The line itself is transparent
            setupDots(set_windspeed, ColorTemplate.getHoloBlue(), Color.rgb(244, 117, 117));
            setupDots(set_windgusts, ColorTemplate.rgb("#C70039"), ColorTemplate.rgb("#C70039"));

            // gusts as small crosses instead of dots, drawn by CrossMarkerLineChartRenderer
            set_windgusts.setDrawCircles(false);
            set_windgusts.setCircleRadius(3.0f);

            // trends as smooth lines
            setupTrend(set_windspeed_trend, ColorTemplate.getHoloBlue(), Color.rgb(244, 117, 117));
            setupTrend(set_windgusts_trend, ColorTemplate.rgb("#C70039"), ColorTemplate.rgb("#C70039"));

            // round line joins and caps, so the line has no sharp spikes and notches
            chart.getRenderer().getPaintRender().setStrokeJoin(Paint.Join.ROUND);
            chart.getRenderer().getPaintRender().setStrokeCap(Paint.Cap.ROUND);

            // legend would show four entries, two of them with the transparent colour
            chart.getLegend().setCustom(new LegendEntry[] {
                    new LegendEntry(set_windspeed.getLabel(), Legend.LegendForm.CIRCLE,
                            Float.NaN, Float.NaN, null, ColorTemplate.getHoloBlue()),
                    new LegendEntry(set_windgusts.getLabel(), Legend.LegendForm.CIRCLE,
                            Float.NaN, Float.NaN, null, ColorTemplate.rgb("#C70039"))
            });

            // create a data object with the data sets, trend lines are drawn on top of the dots
            LineData line_data = new LineData();
            line_data.addDataSet(set_windspeed);
            line_data.addDataSet(set_windgusts);
            line_data.addDataSet(set_windspeed_trend);
            line_data.addDataSet(set_windgusts_trend);
            line_data.setValueTextColor(Color.WHITE);
            line_data.setValueTextSize(9f);

            // set data
            chart.setData(line_data);
            chart.setDoubleTapToZoomEnabled(false);
            chart.setOnChartValueSelectedListener(plotClickEvent);
        }

    }

    /**
     * Configures the data set to display measurements as single, unconnected dots
     */
    private static void setupDots(LineDataSet set, int color, int highlightColor) {
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.TRANSPARENT);
        set.setCircleColor(color);
        set.setCircleRadius(2.5f);
        set.setDrawCircles(true);
        set.setDrawCircleHole(false);
        set.setValueTextColor(ColorTemplate.getHoloBlue());
        set.setDrawValues(true);
        set.setHighLightColor(highlightColor);
    }

    /**
     * Configures the data set to display the trend as a smooth line
     */
    private static void setupTrend(LineDataSet set, int color, int highlightColor) {
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);     // smooth line with slightly rounded corners
        set.setCubicIntensity(0.15f);
        set.setColor(color);
        set.setLineWidth(2.5f);
        set.setDrawCircles(false);
        set.setDrawValues(false);
        set.setHighLightColor(highlightColor);
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