package cc.pogoda.mobile.pogodacc.activity.handler;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import cc.pogoda.mobile.pogodacc.activity.StationDetailsActivity;
import cc.pogoda.mobile.pogodacc.type.ParceableFavsCallReason;
import cc.pogoda.mobile.pogodacc.type.WeatherStation;

public class AllStationsActRecyclerViewButtonClickEvent implements View.OnClickListener {

    WeatherStation station;

    AppCompatActivity p;

    Intent intent;

    ParceableFavsCallReason.Reason reason;

    public AllStationsActRecyclerViewButtonClickEvent(WeatherStation wx, AppCompatActivity parent, ParceableFavsCallReason.Reason r) {
        station = wx;
        p = parent;
        reason = r;
    }

    @Override
    public void onClick(View v) {
        if (reason == null || reason == ParceableFavsCallReason.Reason.FAVOURITES || reason == ParceableFavsCallReason.Reason.ALL_STATIONS) {
            intent = new Intent(p, StationDetailsActivity.class);
            intent.putExtra("station", station);

            p.startActivity(intent);

            return;
        }
        else if (reason == ParceableFavsCallReason.Reason.EXPORT_SELECT) {

            EventBus.getDefault().post(station);

            p.setResult(Activity.RESULT_OK);

            p.finish();

            return;
        }
    }
}
