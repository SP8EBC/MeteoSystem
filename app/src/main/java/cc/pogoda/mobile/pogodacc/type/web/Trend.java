package cc.pogoda.mobile.pogodacc.type.web;

public class Trend {
    public long currentTimestampUtc;

    public String currentTemperatureQf;

    public String currentQnhQf;

    public String currentHumidityQf;

    public String currentWindQf;

    public TrendData temperatureTrend;

    public TrendData humidityTrend;

    public TrendData pressureTrend;

    public TrendData averageWindspeedTrend;

    public TrendData maximumWindpseedTrend;

    public Trend() {
        temperatureTrend = new TrendData();
        humidityTrend = new TrendData();
        pressureTrend = new TrendData();
        averageWindspeedTrend = new TrendData();
        maximumWindpseedTrend = new TrendData();
    }
}
