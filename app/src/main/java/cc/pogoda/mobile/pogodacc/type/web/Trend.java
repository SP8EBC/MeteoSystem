package cc.pogoda.mobile.pogodacc.type.web;

public class Trend {

    public long last_timestamp;

    public String displayed_name;

    public String current_temperature_qf;

    public String current_qnh_qf;

    public String current_humidity_qf;

    public String current_wind_qf;

    public TrendData temperature_trend;

    public TrendData humidity_trend;

    public TrendData pressure_trend;

    public TrendData average_wind_speed_trend;

    public TrendData maximum_wind_speed_trend;

    public TrendData wind_direction_trend;

    public Trend() {
        temperature_trend = new TrendData();
        humidity_trend = new TrendData();
        pressure_trend = new TrendData();
        average_wind_speed_trend = new TrendData();
        maximum_wind_speed_trend = new TrendData();
        wind_direction_trend = new TrendData();
    }
}
