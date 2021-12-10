package cc.pogoda.mobile.meteosystem.type;

import androidx.annotation.NonNull;

public class WeatherStationListEvent {

    public enum EventReason {
        ADD,
        DELETE;
    }

    WeatherStation station;

    EventReason eventReason;

    public WeatherStationListEvent(WeatherStation wx, EventReason reason) {
        station = wx;
        eventReason = reason;
    }

    public WeatherStation getStation() {
        return station;
    }

    public EventReason getEventReason() {
        return eventReason;
    }

    @NonNull
    @Override
    public String toString() {
        return "[station.systemName = " + station.systemName + "][eventReason = " + eventReason + "]";
    }
}
