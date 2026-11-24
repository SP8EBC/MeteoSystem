package cc.pogoda.mobile.meteosystem.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.tinylog.Logger;

import cc.pogoda.mobile.meteosystem.Main;
import cc.pogoda.mobile.meteosystem.R;
import cc.pogoda.mobile.meteosystem.activity.plot.DirectionHistogramView;
import cc.pogoda.mobile.meteosystem.activity.plot.SpeedHistogramView;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;

public class StationDetailsHistogramActivity  extends ToolbarActivity {

    // ---- MOCKUP DATA, to be replaced by HistogramSpeed / HistogramDirection from the web service ----

    /** Wind speed histogram, bins 0-1, 1-2 ... 9-10 m/s and 10+ m/s */
    private static final int[] MOCK_SPEED = {12, 48, 95, 130, 110, 74, 41, 22, 9, 4, 2};

    /** Wind gusts histogram, bins 0-1, 1-2 ... 9-10 m/s and 10+ m/s */
    private static final int[] MOCK_GUSTS = {3, 10, 28, 55, 90, 112, 98, 70, 44, 25, 17};

    /** Wind direction histogram (sector length), 22.5 deg bins from N clockwise: N-NNE, NNE-NE ... NNW-N */
    private static final int[] MOCK_DIRECTION = {20, 14, 8, 5, 6, 10, 18, 30, 42, 65, 88, 120, 96, 60, 35, 24};

    /** Colour level of each direction bin (e.g. average speed in 0.1 m/s), green = low, dark red = high */
    private static final int[] MOCK_DIRECTION_COLOR = {15, 12, 10, 8, 9, 14, 22, 30, 38, 52, 70, 95, 80, 55, 33, 20};

    // ------------------------------------------------------------------------------------------------

    Main main = null;

    WeatherStation station = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_station_details_histogram);

        main = (Main)getApplication();

        station = (WeatherStation) getIntent().getSerializableExtra("station");

        Logger.info("[station.getSystemName() = " + station.getSystemName() +"]");

        TextView title = findViewById(R.id.textViewStationHistogramSummaryTitle);
        title.setText(station.getDisplayedName());

        DirectionHistogramView directionView = findViewById(R.id.directionHistogramView);
        SpeedHistogramView speedView = findViewById(R.id.speedHistogramView);
        SpeedHistogramView gustsView = findViewById(R.id.gustsHistogramView);

        directionView.setData(MOCK_DIRECTION, MOCK_DIRECTION_COLOR);
        speedView.setData(MOCK_SPEED);
        gustsView.setData(MOCK_GUSTS);
        gustsView.setBarColor(ContextCompat.getColor(this, R.color.orange_1));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
