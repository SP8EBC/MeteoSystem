package cc.pogoda.mobile.pogodacc.dao;

import cc.pogoda.mobile.pogodacc.type.web.ListOfStationData;
import cc.pogoda.mobile.pogodacc.web.LastStationDataConsumer;
import cc.pogoda.mobile.pogodacc.web.RestClientConfig;
import cc.pogoda.mobile.pogodacc.web.StationListConsumer;
import retrofit2.Response;

public class LastStationDataDao {
    RestClientConfig restClient;

    Response<ListOfStationData> response = null;

    String station;

    class Worker implements Runnable {

        @Override
        public void run() {
            restClient = new RestClientConfig();

            LastStationDataConsumer consumer = restClient.getWeatherStationClient().create(LastStationDataConsumer.class);

            try {
                response = consumer.getLastDataForStation(station, true, true).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ListOfStationData getLastStationData(String station) {

        ListOfStationData out = null;

        this.station = station;

        Thread worker = new Thread(new Worker());

        worker.start();

        try {
            worker.join();

            if (response != null) {
                out = response.body();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return  out;
    }
}
