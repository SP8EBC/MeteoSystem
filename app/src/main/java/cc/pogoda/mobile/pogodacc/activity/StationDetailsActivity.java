package cc.pogoda.mobile.pogodacc.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.activity.handler.StationDetailsActTemperaturePlotButtonClickEvent;
import cc.pogoda.mobile.pogodacc.activity.handler.StationDetailsActWindDirectionPlotsButtonClickEvent;
import cc.pogoda.mobile.pogodacc.activity.handler.StationDetailsActWindSpeedPlotsButtonClickEvent;
import cc.pogoda.mobile.pogodacc.activity.handler.StationDetailsActSummaryButtonClickEvent;
import cc.pogoda.mobile.pogodacc.activity.handler.StationDetailsActWindRoseButtonClickEvent;
import cc.pogoda.mobile.pogodacc.type.WeatherStation;

public class StationDetailsActivity extends AppCompatActivity {

    WeatherStation station;

    TextView stationName = null;
    TextView stationLocation = null;
    TextView stationLatLon = null;
    TextView stationSponsorUrl = null;

    ImageButton summaryButton = null;
    ImageButton windSpeedPlotsButton = null;
    ImageButton windDirectionPlotsButton = null;
    ImageButton temperatureButton = null;
    ImageButton windRoseButton = null;

    ImageView topBackground = null;

    /**
     * Click event on Station Summary Button
     */
    StationDetailsActSummaryButtonClickEvent summaryClickEvent = null;

    /**
     * Click event on Wind Speed Button
     */
    StationDetailsActWindSpeedPlotsButtonClickEvent windSpeedPlotsClickEvent = null;

    StationDetailsActWindDirectionPlotsButtonClickEvent windDirectionPlotsClickEvent = null;

    StationDetailsActTemperaturePlotButtonClickEvent temperaturePlotButtonClickEvent = null;

    /**
     *
     */
    StationDetailsActWindRoseButtonClickEvent windRoseClickEvent = null;

    /**
     * This class downloads the background JPG image from the internet and
     */
    private class DownloadImage implements Runnable {

        ImageView iv;
        String image_url;

        Bitmap bitmap;

        public DownloadImage(ImageView background, String url) {
            iv = background;
            image_url = url;
        }

        @Override
        public void run() {
            try {
                InputStream in = new java.net.URL(image_url).openStream();
                bitmap = BitmapFactory.decodeStream(in);

                iv.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public StationDetailsActivity() {
        stationName = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_station_details, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        float station_lat = 0.0f;   // szerokość    W - E
        float station_lon = 0.0f;   // długość      S - N

        StringBuilder sb = new StringBuilder();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_station_details);

        station = (WeatherStation) getIntent().getSerializableExtra("station");

        stationName = findViewById(R.id.textViewStationName);
        stationLocation = findViewById(R.id.textViewLocalization);
        stationLatLon = findViewById(R.id.textViewLatLon);
        stationSponsorUrl = findViewById(R.id.textViewSponsorUrl);

//        public static final int BLACK = -16777216;
//        public static final int BLUE = -16776961;
//        public static final int CYAN = -16711681;
//        public static final int DKGRAY = -12303292;
//        public static final int GRAY = -7829368;
//        public static final int GREEN = -16711936;
//        public static final int LTGRAY = -3355444;
//        public static final int MAGENTA = -65281;
//        public static final int RED = -65536;
//        public static final int TRANSPARENT = 0;
//        public static final int WHITE = -1;
//        public static final int YELLOW = -256;

        stationName.setTextColor(station.getStationNameTextColor());

        if (station != null && stationName != null) {

            summaryClickEvent = new StationDetailsActSummaryButtonClickEvent(station, this);
            windSpeedPlotsClickEvent = new StationDetailsActWindSpeedPlotsButtonClickEvent(station, this);
            windDirectionPlotsClickEvent = new StationDetailsActWindDirectionPlotsButtonClickEvent(station, this);
            temperaturePlotButtonClickEvent = new StationDetailsActTemperaturePlotButtonClickEvent(station, this);
            windRoseClickEvent = new StationDetailsActWindRoseButtonClickEvent(station, this);

            summaryButton = findViewById(R.id.imageButtonFavourites);
            summaryButton.setOnClickListener(summaryClickEvent);

            windSpeedPlotsButton = findViewById(R.id.imageButtonPlotsWindSpeed);
            windSpeedPlotsButton.setOnClickListener(windSpeedPlotsClickEvent);

            windDirectionPlotsButton = findViewById(R.id.imageButtonPlotsWindDirection);
            windDirectionPlotsButton.setOnClickListener(windDirectionPlotsClickEvent);

            windRoseButton = findViewById(R.id.imageButtonWindRose);
            windRoseButton.setOnClickListener(windRoseClickEvent);

            temperatureButton = findViewById(R.id.imageButtonPlotsTemperature);
            temperatureButton.setOnClickListener(temperaturePlotButtonClickEvent);

            topBackground = findViewById(R.id.imageViewStationPng);
            switch (station.getImageAlign()) {
                case 0:
                    topBackground.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    break;
                case 1:
                    topBackground.setScaleType(ImageView.ScaleType.FIT_START);
                    break;
                case 2:
                    topBackground.setScaleType(ImageView.ScaleType.FIT_END);
                    break;
                case 3:
                    topBackground.setScaleType(ImageView.ScaleType.CENTER);
                    break;
                case 4:
                    topBackground.setScaleType(ImageView.ScaleType.MATRIX);
                    break;
                case 5:
                    topBackground.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                case 6:
                    topBackground.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    break;
                default:
                    topBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }


            stationName.setText(station.getDisplayedName());
            stationLocation.setText(station.getDisplayedLocation());

            station_lat = station.getLat();
            station_lon = station.getLon();

            stationSponsorUrl.setText(station.getSponsorUrl());

            DownloadImage downloadImage = new DownloadImage(topBackground, station.getImageUrl());
            Thread t = new Thread(downloadImage);
            t.start();
            //runOnUiThread(downloadImage);

            if (station_lat > 0.0f && station_lon > 0.0f) {
                // europe
                sb.append(station_lon);
                sb.append(" N / ");
                sb.append(station_lat);
                sb.append(" E");

                stationLatLon.setText(sb.toString());
            } else if (station_lat < 0.0f && station_lon > 0.0f) {
                // usa
                sb.append(station_lon);
                sb.append(" N / ");
                sb.append(-station_lat);
                sb.append(" W");

                stationLatLon.setText(sb.toString());
            } else if (station_lat < 0.0f && station_lon < 0.0f) {
                // brazil
                sb.append(-station_lon);
                sb.append(" S / ");
                sb.append(-station_lat);
                sb.append(" W");

                stationLatLon.setText(sb.toString());
            } else if (station_lat > 0.0f && station_lat > 0.0f) {
                // australia
                sb.append(-station_lon);
                sb.append(" S / ");
                sb.append(station_lat);
                sb.append(" E");

                stationLatLon.setText(sb.toString());
            }


        }
    }
}