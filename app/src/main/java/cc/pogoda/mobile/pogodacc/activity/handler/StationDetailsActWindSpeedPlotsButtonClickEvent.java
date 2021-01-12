package cc.pogoda.mobile.pogodacc.activity.handler;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import cc.pogoda.mobile.pogodacc.activity.StationDetailsPlotsWind;
import cc.pogoda.mobile.pogodacc.type.WeatherStation;

public class StationDetailsActWindSpeedPlotsButtonClickEvent implements View.OnClickListener {

    WeatherStation station;

    AppCompatActivity p;

    Intent intent;

    public StationDetailsActWindSpeedPlotsButtonClickEvent(WeatherStation wx, AppCompatActivity parent) {
        station = wx;

        p = parent;
    }

    @Override
    public void onClick(View view) {
        intent = new Intent(p, StationDetailsPlotsWind.class);
        intent.putExtra("station", station);

        p.startActivity(intent);
    }
}
