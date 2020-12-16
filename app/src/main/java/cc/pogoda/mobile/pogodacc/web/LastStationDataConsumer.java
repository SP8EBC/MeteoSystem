package cc.pogoda.mobile.pogodacc.web;

import cc.pogoda.mobile.pogodacc.type.web.ListOfStationData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LastStationDataConsumer {

    @GET("meteo_backend/lastStationData/")
    Call<ListOfStationData> getLastDataForStation(@Query("station")String station,
                                                  @Query("ascendingOrder")boolean ascendingOrder,
                                                  @Query("isLong")boolean isLong);
}
