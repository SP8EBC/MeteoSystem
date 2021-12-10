package cc.pogoda.mobile.meteosystem.dao;

import java.io.IOException;

import cc.pogoda.mobile.meteosystem.type.web.QualityFactor;
import cc.pogoda.mobile.meteosystem.type.web.Summary;
import cc.pogoda.mobile.meteosystem.web.RestClientConfig;
import cc.pogoda.mobile.meteosystem.web.SummaryConsumer;
import retrofit2.Response;

/**
 * This DAO downloads the measurements summary data for wx station given by its name
 */
public class SummaryDao {

    RestClientConfig restClient;

    Response<Summary> response = null;

    String station;

    class Worker implements Runnable {

        @Override
        public void run() {
            // create a new instance of factory class. This could be refactored to static invocation
            restClient = new RestClientConfig();

            // create a new instance of Retrofit with OkHttp client with GSON parser
            SummaryConsumer consumer = restClient.getWeatherStationClient().create(SummaryConsumer.class);

            try {
                response = consumer.getSummaryForStation(station).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Summary getStationSummary(String station) {

        Summary out = null;

        this.station = station;

        Thread worker = new Thread(new Worker());

        worker.start();

        try {
            // wait for the web service response
            worker.join();

            // check if web service returned anything
            if (response != null) {
                // if yes get the response body
                out = response.body();

                if (out != null) {
                    // convert all quality factors from string representation to native format
                    out.temperature_qf_native = QualityFactor.valueOf(out.temperature_qf);
                    out.wind_qf_native = QualityFactor.valueOf(out.wind_qf);
                    out.humidity_qf_native = QualityFactor.valueOf(out.humidity_qf);
                    out.qnh_qf_native = QualityFactor.valueOf(out.qnh_qf);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return out;

    }
}
