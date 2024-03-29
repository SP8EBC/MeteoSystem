package cc.pogoda.mobile.meteosystem.dao;

import static cc.pogoda.mobile.meteosystem.config.WebIoConfig.TIMEOUT_SECOND;

import org.tinylog.Logger;

import java.io.IOException;

import cc.pogoda.mobile.meteosystem.type.web.Trend;
import cc.pogoda.mobile.meteosystem.web.RestClientConfig;
import cc.pogoda.mobile.meteosystem.web.TrendConsumer;
import retrofit2.Response;

public class TrendDao {

    Response<Trend> trend;

    String station = null;

    class Worker implements Runnable {

        RestClientConfig restClient;

        @Override
        public void run() {

            restClient = new RestClientConfig();

            TrendConsumer trendConsumer = restClient.getWeatherStationClient().create(TrendConsumer.class);

            try {
                trend = trendConsumer.getTrendForStation(station).execute();
            }
            catch (IOException e) {
                Logger.error("[TrendDao][Worker][Exception][e = " + e.getLocalizedMessage() +"]");

                e.printStackTrace();
            }
        }
    }

    public Trend getStationTrend(String station) {
        Trend out = new Trend();

        this.station = station;

        Thread thread = new Thread(new Worker());

        thread.start();
        try {
            thread.join();

            if (trend != null) {
                out = trend.body();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }

        return out;
    }
}
