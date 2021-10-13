package cc.pogoda.mobile.pogodacc.activity.handler;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import cc.pogoda.mobile.pogodacc.activity.AllStationsActivity;
import cc.pogoda.mobile.pogodacc.activity.ExportDataActivity;

public class MainActImageButtonExportClickEvent implements View.OnClickListener{

    AppCompatActivity parent;

    Intent intent;

    public MainActImageButtonExportClickEvent(AppCompatActivity p) {
        parent = p;

        intent = new Intent(this.parent, ExportDataActivity.class);
    }

    @Override
    public void onClick(View view) {

        parent.startActivity(intent);
    }
}
