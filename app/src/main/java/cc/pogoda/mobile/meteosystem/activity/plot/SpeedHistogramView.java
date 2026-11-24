package cc.pogoda.mobile.meteosystem.activity.plot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import cc.pogoda.mobile.meteosystem.R;

/**
 * Vertical bar plot of a wind speed (or gusts) histogram. There are {@link #BINS} bins, each one
 * 1 m/s wide: 0-1, 1-2 ... 9-10 and the last one for 10 m/s and above. The height of each bar
 * is proportional to the number of measurements in its bin, the highest bin fills the plot area.
 */
public class SpeedHistogramView extends View {

    public static final int BINS = 11;

    private final int[] counts = new int[BINS];

    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF bar = new RectF();

    public SpeedHistogramView(Context context) {
        this(context, null);
    }

    public SpeedHistogramView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        int textColor = ContextCompat.getColor(context, R.color.summary_text);

        barPaint.setStyle(Paint.Style.FILL);
        barPaint.setColor(ContextCompat.getColor(context, R.color.dark_blue));

        axisPaint.setStyle(Paint.Style.STROKE);
        axisPaint.setStrokeWidth(dp(1));
        axisPaint.setColor(textColor);

        textPaint.setColor(textColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(12));
    }

    /**
     * @param values number of measurements in each bin, from 0-1 m/s up to 10 m/s and above.
     *               Shorter arrays leave remaining bins empty, longer ones are truncated.
     */
    public void setData(int[] values) {
        for (int i = 0; i < BINS; i++) {
            counts[i] = (values != null && i < values.length) ? Math.max(0, values[i]) : 0;
        }
        invalidate();
    }

    public void setBarColor(int color) {
        barPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        // 2:1 aspect ratio unless the layout forces something else
        int height = resolveSize(width / 2, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float textHeight = textPaint.getFontSpacing();

        float left = getPaddingLeft();
        float right = getWidth() - getPaddingRight();
        // room for value labels above the bars and bin labels below the axis
        float top = getPaddingTop() + textHeight;
        float bottom = getHeight() - getPaddingBottom() - textHeight - dp(4);

        float slot = (right - left) / BINS;
        float gap = slot * 0.15f;

        int max = 0;
        for (int c : counts) {
            max = Math.max(max, c);
        }

        for (int i = 0; i < BINS; i++) {
            float x0 = left + i * slot;
            float centerX = x0 + slot / 2;

            if (max > 0 && counts[i] > 0) {
                float h = (bottom - top) * counts[i] / max;
                bar.set(x0 + gap, bottom - h, x0 + slot - gap, bottom);
                canvas.drawRect(bar, barPaint);
                canvas.drawText(String.valueOf(counts[i]), centerX, bottom - h - dp(2), textPaint);
            }

            String label = (i == BINS - 1) ? "10+" : String.valueOf(i);
            canvas.drawText(label, centerX, bottom + dp(2) + textHeight - textPaint.descent(), textPaint);
        }

        canvas.drawLine(left, bottom, right, bottom, axisPaint);
    }

    private float dp(float v) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
    }

    private float sp(float v) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, v, getResources().getDisplayMetrics());
    }
}
