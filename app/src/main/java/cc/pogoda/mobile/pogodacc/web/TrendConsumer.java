package cc.pogoda.mobile.pogodacc.web;

import cc.pogoda.mobile.pogodacc.type.web.Trend;
import retrofit2.Call;
import retrofit2.http.Query;

public interface TrendConsumer {

    Call<Trend> getTrendForStation(@Query("station")String station);
}
