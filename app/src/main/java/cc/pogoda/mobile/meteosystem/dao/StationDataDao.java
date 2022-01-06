package cc.pogoda.mobile.meteosystem.dao;

import org.tinylog.Logger;

import cc.pogoda.mobile.meteosystem.type.web.ListOfStationData;
import cc.pogoda.mobile.meteosystem.web.RestClientConfig;
import cc.pogoda.mobile.meteosystem.web.StationDataConsumer;
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
                Logger.error("[StationDataDao][Worker][Exception][e = " + e.getLocalizedMessage() +"]");

                e.printStackTrace();
            }
        }
    }


    public ListOfStationData getLastStationData(String station, long timestampFrom, long timestampTo) {

        ListOfStationData out = null;

        this.from = timestampFrom;
        this.to = timestampTo;
        this.station = station;

        Worker worker = new Worker();

        Thread thread = new Thread(worker);

        try {
            thread.start();
            thread.join();

            out = response.body();
        }
        catch (InterruptedException ex) {

        }

        return out;

    }
}
