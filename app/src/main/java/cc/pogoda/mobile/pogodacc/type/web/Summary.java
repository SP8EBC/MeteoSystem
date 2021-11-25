package cc.pogoda.mobile.pogodacc.type.web;

import cc.pogoda.mobile.pogodacc.config.AppConfiguration;

public class Summary {

    private static final String SPACE = " ";
    private static final String NO_SPACE = "";

    public long last_timestamp;

    public int number_of_measurements;

    private float avg_temperature;

    public String temperature_qf;

    public QualityFactor temperature_qf_native;

    public short qnh;

    public String qnh_qf;

    public QualityFactor qnh_qf_native;

    public byte humidity;

    public String humidity_qf;

    public QualityFactor humidity_qf_native;

    public short direction;

    private float average_speed;

    private float gusts;

    private float hour_gusts;

    private float hour_max_average_speed;

    private float hour_min_average_speed;

    public String wind_qf;

    public QualityFactor wind_qf_native;

    public Summary() {
        temperature_qf_native = QualityFactor.UNSET;
        humidity_qf_native = QualityFactor.UNSET;
        wind_qf_native = QualityFactor.UNSET;
    }

    public float getAvgTemperature() {
        return avg_temperature;
    }

    public float getAverageSpeed() {
        return average_speed;
    }

    public float getGusts() {
        return gusts;
    }

    public float getHourGusts() {
        return hour_gusts;
    }

    public float getHourMaxAverageSpeed() {
        return hour_max_average_speed;
    }

    public float getHourMinAverageSpeed() {
        return hour_min_average_speed;
    }

    public String getTemperatureStr(boolean space, boolean round) {
        String out;

        String s;

        if (space) {
            s = SPACE;
        }
        else {
            s = NO_SPACE;
        }

        if (round) {
            out = String.format("%d%s°C", Math.round(avg_temperature), s);
        }
        else {
            out = String.format("%.1f%s°C", avg_temperature, s);
        }

        out = out.replace(',', '.');

        return out;
    }
    
    public String getWindspeedStr(boolean space) {
        String out;

        String s;

        if (space) {
            s = SPACE;
        }
        else {
            s = NO_SPACE;
        }

        if (AppConfiguration.replaceMsWithKnots) {
            float knots = this.average_speed * 1.94f;

            out = String.format("%2d%skn", Math.round(knots), s);
        }
        else {
            out = String.format("%4.1f%sm/s", this.average_speed, s);
        }

        out = out.replace(',', '.');

        return out;
    }

    public String getHourMinWindspeedStr(boolean space) {
        String out;

        String s;

        if (space) {
            s = SPACE;
        }
        else {
            s = NO_SPACE;
        }

        if (AppConfiguration.replaceMsWithKnots) {
            float knots = this.hour_min_average_speed * 1.94f;

            out = String.format("%2d%skn", Math.round(knots), s);
        }
        else {
            out = String.format("%4.1f%sm/s", this.hour_min_average_speed, s);
        }

        out = out.replace(',', '.');

        return out;
    }

    public String getHourMaxWindspeedStr(boolean space) {
        String out;

        String s;

        if (space) {
            s = SPACE;
        }
        else {
            s = NO_SPACE;
        }

        if (AppConfiguration.replaceMsWithKnots) {
            float knots = this.hour_max_average_speed * 1.94f;

            out = String.format("%2d%skn", Math.round(knots), s);
        }
        else {
            out = String.format("%4.1f%sm/s", this.hour_max_average_speed, s);
        }

        out = out.replace(',', '.');

        return out;
    }

    public String getWindgustsStr(boolean space) {
        String out;

        String s;

        if (space) {
            s = SPACE;
        }
        else {
            s = NO_SPACE;
        }

        if (AppConfiguration.replaceMsWithKnots) {
            float knots = this.gusts * 1.94f;

            out = String.format("%2d%skn", Math.round(knots), s);
        }
        else {
            out = String.format("%4.1f%sm/s", this.gusts, s);
        }

        out = out.replace(',', '.');

        return out;
    }

    public String getHourWindgustsStr(boolean space) {
        String out;

        String s;

        if (space) {
            s = SPACE;
        }
        else {
            s = NO_SPACE;
        }

        if (AppConfiguration.replaceMsWithKnots) {
            float knots = this.hour_gusts * 1.94f;

            out = String.format("%2d%skn", Math.round(knots), s);
        }
        else {
            out = String.format("%4.1f%sm/s", this.hour_gusts, s);
        }

        out = out.replace(',', '.');

        return out;
    }

    public String getWindDirStr() {
        String out;

        if (direction <= 11 && direction >= 349) {
            out = String.format("%5s", "N");
        }
        else if (direction <= 34 && direction > 11) {
            out = String.format("%5s", "N NE");
        }
        else if (direction <= 56 && direction > 34) {
            out = String.format("%5s", "NE");
        }
        else if (direction <= 79 && direction > 56) {
            out = String.format("%5s", "E NE");
        }
        else if (direction <= 101 && direction > 79) {
            out = String.format("%5s", "E");
        }
        else if (direction <= 124 && direction > 101) {
            out = String.format("%5s", "E SE");
        }
        else if (direction <= 146 && direction > 124) {
            out = String.format("%5s", "SE");
        }
        else if (direction <= 169 && direction > 146) {
            out = String.format("%5s", "S SE");
        }
        else if (direction <= 191 && direction > 169) {
            out = String.format("%5s", "S");
        }
        else if (direction <= 214 && direction > 191) {
            out = String.format("%5s", "S SW");
        }
        else if (direction <= 236 && direction > 214) {
            out = String.format("%5s", "SW");
        }
        else if (direction <= 259 && direction > 236) {
            out = String.format("%5s", "W SW");
        }
        else if (direction <= 281 && direction > 259) {
            out = String.format("%5s", "W");
        }
        else if (direction <= 304 && direction > 281) {
            out = String.format("%5s", "W NW");
        }
        else if (direction <= 327 && direction > 304) {
            out = String.format("%5s", "NW");
        }
        else if (direction <= 349 && direction > 327) {
            out = String.format("%5s", "N NW");
        }
        else {
            out = "";
        }

        return out;
    }

    /**
     *
     if (WX.wind_direction <= 11 && WX.wind_direction >= 349)
     html << "- N";
     else if (WX.wind_direction <= 34 && WX.wind_direction > 11)
     html << "- N NE";
     else if (WX.wind_direction <= 56 && WX.wind_direction > 34)
     html << "- NE";
     else if (WX.wind_direction <= 79 && WX.wind_direction > 56)
     html << "- E NE";
     else if (WX.wind_direction <= 101 && WX.wind_direction > 79)
     html << "- E";
     else if (WX.wind_direction <= 124 && WX.wind_direction > 101)
     html << "- E SE";
     else if (WX.wind_direction <= 146 && WX.wind_direction > 124)
     html << "- SE";
     else if (WX.wind_direction <= 169 && WX.wind_direction > 146)
     html << "- S SE";
     else if (WX.wind_direction <= 191 && WX.wind_direction > 169)
     html << "- S";
     else if (WX.wind_direction <= 214 && WX.wind_direction > 191)
     html << "- S SW";
     else if (WX.wind_direction <= 236 && WX.wind_direction > 214)
     html << "- SW";
     else if (WX.wind_direction <= 259 && WX.wind_direction > 236)
     html <<"-  W SW";
     else if (WX.wind_direction <= 281 && WX.wind_direction > 259)
     html << "- W";
     else if (WX.wind_direction <= 304 && WX.wind_direction > 281)
     html << "- W NW";
     else if (WX.wind_direction <= 327 && WX.wind_direction > 304)
     html << "- NW";
     else if (WX.wind_direction <= 349 && WX.wind_direction > 327)
     html << "- N NW";
     else;
     */

}
