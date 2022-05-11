package cc.pogoda.mobile.meteosystem.web;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.tinylog.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import cc.pogoda.mobile.meteosystem.type.WeatherStation;

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
            URL url = new java.net.URL(station.getImageUrl());

            Logger.debug("[url = " + url.toString() +"]");

            in = url.openStream();
            bitmap = BitmapFactory.decodeStream(in);

            in.close();

        } catch (IOException e) {
            Logger.error("[IOException][e = " + e.getLocalizedMessage() +"]");
            e.printStackTrace();
            bitmap = null;
        }
    }
}
