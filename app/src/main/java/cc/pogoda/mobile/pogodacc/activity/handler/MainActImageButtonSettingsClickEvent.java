package cc.pogoda.mobile.pogodacc.activity.handler;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import cc.pogoda.mobile.pogodacc.activity.SettingsActivity;
import cc.pogoda.mobile.pogodacc.file.ConfigurationFile;

public class MainActImageButtonSettingsClickEvent implements View.OnClickListener {

    AppCompatActivity parent;

    Intent intent;

    public MainActImageButtonSettingsClickEvent(AppCompatActivity _parent, ConfigurationFile _configuration_file) {
        parent = _parent;

        intent = new Intent(parent, SettingsActivity.class);

    }

    @Override
    public void onClick(View view) {
        parent.startActivity(intent);
    }
}
