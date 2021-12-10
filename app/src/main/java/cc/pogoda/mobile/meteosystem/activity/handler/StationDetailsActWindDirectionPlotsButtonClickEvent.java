package cc.pogoda.mobile.meteosystem.activity.handler;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import cc.pogoda.mobile.meteosystem.activity.StationDetailsPlotsDirection;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;

public class StationDetailsActWindDirectionPlotsButtonClickEvent implements View.OnClickListener {

    WeatherStation station;

    AppCompatActivity p;

    Intent intent;


    @Override
    public void onClick(View view) {
        int dataLn = (int)p.getIntent().getExtras().get("data_ln");

        intent = new Intent(p, StationDetailsPlotsDirection.class);
        intent.putExtra("station", station);
        if (dataLn < 0)
            intent.putExtra("data_ln", (int)1); // set 24 hours by default
        else
            intent.putExtra("data_ln", dataLn);

        p.startActivity(intent);
    }

    public StationDetailsActWindDirectionPlotsButtonClickEvent(WeatherStation wx, AppCompatActivity parent) {
        station = wx;
        p = parent;
    }
}
