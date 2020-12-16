package cc.pogoda.mobile.pogodacc.activity.handler;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class WindPlotClickEvent implements OnChartValueSelectedListener {

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        float value = e.getX();
        long value_int = (long) value;
        Date date = new Date(value_int);
        return;
    }

    @Override
    public void onNothingSelected() {

    }
}
