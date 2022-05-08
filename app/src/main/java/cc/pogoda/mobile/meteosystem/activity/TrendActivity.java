package cc.pogoda.mobile.meteosystem.activity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.tinylog.Logger;

import java.util.Locale;

import cc.pogoda.mobile.meteosystem.R;
import cc.pogoda.mobile.meteosystem.activity.trend.pressure.PressureTrendFragmentDirections;
import cc.pogoda.mobile.meteosystem.activity.trend.temperature.TemperatureTrendFragmentDirections;
import cc.pogoda.mobile.meteosystem.activity.trend.wind.WindTrendFragmentDirections;
import cc.pogoda.mobile.meteosystem.config.AppConfiguration;

public class TrendActivity extends AppCompatActivity {

    public static String getStation() {
        return station;
    }

    private static String station = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String stationName = (String)getIntent().getExtras().get("station");

        if (AppConfiguration.locale != null && !AppConfiguration.locale.equals("default") ) {
            Logger.debug("[AppConfiguration.locale = " + AppConfiguration.locale +  "]");
            Locale locale = new Locale(AppConfiguration.locale);
            Locale.setDefault(locale);
            Resources resources = this.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }

        this.station = stationName;
        Bundle bundle = new Bundle();
        bundle.putString("station", stationName);

        setContentView(R.layout.activity_trend);

        NavArgument.Builder builder = new NavArgument.Builder();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_pressure, R.id.navigation_temperature, R.id.navigation_wind)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavDirections temperature = TemperatureTrendFragmentDirections.actionNavigationTemperatureToNavigationPressure(stationName);
        NavDirections wind = WindTrendFragmentDirections.actionNavigationWindToNavigationTemperature(stationName);
        NavDirections pressure = PressureTrendFragmentDirections.actionNavigationPressureToNavigationWind(stationName);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

}