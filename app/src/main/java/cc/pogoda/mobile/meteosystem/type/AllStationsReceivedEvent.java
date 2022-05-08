package cc.pogoda.mobile.meteosystem.type;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;

public class AllStationsReceivedEvent {
    List<WeatherStation> stations;

    HashMap<String, AvailableParameters> availableParameters;

    public AllStationsReceivedEvent(@NonNull List<WeatherStation> stations, @NonNull HashMap<String, AvailableParameters> availableParametersHashMap) {
        this.stations = stations;
    }

    @NonNull
    public HashMap<String, AvailableParameters> getAvailableParameters() {
        return availableParameters;
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
