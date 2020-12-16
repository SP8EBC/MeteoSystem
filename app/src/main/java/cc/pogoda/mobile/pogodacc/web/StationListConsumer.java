package cc.pogoda.mobile.pogodacc.web;

import java.util.List;

import cc.pogoda.mobile.pogodacc.type.web.ListOfAllStations;
import retrofit2.Call;
import retrofit2.http.GET;

public interface StationListConsumer {

    @GET("meteo_backend/listOfAllStations/")
    Call<ListOfAllStations> getAllStations();
}
