package cc.pogoda.mobile.meteosystem.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import cc.pogoda.mobile.meteosystem.type.CustomLocalDateTime;
import cc.pogoda.mobile.meteosystem.web.deserializer.CustomLocalDateTimeDeserializer;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClientConfig {

    public Retrofit getWeatherStationClient() {
        Retrofit out = null;

        Gson gson = new GsonBuilder().registerTypeAdapter(CustomLocalDateTime.class, new CustomLocalDateTimeDeserializer()).setLenient().create();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        builder.connectTimeout(20, TimeUnit.SECONDS);
        builder.callTimeout(20, TimeUnit.SECONDS);

        OkHttpClient client = builder.build();//new OkHttpClient(builder);

        out = new Retrofit.Builder().baseUrl("http://pogoda.cc:8080/").addConverterFactory(GsonConverterFactory.create(gson)).client(client).build();

        return out;
    }
}
