package cc.pogoda.mobile.meteosystem.activity.handler;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import cc.pogoda.mobile.meteosystem.activity.StationDetailsHistogramActivity;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;

public class StationDetailsActHistogramButtonClickEvent implements View.OnClickListener {

    WeatherStation station;

    AppCompatActivity p;

    Intent intent;

    public StationDetailsActHistogramButtonClickEvent(WeatherStation wx, AppCompatActivity parent) {
        station = wx;

        p = parent;
    }

    @Override
    public void onClick(View view) {
        intent = new Intent(p, StationDetailsHistogramActivity.class);
        intent.putExtra("station", station);
        intent.putExtra("data_ln", (int)p.getIntent().getExtras().get("data_ln"));

        p.startActivity(intent);
    }
}
