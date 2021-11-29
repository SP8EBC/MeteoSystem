package cc.pogoda.mobile.pogodacc.web;

import cc.pogoda.mobile.pogodacc.type.web.ListOfStationData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LastStationDataConsumer {

    @GET("meteo_backend/station/{stationName}/lastStationData/")
    Call<ListOfStationData> getLastDataForStation(@Path("stationName")String station,
                                                  @Query("ascendingOrder")boolean ascendingOrder,
                                                  @Query("isLong")boolean isLong);
}
