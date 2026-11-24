package cc.pogoda.mobile.meteosystem.activity.plot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import cc.pogoda.mobile.meteosystem.R;

/**
 * Radar (wind rose style) plot of a wind direction histogram. There are {@link #BINS} sectors,
 * each 22.5 degrees wide. The first sector starts at north (0 degrees) and the following ones go
 * clockwise: 0-22.5, 22.5-45 ... 337.5-360.
 *
 * Each sector has two independent values:
 * <ul>
 *     <li>count - controls the length of the sector, proportional to the highest count</li>
 *     <li>colour level - controls the colour of the sector, from green for the lowest value,
 *     through orange, up to dark red for the highest value</li>
 * </ul>
 */
public class DirectionHistogramView extends View {

    public static final int BINS = 16;

    public static final float BIN_WIDTH_DEG = 360.0f / BINS;

    private static final int COLOR_LOW = Color.rgb(0x2E, 0x7D, 0x32);    // green
    private static final int COLOR_MID = Color.rgb(0xFB, 0x8C, 0x00);    // orange
    private static final int COLOR_HIGH = Color.rgb(0x8B, 0x00, 0x00);   // dark red

    private static final String[] CARDINALS = {"N", "E", "S", "W"};

    private final int[] counts = new int[BINS];
    private final int[] colorLevels = new int[BINS];

    /** Colour level mapped to dark red. If not positive, the maximum of colour levels is used */
    private int colorLevelMax = -1;

    private final Paint sectorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Path sector = new Path();
    private final RectF oval = new RectF();

    public DirectionHistogramView(Context context) {
        this(context, null);
    }

    public DirectionHistogramView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        int textColor = ContextCompat.getColor(context, R.color.summary_text);

        sectorPaint.setStyle(Paint.Style.FILL);

        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(dp(1));
        outlinePaint.setColor(ContextCompat.getColor(context, R.color.summary_box_background));

        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(dp(1));
        gridPaint.setColor(ColorUtils.setAlphaComponent(textColor, 0x50));

        textPaint.setColor(textColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(14));
        textPaint.setFakeBoldText(true);
    }

    /**
     * @param binCounts   number of measurements in each sector, starting from north, clockwise.
     *                    Controls the length of each sector.
     * @param binColorLevels colour level of each sector, starting from north, clockwise. Scaled
     *                    from green (0) through orange up to dark red (maximum level). May be null,
     *                    then all sectors are green.
     */
    public void setData(int[] binCounts, @Nullable int[] binColorLevels) {
        for (int i = 0; i < BINS; i++) {
            counts[i] = (binCounts != null && i < binCounts.length) ? Math.max(0, binCounts[i]) : 0;
            colorLevels[i] = (binColorLevels != null && i < binColorLevels.length) ? Math.max(0, binColorLevels[i]) : 0;
        }
        invalidate();
    }

    /**
     * Sets a fixed colour level which is drawn as dark red, so the colours are comparable between
     * different stations. Zero or negative value restores the default: scale to the highest level.
     */
    public void setColorLevelMax(int max) {
        colorLevelMax = max;
        invalidate();
    }

    /**
     * Maps a fraction 0.0 - 1.0 into a colour: green -> orange -> dark red
     */
    public static int colorForFraction(float fraction) {
        float f = Math.max(0.0f, Math.min(1.0f, fraction));
        if (f < 0.5f) {
            return ColorUtils.blendARGB(COLOR_LOW, COLOR_MID, f * 2.0f);
        }
        else {
            return ColorUtils.blendARGB(COLOR_MID, COLOR_HIGH, (f - 0.5f) * 2.0f);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        // square unless the layout forces something else
        int height = resolveSize(width, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth() - getPaddingLeft() - getPaddingRight();
        float h = getHeight() - getPaddingTop() - getPaddingBottom();
        float cx = getPaddingLeft() + w / 2;
        float cy = getPaddingTop() + h / 2;

        // leave room around the plot for N/E/S/W labels
        float labelSpace = textPaint.getFontSpacing() + dp(2);
        float radius = Math.min(w, h) / 2 - labelSpace;
        if (radius <= 0) {
            return;
        }

        int maxCount = 0;
        int maxLevel = colorLevelMax;
        boolean autoLevel = colorLevelMax <= 0;
        for (int i = 0; i < BINS; i++) {
            maxCount = Math.max(maxCount, counts[i]);
            if (autoLevel) {
                maxLevel = Math.max(maxLevel, colorLevels[i]);
            }
        }

        // sectors
        if (maxCount > 0) {
            for (int i = 0; i < BINS; i++) {
                if (counts[i] == 0) {
                    continue;
                }

                float r = radius * counts[i] / maxCount;
                oval.set(cx - r, cy - r, cx + r, cy + r);

                // compass bearing 0 = north (up), clockwise. Canvas angle 0 = east (right),
                // also clockwise, so canvas angle = bearing - 90
                float startAngle = i * BIN_WIDTH_DEG - 90.0f;

                sector.reset();
                sector.moveTo(cx, cy);
                sector.arcTo(oval, startAngle, BIN_WIDTH_DEG);
                sector.close();

                float fraction = maxLevel > 0 ? (float) colorLevels[i] / maxLevel : 0.0f;
                sectorPaint.setColor(colorForFraction(fraction));

                canvas.drawPath(sector, sectorPaint);
                canvas.drawPath(sector, outlinePaint);
            }
        }

        // grid: concentric circles at 25, 50, 75 and 100 % of the highest count
        for (int i = 1; i <= 4; i++) {
            canvas.drawCircle(cx, cy, radius * i / 4.0f, gridPaint);
        }

        // grid: spokes on bin boundaries
        for (int i = 0; i < BINS; i++) {
            double a = Math.toRadians(i * BIN_WIDTH_DEG - 90.0);
            canvas.drawLine(cx, cy, cx + (float) (radius * Math.cos(a)), cy + (float) (radius * Math.sin(a)), gridPaint);
        }

        // cardinal directions
        float textOffset = (textPaint.descent() + textPaint.ascent()) / 2;
        float labelRadius = radius + labelSpace / 2;
        for (int i = 0; i < CARDINALS.length; i++) {
            double a = Math.toRadians(i * 90.0 - 90.0);
            float x = cx + (float) (labelRadius * Math.cos(a));
            float y = cy + (float) (labelRadius * Math.sin(a)) - textOffset;
            canvas.drawText(CARDINALS[i], x, y, textPaint);
        }
    }

    private float dp(float v) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
    }

    private float sp(float v) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, v, getResources().getDisplayMetrics());
    }
}
