package cc.pogoda.mobile.pogodacc.web;

import cc.pogoda.mobile.pogodacc.type.web.Trend;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TrendConsumer {

    @GET("meteo_backend/trend/")
    Call<Trend> getTrendForStation(@Query("station")String station);
}
