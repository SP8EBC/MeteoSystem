package cc.pogoda.mobile.meteosystem.type;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;

public class AllStationsReceivedEvent {
    List<WeatherStation> stations;

    public AllStationsReceivedEvent(@NonNull List<WeatherStation> stations) {
        this.stations = stations;
    }

    @NonNull
    public List<WeatherStation> getStations(){
        return this.stations;
    }

    @NonNull
    @Override
    public String toString() {
        return "[AllStationsReceivedEvent][stations.size() = " + stations.size() +"]";
    }
}
