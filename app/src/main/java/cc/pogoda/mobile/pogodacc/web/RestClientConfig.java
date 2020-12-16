package cc.pogoda.mobile.pogodacc.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cc.pogoda.mobile.pogodacc.type.CustomLocalDateTime;
import cc.pogoda.mobile.pogodacc.web.deserializer.CustomLocalDateTimeDeserializer;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClientConfig {

    public Retrofit getWeatherStationClient() {
        Retrofit out = null;

        Gson gson = new GsonBuilder().registerTypeAdapter(CustomLocalDateTime.class, new CustomLocalDateTimeDeserializer()).setLenient().create();

        OkHttpClient client = new OkHttpClient();

        out = new Retrofit.Builder().baseUrl("http://pogoda.cc:8080/").addConverterFactory(GsonConverterFactory.create(gson)).client(client).build();

        return out;
    }
}
