package cc.pogoda.mobile.meteosystem.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.tinylog.Logger;

import java.util.Locale;

import cc.pogoda.mobile.meteosystem.R;
import cc.pogoda.mobile.meteosystem.activity.handler.StationDetailsActHumidityPlotButtonClickEvent;
import cc.pogoda.mobile.meteosystem.activity.handler.StationDetailsActTemperaturePlotButtonClickEvent;
import cc.pogoda.mobile.meteosystem.activity.handler.StationDetailsActTrendButtonClickEvent;
import cc.pogoda.mobile.meteosystem.activity.handler.StationDetailsActWindDirectionPlotsButtonClickEvent;
import cc.pogoda.mobile.meteosystem.activity.handler.StationDetailsActWindSpeedPlotsButtonClickEvent;
import cc.pogoda.mobile.meteosystem.activity.handler.StationDetailsActSummaryButtonClickEvent;
import cc.pogoda.mobile.meteosystem.activity.handler.StationDetailsActWindRoseButtonClickEvent;
import cc.pogoda.mobile.meteosystem.activity.updater.StationBackgroundImageUpdater;
import cc.pogoda.mobile.meteosystem.config.AppConfiguration;
import cc.pogoda.mobile.meteosystem.type.AvailableParameters;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;
import cc.pogoda.mobile.meteosystem.type.WeatherStationListEvent;
import cc.pogoda.mobile.meteosystem.web.StationBackgroundDownloader;

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
    ImageButton humidityButton = null;
    ImageButton windRoseButton = null;
    ImageButton trendButton = null;

    ImageView topBackground = null;

    AppCompatActivity act;

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

    StationDetailsActHumidityPlotButtonClickEvent humidityPlotButtonClickEvent = null;

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

    Handler handler;

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
                    boolean result = true;

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
                    boolean result = true;

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

        act = this;
        station = (WeatherStation) getIntent().getSerializableExtra("station");

        Logger.info("[station.getSystemName() = " + station.getSystemName() +"]");

        if (AppConfiguration.locale != null && !AppConfiguration.locale.equals("default") ) {
            Logger.debug("[AppConfiguration.locale = " + AppConfiguration.locale +  "]");
            Locale locale = new Locale(AppConfiguration.locale);
            Locale.setDefault(locale);
            Resources resources = this.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }

        setContentView(R.layout.activity_station_details);

        AvailableParameters parameters = station.getAvailableParameters();

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
            humidityPlotButtonClickEvent = new StationDetailsActHumidityPlotButtonClickEvent(station, this);
            windRoseClickEvent = new StationDetailsActWindRoseButtonClickEvent(station, this);
            trendButtonClickEvent = new StationDetailsActTrendButtonClickEvent(station, this);

            summaryButton = findViewById(R.id.imageButtonFavourites);
            summaryButton.setOnClickListener(summaryClickEvent);

            windSpeedPlotsButton = findViewById(R.id.imageButtonPlotsWindSpeed);
            if (parameters.windSpeed) {
                windSpeedPlotsButton.setOnClickListener(windSpeedPlotsClickEvent);
            }
            else {
                windSpeedPlotsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(act);
                        builder.setMessage(R.string.station_doesnt_measure);
                        builder.setPositiveButton(R.string.ok, (DialogInterface var1, int var2) -> {
                            var1.dismiss();
                        });
                        builder.create();
                        builder.show();
                    }
                });

            }

            windDirectionPlotsButton = findViewById(R.id.imageButtonPlotsWindDirection);
            if (parameters.windSpeed) {
                windDirectionPlotsButton.setOnClickListener(windDirectionPlotsClickEvent);
            }
            else {
                windDirectionPlotsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(act);
                        builder.setMessage(R.string.station_doesnt_measure);
                        builder.setPositiveButton(R.string.ok, (DialogInterface var1, int var2) -> {
                            var1.dismiss();
                        });
                        builder.create();
                        builder.show();
                    }
                });
            }

            windRoseButton = findViewById(R.id.imageButtonWindRose);
            windRoseButton.setOnClickListener(windRoseClickEvent);

            temperatureButton = findViewById(R.id.imageButtonPlotsTemperature);
            temperatureButton.setOnClickListener(temperaturePlotButtonClickEvent);

            humidityButton = findViewById(R.id.imageButtonPlotsHumidity);
            if (parameters.humidity) {
                humidityButton.setOnClickListener(humidityPlotButtonClickEvent);
            }
            else {
                humidityButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(act);
                        builder.setMessage(R.string.station_doesnt_measure);
                        builder.setPositiveButton(R.string.ok, (DialogInterface var1, int var2) -> {
                            var1.dismiss();
                        });
                        builder.create();
                        builder.show();
                    }
                });
            }

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


            if (station.getDisplayedName().length() > 18) {
                stationName.setText(station.getDisplayedName());
                stationName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30.0f);
            }
            else {
                stationName.setText(station.getDisplayedName());
                stationName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36.0f);
            }

            stationLocation.setText(station.getDisplayedLocation());

            station_lat = station.getLat();
            station_lon = station.getLon();

            stationSponsorUrl.setAutoLinkMask(0);
            stationSponsorUrl.setMovementMethod(LinkMovementMethod.getInstance());
            String anchorText;
            if (station.getSponsorUrl().length() > 32) {
                anchorText = getString(R.string.www_link);
            } else {
                anchorText = station.getSponsorUrl();
            }
            stationSponsorUrl.setMovementMethod(LinkMovementMethod.getInstance());
            stationSponsorUrl.setText(
                    HtmlCompat.fromHtml(
                            "<a href=\"" + station.getSponsorUrl() + "\">" + anchorText + "</a>\n", HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
            );

//            if (station.getSponsorUrl().length() > 32) {
//                stationSponsorUrl.setClickable(true);
//                stationSponsorUrl.setMovementMethod(LinkMovementMethod.getInstance());
//                stationSponsorUrl.setText(Html.fromHtml("<a href=\"" + station.getSponsorUrl() +"\">" + getString(R.string.www_link) + "</a>\n", HtmlCompat.FROM_HTML_MODE_LEGACY));
//            }
//            else {
//                stationSponsorUrl.setText(station.getSponsorUrl());
//            }

            stationMoreInfo.setText(station.getMoreInfo());

            StationBackgroundDownloader downloader = new StationBackgroundDownloader(station);
            Thread t = new Thread(downloader);
            t.start();

            handler = new Handler();
            handler.postDelayed(new StationBackgroundImageUpdater(topBackground, stationName, station, downloader, handler), 100);

            if (station_lat > 0.0f && station_lon > 0.0f) {
                // europe
                sb.append(station_lat);
                sb.append(" N / ");
                sb.append(station_lon);
                sb.append(" E");

                stationLatLon.setText(sb.toString());
            } else if (station_lat < 0.0f && station_lon > 0.0f) {
                // usa
                sb.append(station_lat);
                sb.append(" N / ");
                sb.append(-station_lon);
                sb.append(" W");

                stationLatLon.setText(sb.toString());
            } else if (station_lat < 0.0f && station_lon < 0.0f) {
                // brazil
                sb.append(-station_lat);
                sb.append(" S / ");
                sb.append(-station_lon);
                sb.append(" W");

                stationLatLon.setText(sb.toString());
            } else if (station_lat > 0.0f && station_lat > 0.0f) {
                // australia
                sb.append(-station_lat);
                sb.append(" S / ");
                sb.append(station_lon);
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