package cc.pogoda.mobile.pogodacc.type;

import java.io.Serializable;

import cc.pogoda.mobile.pogodacc.type.web.StationDefinition;

public class AvailableParameters implements Serializable {

    public boolean windSpeed;

    public boolean windGusts;

    public boolean windDirection;

    public boolean airTemperature;

    public boolean waterTemperature;

    public boolean qnh;

    public boolean humidity;

    public boolean rain;

    public AvailableParameters() {
        windSpeed = false;
        windGusts = false;
        windDirection = false;
        airTemperature = false;
        qnh = false;
        humidity = false;
        waterTemperature = false;
        rain = false;
    }

    public static AvailableParameters fromStation(StationDefinition s) {
        AvailableParameters out = new AvailableParameters();

        if (s.hasHumidity) {
            out.humidity = true;
        }

        if (s.hasWind) {
            out.windDirection = true;
            out.windGusts = true;
            out.windSpeed = true;
        }

        if (s.hasQnh) {
            out.qnh = true;
        }

        if (s.hasRain) {
            out.rain = true;
        }


        return out;
    }
}
