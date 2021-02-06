package cc.pogoda.mobile.pogodacc.activity.trend.temperature;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import cc.pogoda.mobile.pogodacc.dao.TrendDao;
import cc.pogoda.mobile.pogodacc.type.web.Trend;

public class TemperatureTrendViewModel extends ViewModel {

    public void setStation(String station) {
        this.station = station;
    }

    private String station = "";

    private TrendDao trendDao;

    private MutableLiveData<String> displayedStationName;

    public MutableLiveData<String> getDisplayedStationName() {
        return displayedStationName;
    }

    public MutableLiveData<String> getLastMeasuremenetTime() {
        return lastMeasuremenetTime;
    }

    public MutableLiveData<String> getCurrentTemperatureValue() {
        return currentTemperatureValue;
    }

    public MutableLiveData<String> getTwoHoursTemperatureValue() {
        return twoHoursTemperatureValue;
    }

    public MutableLiveData<String> getFourHoursTemperatureValue() {
        return fourHoursTemperatureValue;
    }

    public MutableLiveData<String> getSixHoursTemperatureValue() {
        return sixHoursTemperatureValue;
    }

    public MutableLiveData<String> getEightHoursTemperatureValue() {
        return eightHoursTemperatureValue;
    }

    public MutableLiveData<String> getCurrentHumidityValue() {
        return currentHumidityValue;
    }

    public MutableLiveData<String> getTwoHoursHumidityValue() {
        return twoHoursHumidityValue;
    }

    public MutableLiveData<String> getFourHoursHumidityValue() {
        return fourHoursHumidityValue;
    }

    public MutableLiveData<String> getSixHoursHumidityValue() {
        return sixHoursHumidityValue;
    }

    public MutableLiveData<String> getEightHoursHumidityValue() {
        return eightHoursHumidityValue;
    }

    private MutableLiveData<String> lastMeasuremenetTime;

    private MutableLiveData<String> currentTemperatureValue;
    private MutableLiveData<String> twoHoursTemperatureValue;
    private MutableLiveData<String> fourHoursTemperatureValue;
    private MutableLiveData<String> sixHoursTemperatureValue;
    private MutableLiveData<String> eightHoursTemperatureValue;

    private MutableLiveData<String> currentHumidityValue;
    private MutableLiveData<String> twoHoursHumidityValue;
    private MutableLiveData<String> fourHoursHumidityValue;
    private MutableLiveData<String> sixHoursHumidityValue;
    private MutableLiveData<String> eightHoursHumidityValue;

    public TemperatureTrendViewModel() {
        trendDao = new TrendDao();

        lastMeasuremenetTime = new MutableLiveData<String>();
        displayedStationName = new MutableLiveData<String>();

        currentTemperatureValue = new MutableLiveData<String>();
        twoHoursTemperatureValue = new MutableLiveData<String>();
        fourHoursTemperatureValue = new MutableLiveData<String>();
        sixHoursTemperatureValue = new MutableLiveData<String>();
        eightHoursTemperatureValue = new MutableLiveData<String>();

        currentHumidityValue = new MutableLiveData<String>();
        twoHoursHumidityValue = new MutableLiveData<String>();
        fourHoursHumidityValue = new MutableLiveData<String>();
        sixHoursHumidityValue = new MutableLiveData<String>();
        eightHoursHumidityValue = new MutableLiveData<String>();
    }

    public boolean getData() {
        Trend trend = trendDao.getStationTrend(this.station);

        if (trend != null) {
            // format the time and date according to current locale
            String dt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT).format(LocalDateTime.ofEpochSecond(trend.last_timestamp, 0, ZoneOffset.UTC).atOffset(ZoneOffset.UTC).atZoneSameInstant(ZoneOffset.systemDefault()));

            lastMeasuremenetTime.postValue(dt);
            displayedStationName.postValue(trend.displayed_name);

            if (!trend.current_humidity_qf.equals("NOT_AVALIABLE") && !trend.current_humidity_qf.equals("NO_DATA")) {


                currentHumidityValue.postValue(String.format("%.1f %%", trend.humidity_trend.current_value));
                twoHoursHumidityValue.postValue(String.format("%.1f %%", trend.humidity_trend.two_hours_value));
                fourHoursHumidityValue.postValue(String.format("%.1f %%", trend.humidity_trend.four_hours_value));
                sixHoursHumidityValue.postValue(String.format("%.1f %%", trend.humidity_trend.six_hours_value));
                eightHoursHumidityValue.postValue(String.format("%.1f %%", trend.humidity_trend.eight_hours_value));
            }
            else {


                currentHumidityValue.postValue("-- %");
                twoHoursHumidityValue.postValue("-- %");
                fourHoursHumidityValue.postValue("-- %");
                sixHoursHumidityValue.postValue("-- %");
                eightHoursHumidityValue.postValue("-- %");

            }

            if (!trend.current_temperature_qf.equals("NOT_AVALIABLE") && !trend.current_temperature_qf.equals("NO_DATA")) {
                currentTemperatureValue.postValue(String.format("%.1f °C", trend.temperature_trend.current_value));
                twoHoursTemperatureValue.postValue(String.format("%.1f °C", trend.temperature_trend.two_hours_value));
                fourHoursTemperatureValue.postValue(String.format("%.1f °C", trend.temperature_trend.four_hours_value));
                sixHoursTemperatureValue.postValue(String.format("%.1f °C", trend.temperature_trend.six_hours_value));
                eightHoursTemperatureValue.postValue(String.format("%.1f °C", trend.temperature_trend.eight_hours_value));
            }
            else {
                currentTemperatureValue.postValue("-- °C");
                twoHoursTemperatureValue.postValue("-- °C");
                fourHoursTemperatureValue.postValue("-- °C");
                sixHoursTemperatureValue.postValue("-- °C");
                eightHoursTemperatureValue.postValue("-- °C");
            }
            return true;
        }
        else {
            currentTemperatureValue.postValue("-- °C");
            twoHoursTemperatureValue.postValue("-- °C");
            fourHoursTemperatureValue.postValue("-- °C");
            sixHoursTemperatureValue.postValue("-- °C");
            eightHoursTemperatureValue.postValue("-- °C");

            currentHumidityValue.postValue("-- %");
            twoHoursHumidityValue.postValue("-- %");
            fourHoursHumidityValue.postValue("-- %");
            sixHoursHumidityValue.postValue("-- %");
            eightHoursHumidityValue.postValue("-- %");
            return false;
        }
    }


}