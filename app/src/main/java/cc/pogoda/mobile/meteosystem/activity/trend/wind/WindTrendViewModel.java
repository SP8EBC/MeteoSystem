package cc.pogoda.mobile.meteosystem.activity.trend.wind;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import cc.pogoda.mobile.meteosystem.dao.TrendDao;
import cc.pogoda.mobile.meteosystem.type.web.Trend;

public class WindTrendViewModel extends ViewModel {

    public void setStation(String station) {
        this.station = station;
    }

    private String station = "";

    private TrendDao trendDao;

    private MutableLiveData<String> displayedStationName;
    private MutableLiveData<String> lastMeasuremenetTime;

    private MutableLiveData<String> currentMeanValue;
    private MutableLiveData<String> twoHoursMeanValue;
    private MutableLiveData<String> fourHoursMeanValue;
    private MutableLiveData<String> sixHoursMeanValue;
    private MutableLiveData<String> eightHoursMeanValue;

    private MutableLiveData<String> currentGustValue;
    private MutableLiveData<String> twoHoursGustValue;
    private MutableLiveData<String> fourHoursGustValue;
    private MutableLiveData<String> sixHoursGustValue;
    private MutableLiveData<String> eightHoursGustValue;

    public MutableLiveData<String> getDisplayedStationName() {
        return displayedStationName;
    }

    public MutableLiveData<String> getLastMeasuremenetTime() {
        return lastMeasuremenetTime;
    }

    public MutableLiveData<String> getCurrentMeanValue() {
        return currentMeanValue;
    }

    public MutableLiveData<String> getTwoHoursMeanValue() {
        return twoHoursMeanValue;
    }

    public MutableLiveData<String> getFourHoursMeanValue() {
        return fourHoursMeanValue;
    }

    public MutableLiveData<String> getSixHoursMeanValue() {
        return sixHoursMeanValue;
    }

    public MutableLiveData<String> getEightHoursMeanValue() {
        return eightHoursMeanValue;
    }

    public MutableLiveData<String> getCurrentGustValue() {
        return currentGustValue;
    }

    public MutableLiveData<String> getTwoHoursGustValue() {
        return twoHoursGustValue;
    }

    public MutableLiveData<String> getFourHoursGustValue() {
        return fourHoursGustValue;
    }

    public MutableLiveData<String> getSixHoursGustValue() {
        return sixHoursGustValue;
    }

    public MutableLiveData<String> getEightHoursGustValue() {
        return eightHoursGustValue;
    }



    public WindTrendViewModel() {
        trendDao = new TrendDao();

        displayedStationName = new MutableLiveData<String>();
        lastMeasuremenetTime = new MutableLiveData<String>();

        currentMeanValue = new MutableLiveData<String>();
        twoHoursMeanValue = new MutableLiveData<String>();
        fourHoursMeanValue = new MutableLiveData<String>();
        sixHoursMeanValue = new MutableLiveData<String>();
        eightHoursMeanValue = new MutableLiveData<String>();

        currentGustValue = new MutableLiveData<String>();
        twoHoursGustValue = new MutableLiveData<String>();
        fourHoursGustValue = new MutableLiveData<String>();
        sixHoursGustValue = new MutableLiveData<String>();
        eightHoursGustValue = new MutableLiveData<String>();

    }

    public boolean updateData() {
        if (station != null && station.length() > 0) {
            Trend trend = trendDao.getStationTrend(station);

            if (trend != null) {

                // format the time and date according to current locale
                String dt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT).format(LocalDateTime.ofEpochSecond(trend.last_timestamp, 0, ZoneOffset.UTC).atOffset(ZoneOffset.UTC).atZoneSameInstant(ZoneOffset.systemDefault()));

                lastMeasuremenetTime.postValue(dt);
                displayedStationName.postValue(trend.displayed_name);

                if (!trend.current_wind_qf.equals("NOT_AVALIABLE") && !trend.current_wind_qf.equals("NO_DATA")) {
                    currentMeanValue.postValue(String.format("%sm/s", trend.average_wind_speed_trend.getCurrentVal(true, false)));
                    twoHoursMeanValue.postValue(String.format("%sm/s", trend.average_wind_speed_trend.getTwoHoursVal(true, false)));
                    fourHoursMeanValue.postValue(String.format("%sm/s", trend.average_wind_speed_trend.getFourHoursVal(true, false)));
                    sixHoursMeanValue.postValue(String.format("%sm/s", trend.average_wind_speed_trend.getSixHoursVal(true, false)));
                    eightHoursMeanValue.postValue(String.format("%sm/s", trend.average_wind_speed_trend.getEightHoursVal(true, false)));

                    currentGustValue.postValue(String.format("%sm/s", trend.maximum_wind_speed_trend.getCurrentVal(true, false)));
                    twoHoursGustValue.postValue(String.format("%sm/s", trend.maximum_wind_speed_trend.getTwoHoursVal(true, false)));
                    fourHoursGustValue.postValue(String.format("%sm/s", trend.maximum_wind_speed_trend.getFourHoursVal(true, false)));
                    sixHoursGustValue.postValue(String.format("%sm/s", trend.maximum_wind_speed_trend.getSixHoursVal(true, false)));
                    eightHoursGustValue.postValue(String.format("%sm/s", trend.maximum_wind_speed_trend.getEightHoursVal(true, false)));

//                    if (AppConfiguration.replaceMsWithKnots) {
//                        // if knots
//                        currentMeanValue.postValue(String.format("%.0f kts", trend.average_wind_speed_trend.current_value));
//                        twoHoursMeanValue.postValue(String.format("%.0f kts", trend.average_wind_speed_trend.two_hours_value));
//                        fourHoursMeanValue.postValue(String.format("%.0f kts", trend.average_wind_speed_trend.four_hours_value));
//                        sixHoursMeanValue.postValue(String.format("%.0f kts", trend.average_wind_speed_trend.six_hours_value));
//                        eightHoursMeanValue.postValue(String.format("%.0f kts", trend.average_wind_speed_trend.eight_hours_value));
//
//                        currentGustValue.postValue(String.format("%.0f kts", trend.maximum_wind_speed_trend.current_value));
//                        twoHoursGustValue.postValue(String.format("%.0f kts", trend.maximum_wind_speed_trend.two_hours_value));
//                        fourHoursGustValue.postValue(String.format("%.0f kts", trend.maximum_wind_speed_trend.four_hours_value));
//                        sixHoursGustValue.postValue(String.format("%.0f kts", trend.maximum_wind_speed_trend.six_hours_value));
//                        eightHoursGustValue.postValue(String.format("%.0f kts", trend.maximum_wind_speed_trend.eight_hours_value));
//                    } else {
//                        // if meters per second
//                        currentMeanValue.postValue(String.format("%.1f m/s", trend.average_wind_speed_trend.current_value));
//                        twoHoursMeanValue.postValue(String.format("%.1f m/s", trend.average_wind_speed_trend.two_hours_value));
//                        fourHoursMeanValue.postValue(String.format("%.1f m/s", trend.average_wind_speed_trend.four_hours_value));
//                        sixHoursMeanValue.postValue(String.format("%.1f m/s", trend.average_wind_speed_trend.six_hours_value));
//                        eightHoursMeanValue.postValue(String.format("%.1f m/s", trend.average_wind_speed_trend.eight_hours_value));
//
//                        currentGustValue.postValue(String.format("%.1f m/s", trend.maximum_wind_speed_trend.current_value));
//                        twoHoursGustValue.postValue(String.format("%.1f m/s", trend.maximum_wind_speed_trend.two_hours_value));
//                        fourHoursGustValue.postValue(String.format("%.1f m/s", trend.maximum_wind_speed_trend.four_hours_value));
//                        sixHoursGustValue.postValue(String.format("%.1f m/s", trend.maximum_wind_speed_trend.six_hours_value));
//                        eightHoursGustValue.postValue(String.format("%.1f m/s", trend.maximum_wind_speed_trend.eight_hours_value));
//                    }
                } else {
                    currentMeanValue.postValue("--");
                    twoHoursMeanValue.postValue("--");
                    fourHoursMeanValue.postValue("--");
                    sixHoursMeanValue.postValue("--");
                    eightHoursMeanValue.postValue("--");

                    currentGustValue.postValue("--");
                    twoHoursGustValue.postValue("--");
                    fourHoursGustValue.postValue("--");
                    sixHoursGustValue.postValue("--");
                    eightHoursGustValue.postValue("--");
                }
                return true;
            } else {
                currentMeanValue.postValue("--");
                twoHoursMeanValue.postValue("--");
                fourHoursMeanValue.postValue("--");
                sixHoursMeanValue.postValue("--");
                eightHoursMeanValue.postValue("--");

                currentGustValue.postValue("--");
                twoHoursGustValue.postValue("--");
                fourHoursGustValue.postValue("--");
                sixHoursGustValue.postValue("--");
                eightHoursGustValue.postValue("--");
                return false;
            }
        }

        return false;
    }

}