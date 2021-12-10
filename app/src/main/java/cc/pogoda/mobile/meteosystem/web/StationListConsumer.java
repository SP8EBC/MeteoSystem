package cc.pogoda.mobile.meteosystem.web;

import cc.pogoda.mobile.meteosystem.type.web.ListOfAllStations;
import retrofit2.Call;
import retrofit2.http.GET;

public interface StationListConsumer {

    @GET("meteo_backend/listOfAllStations/")
    Call<ListOfAllStations> getAllStations();
}
