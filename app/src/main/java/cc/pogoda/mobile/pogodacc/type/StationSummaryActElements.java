package cc.pogoda.mobile.pogodacc.type;

import android.widget.TextView;

import cc.pogoda.mobile.pogodacc.type.web.Summary;

public class StationSummaryActElements {

    public TextView title = null;
    public TextView wind_speed_val = null;
    public TextView wind_gusts_val = null;
    public TextView wind_dir_val = null;
    public TextView temperature_val = null;
    public TextView qnh_val = null;
    public TextView humidity_val = null;

    private String convertDegreesToDir(int directionInDegrees) {
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

    public void updateFromSummary(Summary s) {
        if (wind_speed_val != null)
            wind_speed_val.setText(String.format("%.1f m/s", s.average_speed));

        if (wind_gusts_val != null)
            wind_gusts_val.setText(String.format("%.1f m/s", s.gusts));

        if (wind_dir_val != null)
            wind_dir_val.setText(this.convertDegreesToDir(s.direction));

        if (temperature_val != null)
            temperature_val.setText(String.format("%.1f °C", s.avg_temperature));

        if (qnh_val != null)
            qnh_val.setText(String.format("%d hPa", s.qnh));

        if (humidity_val != null)
            humidity_val.setText(String.format("%d %", s.humidity));
    }

}
