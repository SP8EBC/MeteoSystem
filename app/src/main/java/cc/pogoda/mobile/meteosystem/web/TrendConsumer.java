package cc.pogoda.mobile.meteosystem.web;

import cc.pogoda.mobile.meteosystem.type.web.Trend;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TrendConsumer {

    @GET("meteo_backend/station/{stationName}/trend/")
    Call<Trend> getTrendForStation(@Path("stationName")String station);
}
