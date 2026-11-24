package cc.pogoda.mobile.meteosystem.web;

import cc.pogoda.mobile.meteosystem.type.web.HistogramDirection;
import cc.pogoda.mobile.meteosystem.type.web.HistogramSpeed;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Histograms of wind parameters. 'from' and 'to' are UNIX epoch timestamps (UTC) and are
 * optional: pass null (or zero) to get a histogram for the last 2 hours. The maximum
 * period is 24 hours; a longer one is answered with HTTP 416.
 */
public interface HistogramConsumer {

    @GET("meteo_backend/station/{stationName}/histogram/direction")
    Call<HistogramDirection> getDirectionHistogramForStation(@Path("stationName")String station,
                                                             @Query("from")Long from,
                                                             @Query("to")Long to);

    @GET("meteo_backend/station/{stationName}/histogram/speed")
    Call<HistogramSpeed> getSpeedHistogramForStation(@Path("stationName")String station,
                                                     @Query("from")Long from,
                                                     @Query("to")Long to);

    @GET("meteo_backend/station/{stationName}/histogram/gusts")
    Call<HistogramSpeed> getGustsHistogramForStation(@Path("stationName")String station,
                                                     @Query("from")Long from,
                                                     @Query("to")Long to);
}
