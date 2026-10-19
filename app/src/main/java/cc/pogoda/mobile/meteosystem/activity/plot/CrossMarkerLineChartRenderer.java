package cc.pogoda.mobile.meteosystem.activity.plot;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;

/**
 * Line chart renderer, which additionally draws entries of each {@link CrossLineDataSet} as
 * small 'x' crosses. All other data sets are drawn as by the default renderer.
 */
public class CrossMarkerLineChartRenderer extends LineChartRenderer {

    private final Paint crossPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final float[] buffer = new float[2];

    /**
     * Replaces the default renderer of the chart with this one
     */
    public static void install(LineChart chart) {
        chart.setRenderer(new CrossMarkerLineChartRenderer(chart));
    }

    private CrossMarkerLineChartRenderer(LineChart chart) {
        super(chart, chart.getAnimator(), chart.getViewPortHandler());

        crossPaint.setStyle(Paint.Style.STROKE);
        crossPaint.setStrokeCap(Paint.Cap.ROUND);
        crossPaint.setStrokeWidth(Utils.convertDpToPixel(1.5f));
    }

    @Override
    public void drawExtras(Canvas c) {
        super.drawExtras(c);

        float phaseY = mAnimator.getPhaseY();

        for (ILineDataSet dataSet : mChart.getLineData().getDataSets()) {

            if (!(dataSet instanceof CrossLineDataSet) || !dataSet.isVisible() || dataSet.getEntryCount() == 0) {
                continue;
            }

            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

            mXBounds.set(mChart, dataSet);

            // circle radius is already converted to pixels by the data set
            float half = dataSet.getCircleRadius();
            crossPaint.setColor(dataSet.getCircleColor(0));

            int boundsRangeCount = mXBounds.range + mXBounds.min;

            for (int j = mXBounds.min; j <= boundsRangeCount; j++) {

                Entry e = dataSet.getEntryForIndex(j);

                if (e == null) break;

                buffer[0] = e.getX();
                buffer[1] = e.getY() * phaseY;

                trans.pointValuesToPixel(buffer);

                if (!mViewPortHandler.isInBoundsRight(buffer[0]))
                    break;

                if (!mViewPortHandler.isInBoundsLeft(buffer[0]) ||
                        !mViewPortHandler.isInBoundsY(buffer[1]))
                    continue;

                c.drawLine(buffer[0] - half, buffer[1] - half, buffer[0] + half, buffer[1] + half, crossPaint);
                c.drawLine(buffer[0] - half, buffer[1] + half, buffer[0] + half, buffer[1] - half, crossPaint);
            }
        }
    }
}
