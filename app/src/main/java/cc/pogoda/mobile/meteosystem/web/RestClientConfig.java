package cc.pogoda.mobile.meteosystem.web;

import static cc.pogoda.mobile.meteosystem.config.WebIoConfig.TIMEOUT_SECOND;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import cc.pogoda.mobile.meteosystem.type.CustomLocalDateTime;
import cc.pogoda.mobile.meteosystem.web.deserializer.CustomLocalDateTimeDeserializer;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClientConfig {

    public Retrofit getWeatherStationClient() {
        Retrofit out = null;

        Gson gson = new GsonBuilder().registerTypeAdapter(CustomLocalDateTime.class, new CustomLocalDateTimeDeserializer()).setLenient().create();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.readTimeout(TIMEOUT_SECOND, TimeUnit.SECONDS);
        builder.writeTimeout(TIMEOUT_SECOND, TimeUnit.SECONDS);
        builder.connectTimeout(TIMEOUT_SECOND, TimeUnit.SECONDS);
        builder.callTimeout(TIMEOUT_SECOND, TimeUnit.SECONDS);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        OkHttpClient client = builder.addInterceptor(loggingInterceptor).build();//new OkHttpClient(builder);

        out = new Retrofit.Builder().baseUrl("http://pogoda.cc:8080/").addConverterFactory(GsonConverterFactory.create(gson)).client(client).build();

        return out;
    }
}
