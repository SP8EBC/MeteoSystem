package cc.pogoda.mobile.pogodacc.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.io.InputStream;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.activity.handler.StationDetailsActTemperaturePlotButtonClickEvent;
import cc.pogoda.mobile.pogodacc.activity.handler.StationDetailsActTrendButtonClickEvent;
import cc.pogoda.mobile.pogodacc.activity.handler.StationDetailsActWindDirectionPlotsButtonClickEvent;
import cc.pogoda.mobile.pogodacc.activity.handler.StationDetailsActWindSpeedPlotsButtonClickEvent;
import cc.pogoda.mobile.pogodacc.activity.handler.StationDetailsActSummaryButtonClickEvent;
import cc.pogoda.mobile.pogodacc.activity.handler.StationDetailsActWindRoseButtonClickEvent;
import cc.pogoda.mobile.pogodacc.config.AppConfiguration;
import cc.pogoda.mobile.pogodacc.type.WeatherStation;
import cc.pogoda.mobile.pogodacc.type.WeatherStationListEvent;

public class StationDetailsActivity extends AppCompatActivity {

    WeatherStation station;

    TextView stationName = null;
    TextView stationLocation = null;
    TextView stationLatLon = null;
    TextView stationSponsorUrl = null;
    TextView stationMoreInfo = null;

    ImageButton summaryButton = null;
    ImageButton windSpeedPlotsButton = null;
    ImageButton windDirectionPlotsButton = null;
    ImageButton temperatureButton = null;
    ImageButton windRoseButton = null;
    ImageButton trendButton = null;

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

    StationDetailsActTrendButtonClickEvent trendButtonClickEvent = null;

    /**
     *
     */
    StationDetailsActWindRoseButtonClickEvent windRoseClickEvent = null;

    /**
     *
     */
    int plotDataLn = -1;

    /*
     *  the value selected by a user in plot lenght dialog before clicking OK
     */
    int selectedLn = 0;

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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //return super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.menuItemStationDetailsAddFavourites:
                if (station != null) {
                    boolean result = false;

                    //result = AppConfiguration.favourites.addFav(station);
                    EventBus.getDefault().post(new WeatherStationListEvent(station, WeatherStationListEvent.EventReason.ADD));

                    if (result) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(R.string.fav_added_success);
                        builder.setPositiveButton(R.string.ok, (DialogInterface var1, int var2) -> {
                            var1.dismiss();
                        });
                        builder.create();
                        builder.show();
                    }
                }
                break;
            case R.id.menuItemStationDetailsDeleteFavourites:
                if (station != null) {
                    boolean result = false;

                    //result = AppConfiguration.favourites.removeFav(station);
                    EventBus.getDefault().post(new WeatherStationListEvent(station, WeatherStationListEvent.EventReason.DELETE));

                    if (result) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(R.string.fav_deleted_success);
                        builder.setPositiveButton(R.string.ok, (DialogInterface var1, int var2) -> {
                            var1.dismiss();
                        });
                        builder.create();
                        builder.show();
                    }
                }
                break;
            case R.id.menuItemStationDetailsPlotsLn:
                setPlotsLn();
                break;
        }

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
        stationMoreInfo = findViewById(R.id.textViewMoreInfo);

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

            // set the default value od data lenght
            this.getIntent().putExtra("data_ln", plotDataLn);

            summaryClickEvent = new StationDetailsActSummaryButtonClickEvent(station, this);
            windSpeedPlotsClickEvent = new StationDetailsActWindSpeedPlotsButtonClickEvent(station, this);
            windDirectionPlotsClickEvent = new StationDetailsActWindDirectionPlotsButtonClickEvent(station, this);
            temperaturePlotButtonClickEvent = new StationDetailsActTemperaturePlotButtonClickEvent(station, this);
            windRoseClickEvent = new StationDetailsActWindRoseButtonClickEvent(station, this);
            trendButtonClickEvent = new StationDetailsActTrendButtonClickEvent(station, this);

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

            trendButton = findViewById(R.id.imageButtonTrend);
            trendButton.setOnClickListener(trendButtonClickEvent);

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
            stationMoreInfo.setText(station.getMoreInfo());


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

    private void setPlotsLn() {

        // an array of strings with radio button options
        CharSequence[] vales = new CharSequence[3];

        // fill the array from resources
        vales[0] = getResources().getString(R.string.hours_12).toString();
        vales[1] = getResources().getString(R.string.hours_24).toString();
        vales[2] = getResources().getString(R.string.days_3).toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.plot_data_lenght);
        builder.setSingleChoiceItems(vales, plotDataLn, (DialogInterface var1, int var2) -> { selectedLn = var2; } );
        builder.setPositiveButton(R.string.ok, (DialogInterface var1, int var2) -> {
            plotDataLn = selectedLn;
            this.getIntent().putExtra("data_ln", plotDataLn);
            var1.dismiss();
        });
        builder.create();
        builder.show();

    }
}