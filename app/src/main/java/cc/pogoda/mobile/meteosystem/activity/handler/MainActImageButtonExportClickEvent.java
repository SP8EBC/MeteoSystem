package cc.pogoda.mobile.meteosystem.activity.handler;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import cc.pogoda.mobile.meteosystem.activity.ExportDataActivity;
import cc.pogoda.mobile.meteosystem.type.ParceableStationsList;

public class MainActImageButtonExportClickEvent implements View.OnClickListener{

    AppCompatActivity parent;

    Intent intent;

    public MainActImageButtonExportClickEvent(AppCompatActivity p, ParceableStationsList favs) {
        parent = p;

        intent = new Intent(this.parent, ExportDataActivity.class);
        intent.putExtra("favs", favs);
    }

    @Override
    public void onClick(View view) {

        parent.startActivity(intent);
    }
}
