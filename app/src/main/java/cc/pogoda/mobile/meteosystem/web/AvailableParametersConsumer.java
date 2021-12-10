package cc.pogoda.mobile.meteosystem.web;

import cc.pogoda.mobile.meteosystem.type.web.AvailableParametersWeb;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AvailableParametersConsumer {

    @GET("meteo_backend/station/{name}/availableParameters")
    Call<AvailableParametersWeb> getParametersForStation(@Path("name") String name);
}
