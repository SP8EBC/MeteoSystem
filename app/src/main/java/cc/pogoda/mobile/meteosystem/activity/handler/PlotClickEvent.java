package cc.pogoda.mobile.meteosystem.activity.handler;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.util.Date;

import cc.pogoda.mobile.meteosystem.type.StationDetailsPlot;

public class PlotClickEvent implements OnChartValueSelectedListener {

    private StationDetailsPlot parent;

    public PlotClickEvent(StationDetailsPlot parent) {

        this.parent = parent;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        float value = e.getX();
        long value_int = (long) value;
        Date date = new Date(value_int);

        // the web service and the plot always stores the entries as UTC. So first convert epoch timestamp to the LocalDateTime
        LocalDateTime utcDateTime = LocalDateTime.ofEpochSecond(value_int / 1000, 0, ZoneOffset.UTC);

        // and then shift to the user timezone for convinient display
        ZonedDateTime localDateTime = utcDateTime.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault());

        // format the time and date according to current locale
        String dt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT).format(localDateTime);

        // call the function to look for the matching windspeed and gusts vales
        // as the 'h' parameter doesn't hold an information about an index in the
        // input dataset
        parent.updateLabels(dt, e);

        return;
    }

    @Override
    public void onNothingSelected() {

    }
}
