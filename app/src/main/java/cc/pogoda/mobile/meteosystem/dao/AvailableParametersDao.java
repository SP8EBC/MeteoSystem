package cc.pogoda.mobile.meteosystem.dao;

import org.tinylog.Logger;

import java.io.IOException;

import cc.pogoda.mobile.meteosystem.type.web.AvailableParametersWeb;
import cc.pogoda.mobile.meteosystem.web.AvailableParametersConsumer;
import cc.pogoda.mobile.meteosystem.web.RestClientConfig;
import retrofit2.Response;

public class AvailableParametersDao {

    RestClientConfig restClient;

    Response<AvailableParametersWeb> response = null;

    String stationName;

    class Worker implements Runnable {

        @Override
        public void run() {
            restClient = new RestClientConfig();

            AvailableParametersConsumer consumer = restClient.getWeatherStationClient().create(AvailableParametersConsumer.class);

            try {
                response = consumer.getParametersForStation(stationName).execute();
            } catch (IOException e) {
                Logger.error("[AvailableParametersDao][Worker][IOException][e = " + e.getLocalizedMessage() +"]");

                e.printStackTrace();
            } catch (RuntimeException e) {
                Logger.error("[AvailableParametersDao][Worker][RuntimeException][e = " + e.getLocalizedMessage() +"]");

                e.printStackTrace();
            }
            catch (Exception e) {
                Logger.error("[AvailableParametersDao][Worker][Exception][e = " + e.getLocalizedMessage() +"]");

                e.printStackTrace();
            }

            if (response == null) {
                Logger.error("[AvailableParametersDao][Worker][worker is done, response is null]");
            }
            else {
                Logger.debug("[AvailableParametersDao][Worker][worker is done][response.code() = " + response.code() +"]");
            }
        }
    }

    public AvailableParametersWeb getAvaliableParamsByStationName(String name) {
        AvailableParametersWeb out = null;

        stationName = name;

        Thread t = new Thread(new Worker());

        t.start();

        try {
            t.join();

            out = response.body();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return out;
    }
}
