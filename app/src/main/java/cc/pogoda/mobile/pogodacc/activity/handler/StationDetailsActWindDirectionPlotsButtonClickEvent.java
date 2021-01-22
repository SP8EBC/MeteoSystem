package cc.pogoda.mobile.pogodacc.activity.handler;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import cc.pogoda.mobile.pogodacc.activity.StationDetailsPlotsDirection;
import cc.pogoda.mobile.pogodacc.activity.StationDetailsPlotsTemperature;
import cc.pogoda.mobile.pogodacc.type.WeatherStation;

public class StationDetailsActWindDirectionPlotsButtonClickEvent implements View.OnClickListener {

    WeatherStation station;

    AppCompatActivity p;

    Intent intent;


    @Override
    public void onClick(View view) {
        intent = new Intent(p, StationDetailsPlotsDirection.class);
        intent.putExtra("station", station);
        intent.putExtra("data_ln", (int)p.getIntent().getExtras().get("data_ln"));

        p.startActivity(intent);
    }

    public StationDetailsActWindDirectionPlotsButtonClickEvent(WeatherStation wx, AppCompatActivity parent) {
        station = wx;
        p = parent;
    }
}
