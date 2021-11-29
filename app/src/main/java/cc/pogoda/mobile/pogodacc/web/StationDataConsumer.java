package cc.pogoda.mobile.pogodacc.web;

import cc.pogoda.mobile.pogodacc.type.web.ListOfStationData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StationDataConsumer {

    @GET("meteo_backend/station/{stationName}/stationData/")
    Call<ListOfStationData> getDataForStation(@Path("stationName")String station,
                                              @Query("from")long from,
                                              @Query("to")long to);
}
