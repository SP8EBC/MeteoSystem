package cc.pogoda.mobile.pogodacc.dao;

import cc.pogoda.mobile.pogodacc.type.web.ListOfStationData;
import cc.pogoda.mobile.pogodacc.web.LastStationDataConsumer;
import cc.pogoda.mobile.pogodacc.web.RestClientConfig;
import cc.pogoda.mobile.pogodacc.web.StationDataConsumer;
import retrofit2.Response;

public class StationDataDao {

    RestClientConfig restClient;

    Response<ListOfStationData> response = null;

    String station;

    long from, to;

    class Worker implements Runnable {

        @Override
        public void run() {
            restClient = new RestClientConfig();

            StationDataConsumer consumer = restClient.getWeatherStationClient().create(StationDataConsumer.class);

            try {
                response = consumer.getDataForStation(station, from, to).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public ListOfStationData getLastStationData(String station, long timestampFrom, long timestampTo) {

        this.from = timestampFrom;
        this.to = timestampTo;

        return null;

    }
}
