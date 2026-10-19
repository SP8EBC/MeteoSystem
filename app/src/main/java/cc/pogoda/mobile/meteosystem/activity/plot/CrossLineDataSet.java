package cc.pogoda.mobile.meteosystem.activity.plot;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

/**
 * Line data set which entries are drawn by {@link CrossMarkerLineChartRenderer} as small crosses.
 * Size of a cross is set by {@link #setCircleRadius(float)} (half of the cross width) and its colour
 * by {@link #setCircleColor(int)}. Circles shall be disabled, otherwise both markers are drawn.
 */
public class CrossLineDataSet extends LineDataSet {

    public CrossLineDataSet(List<Entry> yVals, String label) {
        super(yVals, label);
    }
}
