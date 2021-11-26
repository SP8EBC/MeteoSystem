package cc.pogoda.mobile.pogodacc.web;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

import cc.pogoda.mobile.pogodacc.type.WeatherStation;

public class StationBackgroundDownloader implements Runnable {

    private WeatherStation station;

    public Bitmap getBitmap() {
        return bitmap;
    }

    private Bitmap bitmap;

    public StationBackgroundDownloader(WeatherStation _wx_station) {
        station = _wx_station;

        bitmap = null;
    }

    @Override
    public void run() {
        InputStream in = null;
        try {
            in = new java.net.URL(station.getImageUrl()).openStream();
            bitmap = BitmapFactory.decodeStream(in);

        } catch (IOException e) {
            e.printStackTrace();
            bitmap = null;
        }
    }
}
