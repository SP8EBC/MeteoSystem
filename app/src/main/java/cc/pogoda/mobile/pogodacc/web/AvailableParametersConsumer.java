package cc.pogoda.mobile.pogodacc.web;

import cc.pogoda.mobile.pogodacc.type.web.AvailableParametersWeb;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AvailableParametersConsumer {

    @GET("meteo_backend/station/{name}/availableParameters")
    Call<AvailableParametersWeb> getParametersForStation(@Path("name") String name);
}
