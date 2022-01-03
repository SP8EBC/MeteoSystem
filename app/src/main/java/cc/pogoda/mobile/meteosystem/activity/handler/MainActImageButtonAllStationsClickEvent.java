package cc.pogoda.mobile.meteosystem.activity.handler;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import cc.pogoda.mobile.meteosystem.activity.AllStationsActivity;
import cc.pogoda.mobile.meteosystem.type.ParceableStationsList;

public class MainActImageButtonAllStationsClickEvent implements View.OnClickListener {

    AppCompatActivity parent;

    Intent intent;

    public MainActImageButtonAllStationsClickEvent(AppCompatActivity parent) {
        this.parent = parent;

        intent = new Intent(this.parent, AllStationsActivity.class);


    }

    @Override
    public void onClick(View v) {
        launchActivity();

        return;
    }

    private void launchActivity() {
        parent.startActivity(intent);
    }
}
