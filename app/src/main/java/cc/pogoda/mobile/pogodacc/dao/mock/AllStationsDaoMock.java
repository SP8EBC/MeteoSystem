package cc.pogoda.mobile.pogodacc.dao.mock;

import java.util.LinkedList;
import java.util.List;

import cc.pogoda.mobile.pogodacc.type.WeatherStation;

public class AllStationsDaoMock {

    public List<WeatherStation> getAllStations() {

        LinkedList<WeatherStation> out = new LinkedList<>();

        out.add(new WeatherStation("Skrzyczne"));
        out.add(new WeatherStation("Magurka"));

        return out;
    }
}
