package cc.pogoda.mobile.pogodacc.dao;

import java.util.LinkedList;
import java.util.List;

import cc.pogoda.mobile.pogodacc.type.AvailableParameters;
import cc.pogoda.mobile.pogodacc.type.WeatherStation;
import cc.pogoda.mobile.pogodacc.type.web.ListOfAllStations;
import cc.pogoda.mobile.pogodacc.type.web.StationDefinition;
import cc.pogoda.mobile.pogodacc.web.RestClientConfig;
import cc.pogoda.mobile.pogodacc.web.StationListConsumer;
import retrofit2.Response;

public class AllStationsDao {

    RestClientConfig restClient;

    ListOfAllStations intermediate;

    Response<ListOfAllStations> resp = null;

    class Worker implements Runnable {

        @Override
        public void run() {
            restClient = new RestClientConfig();

            StationListConsumer consumer = restClient.getWeatherStationClient().create(StationListConsumer.class);

            try {
                resp = consumer.getAllStations().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<WeatherStation> getAllStations() {
        List<WeatherStation> out = null;

        Thread worker = new Thread(new Worker());

        worker.start();

        try {
            worker.join();

            if (resp != null) {
                intermediate = resp.body();

                if (intermediate != null) {

                    out = new LinkedList<WeatherStation>();

                    for (StationDefinition def : intermediate.stations) {
                        if (def.enabled) {
                            WeatherStation elem = new WeatherStation();

                            elem.setSystemName(def.name);
                            elem.setDisplayedLocation(def.displayedLocation);
                            elem.setDisplayedName(def.displayedName);
                            elem.setLat(def.lat);
                            elem.setLon(def.lon);
                            elem.setSponsorUrl(def.sponsorUrl);
                            elem.setImageUrl(def.backgroundJpg);
                            elem.setStationNameTextColor(def.stationNameTextColour);
                            elem.setImageAlign(def.backgroundJpgAlign);
                            elem.setMoreInfo(def.moreInfo);

                            AvailableParameters availableParameters = AvailableParameters.fromStation(def);
                            elem.setAvailableParameters(availableParameters);

                            out.add(elem);
                        }
                    }
                }

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return out;
    }

}
