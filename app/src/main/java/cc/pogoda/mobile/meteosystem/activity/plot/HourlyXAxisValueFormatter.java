package cc.pogoda.mobile.meteosystem.activity.plot;

import com.github.mikephil.charting.formatter.ValueFormatter;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;

/**
 * Formats X axis labels placed by {@link HourlyXAxisRenderer}. Values are epoch timestamps in
 * milliseconds, displayed in the user timezone.
 */
public class HourlyXAxisValueFormatter extends ValueFormatter {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public String getFormattedValue(float value) {

        // the plot stores X as float, which for epoch millis has a resolution of about two minutes,
        // so a tick placed at 20:00 may come back as i.e 19:59:30. Labels are always at the
        // top of an hour, so round to the nearest one.
        ZonedDateTime localDateTime = Instant.ofEpochMilli((long) value)
                .atZone(ZoneId.systemDefault())
                .plusMinutes(30)
                .truncatedTo(ChronoUnit.HOURS);

        return FMT.format(localDateTime);
    }
}
