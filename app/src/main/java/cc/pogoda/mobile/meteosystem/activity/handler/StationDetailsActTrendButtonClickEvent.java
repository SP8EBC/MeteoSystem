package cc.pogoda.mobile.meteosystem.activity.handler;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import cc.pogoda.mobile.meteosystem.activity.TrendActivity;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;

public class StationDetailsActTrendButtonClickEvent implements View.OnClickListener {

    private AppCompatActivity parent;

    private Intent intent;

    private WeatherStation station;

    public StationDetailsActTrendButtonClickEvent(WeatherStation station, AppCompatActivity parent) {
        this.parent = parent;
        this.station = station;
    }

    @Override
    public void onClick(View view) {
        intent = new Intent(parent, TrendActivity.class);

        intent.putExtra("station", station.getSystemName());

        parent.startActivity(intent);
    }
}
