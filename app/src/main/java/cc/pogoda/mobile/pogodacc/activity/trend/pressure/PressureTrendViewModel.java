package cc.pogoda.mobile.pogodacc.activity.trend.pressure;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import cc.pogoda.mobile.pogodacc.dao.TrendDao;
import cc.pogoda.mobile.pogodacc.type.web.Trend;

public class PressureTrendViewModel extends ViewModel {

    private MutableLiveData<String> stationName;
    private MutableLiveData<String> lastMeasuremenetTime;
    private MutableLiveData<String> currentValue;
    private MutableLiveData<String> twoHoursValue;
    private MutableLiveData<String> fourHoursValue;
    private MutableLiveData<String> sixHoursValue;
    private MutableLiveData<String> eightHoursValue;

    private TrendDao trendDao;

    private String station = "";

    public PressureTrendViewModel() {
        stationName = new MutableLiveData<>();
        lastMeasuremenetTime = new MutableLiveData<>();
        currentValue = new MutableLiveData<>();
        twoHoursValue = new MutableLiveData<>();
        fourHoursValue = new MutableLiveData<>();
        sixHoursValue = new MutableLiveData<>();
        eightHoursValue = new MutableLiveData<>();

        trendDao = new TrendDao();
    }

    public boolean updateData() {
        Trend trend = trendDao.getStationTrend(station);

        if (trend != null) {
            // format the time and date according to current locale
            String dt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT).format(LocalDateTime.ofEpochSecond(trend.last_timestamp, 0, ZoneOffset.UTC).atOffset(ZoneOffset.UTC).atZoneSameInstant(ZoneOffset.systemDefault()));

            lastMeasuremenetTime.postValue(dt);
            stationName.postValue(trend.displayed_name);

            if (!trend.current_qnh_qf.equals("NOT_AVALIABLE") && !trend.current_qnh_qf.equals("NO_DATA")) {
                currentValue.postValue(String.format("%shPa", trend.pressure_trend.getCurrentVal(true, true)));
                twoHoursValue.postValue(String.format("%shPa", trend.pressure_trend.getTwoHoursVal(true, true)));
                fourHoursValue.postValue(String.format("%shPa", trend.pressure_trend.getFourHoursVal(true, true)));
                sixHoursValue.postValue(String.format("%shPa", trend.pressure_trend.getSixHoursVal(true, true)));
                eightHoursValue.postValue(String.format("%shPa", trend.pressure_trend.getEightHoursVal(true, true)));
            }
            else {
                currentValue.postValue("-- hPa");
                twoHoursValue.postValue("-- hPa");
                fourHoursValue.postValue("-- hPa");
                sixHoursValue.postValue("-- hPa");
                eightHoursValue.postValue("-- hPa");
            }
            return true;
        }
        else {
            currentValue.postValue("-- hPa");
            twoHoursValue.postValue("-- hPa");
            fourHoursValue.postValue("-- hPa");
            sixHoursValue.postValue("-- hPa");
            eightHoursValue.postValue("-- hPa");
            return false;
        }
    }

    public void setStation(String station) {
        this.station = station;
    }

    public LiveData<String> getStationName() {
        return stationName;
    }

    public LiveData<String> getLastMeasuremenetTime() {
        return lastMeasuremenetTime;
    }

    public LiveData<String> getCurrentValue() {
        return currentValue;
    }

    public LiveData<String> getTwoHoursValue() {
        return twoHoursValue;
    }

    public LiveData<String> getFourHoursValue() {
        return fourHoursValue;
    }

    public LiveData<String> getSixHoursValue() {
        return sixHoursValue;
    }

    public LiveData<String> getEightHoursValue() {
        return eightHoursValue;
    }
}