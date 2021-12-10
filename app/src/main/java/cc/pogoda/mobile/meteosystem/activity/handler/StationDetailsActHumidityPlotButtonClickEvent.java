package cc.pogoda.mobile.meteosystem.activity.handler;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import cc.pogoda.mobile.meteosystem.activity.StationDetailsPlotsHumidity;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;

public class StationDetailsActHumidityPlotButtonClickEvent implements View.OnClickListener {

    WeatherStation station;

    AppCompatActivity p;

    Intent intent;

    @Override
    public void onClick(View view) {
        intent = new Intent(p, StationDetailsPlotsHumidity.class);
        intent.putExtra("station", station);
        intent.putExtra("data_ln", (int)p.getIntent().getExtras().get("data_ln"));

        p.startActivity(intent);
    }

    public StationDetailsActHumidityPlotButtonClickEvent(WeatherStation wx, AppCompatActivity parent) {
        station = wx;
        p = parent;
    }
}
