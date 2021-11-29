package cc.pogoda.mobile.pogodacc.web;


import cc.pogoda.mobile.pogodacc.type.web.Summary;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SummaryConsumer {

    @GET("meteo_backend/station/{stationName}/summary/")
    Call<Summary> getSummaryForStation(@Path("stationName")String station);
}
