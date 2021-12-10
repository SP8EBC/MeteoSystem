package cc.pogoda.mobile.meteosystem.web;


import cc.pogoda.mobile.meteosystem.type.web.Summary;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SummaryConsumer {

    @GET("meteo_backend/station/{stationName}/summary/")
    Call<Summary> getSummaryForStation(@Path("stationName")String station);
}
