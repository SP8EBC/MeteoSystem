package cc.pogoda.mobile.pogodacc.type.web;

public class Summary {

    public long last_timestamp;

    public int number_of_measurements;

    public float avg_temperature;

    public String temperature_qf;

    public QualityFactor temperature_qf_native;

    public short qnh;

    public String qnh_qf;

    public QualityFactor qnh_qf_native;

    public byte humidity;

    public String humidity_qf;

    public QualityFactor humidity_qf_native;

    public short direction;

    public float average_speed;

    public float gusts;

    public float hour_gusts;

    public float hour_max_average_speed;

    public float hour_min_average_speed;

    public String wind_qf;

    public QualityFactor wind_qf_native;

    public Summary() {
        temperature_qf_native = QualityFactor.UNSET;
        humidity_qf_native = QualityFactor.UNSET;
        wind_qf_native = QualityFactor.UNSET;
    }

}
