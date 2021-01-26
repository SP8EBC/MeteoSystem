package cc.pogoda.mobile.pogodacc.dao;

import java.io.IOException;

import cc.pogoda.mobile.pogodacc.type.web.Trend;
import cc.pogoda.mobile.pogodacc.web.RestClientConfig;
import cc.pogoda.mobile.pogodacc.web.TrendConsumer;
import retrofit2.Response;

public class TrendDao {
    RestClientConfig restClient;

    Response<Trend> trend;

    String station = null;

    class Worker implements Runnable {

        @Override
        public void run() {

            restClient = new RestClientConfig();

            TrendConsumer trendConsumer = restClient.getWeatherStationClient().create(TrendConsumer.class);

            try {
                trend = trendConsumer.getTrendForStation(station).execute();
            }
            catch (IOException e) {

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

            out = trend.body();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return out;
    }
}
