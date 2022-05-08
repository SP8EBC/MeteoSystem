package cc.pogoda.mobile.meteosystem.activity.handler;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.tinylog.Logger;

import cc.pogoda.mobile.meteosystem.activity.ExportDataActivity;
import cc.pogoda.mobile.meteosystem.type.ParceableStationsList;

public class MainActImageButtonExportClickEvent implements View.OnClickListener{

    AppCompatActivity parent;

    Intent intent;

    public MainActImageButtonExportClickEvent(AppCompatActivity p) {
        parent = p;

        intent = new Intent(this.parent, ExportDataActivity.class);
    }

    @Override
    public void onClick(View view) {

        Logger.info("[onClick]");

        parent.startActivity(intent);
    }
}
