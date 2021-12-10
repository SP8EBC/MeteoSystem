package cc.pogoda.mobile.meteosystem.activity.handler;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import cc.pogoda.mobile.meteosystem.activity.StationDetailsSummaryActivity;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;

public class StationDetailsActSummaryButtonClickEvent implements View.OnClickListener {

    WeatherStation station;

    AppCompatActivity p;

    Intent intent;

    public StationDetailsActSummaryButtonClickEvent(WeatherStation wx, AppCompatActivity parent) {
        station = wx;

        p = parent;
    }

    @Override
    public void onClick(View view) {
        intent = new Intent(p, StationDetailsSummaryActivity.class);
        intent.putExtra("station", station);

        p.startActivity(intent);

    }
}
