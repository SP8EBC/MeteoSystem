package cc.pogoda.mobile.pogodacc.type.web;

import java.time.LocalDateTime;

import cc.pogoda.mobile.pogodacc.type.CustomLocalDateTime;

public class StationData {

    public int id;

    public long epoch;

    public CustomLocalDateTime datetime;
    //public LocalDateTime datetime;

    public String station;

    public float temperature;

    public short humidity;

    public float pressure;

    public short winddir;

    public float windspeed;

    public float windgusts;

    public String tsource;

    public String wsource;

    public String psource;

    public String hsource;

    public String rsource;
}
