package cc.pogoda.mobile.meteosystem.type;

import android.app.Activity;
import android.graphics.Color;
import android.widget.TextView;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;
import org.tinylog.Logger;

import cc.pogoda.mobile.meteosystem.R;
import cc.pogoda.mobile.meteosystem.type.web.QualityFactor;
import cc.pogoda.mobile.meteosystem.type.web.Summary;

public class StationSummaryActElements implements StationActivityElements {

    public TextView title = null;
    public TextView wind_speed_val = null;
    public TextView wind_gusts_val = null;
    public TextView wind_dir_val = null;
    public TextView temperature_val = null;
    public TextView qnh_val = null;
    public TextView humidity_val = null;
    public TextView message = null;

    public int goodColor = 0;
    public int badColor = 0;

    public static String convertDegreesToDir(int directionInDegrees) {
        String out = null;

        if (directionInDegrees <= 11 || directionInDegrees >= 349)
            out = "N";
        else if (directionInDegrees <= 34 && directionInDegrees > 11)
            out = "N NE";
        else if (directionInDegrees <= 56 && directionInDegrees > 34)
            out = "NE";
        else if (directionInDegrees <= 79 && directionInDegrees > 56)
            out = "E NE";
        else if (directionInDegrees <= 101 && directionInDegrees > 79)
            out = "E";
        else if (directionInDegrees <= 124 && directionInDegrees > 101)
            out = "E SE";
        else if (directionInDegrees <= 146 && directionInDegrees > 124)
            out = "SE";
        else if (directionInDegrees <= 169 && directionInDegrees > 146)
            out = "S SE";
        else if (directionInDegrees <= 191 && directionInDegrees > 169)
            out = "S";
        else if (directionInDegrees <= 214 && directionInDegrees > 191)
            out = "S SW";
        else if (directionInDegrees <= 236 && directionInDegrees > 214)
            out = "SW";
        else if (directionInDegrees <= 259 && directionInDegrees > 236)
            out = "W SW";
        else if (directionInDegrees <= 281 && directionInDegrees > 259)
            out = "W";
        else if (directionInDegrees <= 304 && directionInDegrees > 281)
            out = "W NW";
        else if (directionInDegrees <= 327 && directionInDegrees > 304)
            out = "NW";
        else if (directionInDegrees <= 349 && directionInDegrees > 327)
            out = "N NW";
        else;


        return out;
    }

    public void updateFromSummary(Summary s, AvailableParameters enabledForStation) {

        if (s == null) {
            // print a message in case there is no data available
            wind_speed_val.setText(R.string.no_data);
            wind_gusts_val.setText(R.string.no_data);
            wind_dir_val.setText(R.string.no_data);
            temperature_val.setText(R.string.no_data);
            qnh_val.setText(R.string.no_data);
            humidity_val.setText(R.string.no_data);

            message.setText(R.string.no_data);

            return;
        }

        // convert the integer with unix epoch timestamp to LocalDateTime in current system Time Zone
        LocalDateTime last_station_data = LocalDateTime.ofEpochSecond(s.last_timestamp, 0, ZonedDateTime.now().getOffset());

        // current date and time (in current time zone set in system configuration)
        LocalDateTime current = LocalDateTime.now();

        long minutes_difference = last_station_data.until(current, ChronoUnit.MINUTES);

        Logger.debug("[StationSummaryActElements][updateFromSummary][last_station_data = " + last_station_data.format(DateTimeFormatter. ISO_LOCAL_DATE_TIME) +"]");

        // calculate the duration between
        Duration duration = Duration.between(last_station_data, current);

        // check how old the last data from stations is
        if (duration.getSeconds() < 7200) {
            // if the last data is no older than 2 hours
            message.setText(R.string.auto_refresh);
            message.setTextColor(Color.BLACK);
        }
        else {
            message.setText(R.string.station_not_comm);
            message.setTextColor(Color.argb(0xFF, 0xFF, 0x0, 0x0));
        }

        if (enabledForStation.windSpeed) {
            wind_speed_val.setText(String.format("%s", s.getWindspeedStr(true)));

            if (goodColor != 0 && !s.wind_qf_native.equals(QualityFactor.NOT_AVALIABLE)) {
                wind_speed_val.setTextColor(goodColor);
            }
            else if (badColor != 0) {
                wind_speed_val.setTextColor(badColor);
            }
        }
        else {
            wind_speed_val.setText("---");
        }

        if (enabledForStation.windGusts) {
            wind_gusts_val.setText(String.format("%s", s.getWindgustsStr(true)));

            if (goodColor != 0 && !s.wind_qf_native.equals(QualityFactor.NOT_AVALIABLE)) {
                wind_gusts_val.setTextColor(goodColor);
            }
            else if (badColor != 0) {
                wind_gusts_val.setText(badColor);
            }
        }
        else {
            wind_gusts_val.setText("---");
        }

        if (enabledForStation.windDirection) {
            wind_dir_val.setText(this.convertDegreesToDir(s.direction));

            if (goodColor != 0 && !s.wind_qf_native.equals(QualityFactor.NOT_AVALIABLE)) {
                wind_dir_val.setTextColor(goodColor);
            }
            else if (badColor != 0){
                wind_dir_val.setTextColor(badColor);
            }
        }
        else  {
            wind_dir_val.setText("---");
        }

        temperature_val.setText(String.format("%s", s.getTemperatureStr(true, false)));

        if (goodColor != 0 && !s.temperature_qf_native.equals(QualityFactor.NOT_AVALIABLE)) {
            temperature_val.setTextColor(goodColor);
        }
        else if (badColor != 0){
            temperature_val.setTextColor(badColor);
        }
        // TODO
        if (enabledForStation.qnh) {
            qnh_val.setText(String.format("%d hPa", s.qnh));

            if (goodColor != 0 && !s.qnh_qf_native.equals(QualityFactor.NOT_AVALIABLE)) {
                qnh_val.setTextColor(goodColor);
            }
            else if (badColor != 0) {
                qnh_val.setTextColor(badColor);
            }
        }
        else {
            qnh_val.setText("---");
        }

        if (enabledForStation.humidity) {
            humidity_val.setText(String.format("%d %%", s.humidity));

            if (goodColor != 0 && !s.humidity_qf_native.equals(QualityFactor.NOT_AVALIABLE)) {
                humidity_val.setTextColor(goodColor);
            }
            else {
                humidity_val.setTextColor(badColor);
            }
        }
        else {
            humidity_val.setText("---");
        }
    }

    @Override
    public void setActivity(Activity act) {

    }

}
