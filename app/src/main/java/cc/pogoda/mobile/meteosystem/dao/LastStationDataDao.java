package cc.pogoda.mobile.meteosystem.dao;

import static cc.pogoda.mobile.meteosystem.config.WebIoConfig.TIMEOUT_SECOND;

import org.tinylog.Logger;

import cc.pogoda.mobile.meteosystem.type.web.ListOfStationData;
import cc.pogoda.mobile.meteosystem.web.LastStationDataConsumer;
import cc.pogoda.mobile.meteosystem.web.RestClientConfig;
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
                Logger.error("[Exception][e = " + e.getLocalizedMessage() +"]");

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
