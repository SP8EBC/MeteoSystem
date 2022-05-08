package cc.pogoda.mobile.meteosystem.dao;

import static cc.pogoda.mobile.meteosystem.config.WebIoConfig.TIMEOUT_SECOND;

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
                Logger.error("[IOException][e = " + e.getLocalizedMessage() +"]");

                e.printStackTrace();
            } catch (RuntimeException e) {
                Logger.error("[RuntimeException][e = " + e.getLocalizedMessage() +"]");

                e.printStackTrace();
            }
            catch (Exception e) {
                Logger.error("[Exception][e = " + e.getLocalizedMessage() +"]");

                e.printStackTrace();
            }

            if (response == null) {
                Logger.error("[worker is done, response is null]");
            }
            else {
                Logger.info("[worker is done][response.code() = " + response.code() +"]");
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

            if (response != null) {
                out = response.body();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return out;
    }
}
