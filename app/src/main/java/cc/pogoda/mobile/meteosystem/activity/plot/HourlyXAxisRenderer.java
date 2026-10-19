package cc.pogoda.mobile.meteosystem.activity.plot;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.Utils;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;

/**
 * X axis renderer for plots where X values are epoch timestamps in milliseconds. Instead of
 * the library default (which picks a 'round' number of milliseconds as the step, giving labels
 * like 19:21, 20:09) it places major steps exactly at the top of each hour in the user timezone.
 * If hourly labels would overlap (plot zoomed out), the step is increased to 2, 3, 6, 12 or 24
 * hours, still aligned to the full hour, unless the renderer is installed with a fixed one hour step.
 */
public class HourlyXAxisRenderer extends XAxisRenderer {

    private static final int[] STEPS_HOURS = {1, 2, 3, 6, 12, 24};

    /**
     * if true the step is always one hour, even if labels would overlap
     */
    private final boolean fixedHourlyStep;

    /**
     * Replaces the default X axis renderer of the chart with this one, the step is increased
     * when hourly labels would overlap
     */
    public static void install(LineChart chart) {
        install(chart, false);
    }

    /**
     * Replaces the default X axis renderer of the chart with this one
     * @param fixedHourlyStep if true the step is always one hour
     */
    public static void install(LineChart chart, boolean fixedHourlyStep) {
        XAxis xAxis = chart.getXAxis();

        // centered labels would be shifted by half of a step, away from the hour
        xAxis.setCenterAxisLabels(false);
        xAxis.setValueFormatter(new HourlyXAxisValueFormatter());

        chart.setXAxisRenderer(new HourlyXAxisRenderer(chart, fixedHourlyStep));
    }

    private HourlyXAxisRenderer(LineChart chart, boolean fixedHourlyStep) {
        super(chart.getViewPortHandler(), chart.getXAxis(), chart.getTransformer(YAxis.AxisDependency.LEFT));
        this.fixedHourlyStep = fixedHourlyStep;
    }

    @Override
    protected void computeAxisValues(float min, float max) {

        ArrayList<Float> entries = new ArrayList<>();

        if (max > min) {
            int stepHours = fixedHourlyStep ? 1 : calculateStepHours(max - min);

            ZoneId zone = ZoneId.systemDefault();

            // first full hour at or after the beginning of the visible range
            ZonedDateTime tick = Instant.ofEpochMilli((long) min).atZone(zone).truncatedTo(ChronoUnit.HOURS);
            if (tick.toInstant().toEpochMilli() < (long) min) {
                tick = tick.plusHours(1);
            }

            // align to the step, so i.e with 3h step labels are at 0:00, 3:00, 6:00 ...
            while (tick.getHour() % stepHours != 0) {
                tick = tick.plusHours(1);
            }

            while (tick.toInstant().toEpochMilli() <= (long) max) {
                entries.add((float) tick.toInstant().toEpochMilli());
                tick = tick.plusHours(stepHours);
            }
        }

        int n = entries.size();

        if (mAxis.mEntries.length < n) {
            mAxis.mEntries = new float[n];
        }
        if (mAxis.mCenteredEntries.length < n) {
            mAxis.mCenteredEntries = new float[n];
        }

        for (int i = 0; i < n; i++) {
            mAxis.mEntries[i] = entries.get(i);
            mAxis.mCenteredEntries[i] = entries.get(i);
        }

        mAxis.mEntryCount = n;
        mAxis.mDecimals = 0;

        computeSize();
    }

    /**
     * Returns the smallest step (in hours) at which the labels won't overlap each other
     */
    private int calculateStepHours(float rangeMillis) {

        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());

        FSize labelSize = Utils.calcTextSize(mAxisLabelPaint, "00:00");
        FSize rotated = Utils.getSizeOfRotatedRectangleByDegrees(
                labelSize.width, labelSize.height, mXAxis.getLabelRotationAngle());

        // keep some free space between labels
        float labelWidth = rotated.width * 1.5f;
        FSize.recycleInstance(labelSize);
        FSize.recycleInstance(rotated);

        float maxLabels = Math.max(2.0f, mViewPortHandler.contentWidth() / labelWidth);
        float rangeHours = rangeMillis / 3_600_000.0f;

        for (int step : STEPS_HOURS) {
            if (rangeHours / step <= maxLabels) {
                return step;
            }
        }

        // a lot of days visible, use multiplies of 24 hours
        return 24 * (int) Math.ceil(rangeHours / 24.0f / maxLabels);
    }
}
