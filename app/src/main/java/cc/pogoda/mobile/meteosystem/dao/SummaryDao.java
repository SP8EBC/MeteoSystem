package cc.pogoda.mobile.meteosystem.dao;

import static cc.pogoda.mobile.meteosystem.config.WebIoConfig.TIMEOUT_SECOND;

import org.tinylog.Logger;

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
                Logger.info("[station = " + station +"]");

                response = consumer.getSummaryForStation(station).execute();
            } catch (IOException e) {
                Logger.error("[station = " + station + "][IOException][e = " + e.getLocalizedMessage() +"]");

                e.printStackTrace();
            } catch (RuntimeException e) {
                Logger.error("[station = " + station + "][RuntimeException][e = " + e.getLocalizedMessage() +"]");

                e.printStackTrace();
            }
            catch (Exception e) {
                Logger.error("[station = " + station + "][Exception][e = " + e.getLocalizedMessage() +"]");

                e.printStackTrace();
            }

            if (response == null) {
                Logger.error("[station = " + station + "][worker is done, response is null]");
            }
            else {
                Logger.info("[station = " + station + "][worker is done][response.code() = " + response.code() +"]");
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
                else {
                    Logger.error("[station = " + station +"][response.code() = " + response.code() +"][response body is nulll, probably HTTP error" +
                            "]");
                }
            }
            else {
                Logger.error("[station = " + station +"][response is null!!]");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Logger.error("[station = " + station +"][InterruptedException]");
        }

        return out;

    }
}
