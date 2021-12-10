package cc.pogoda.mobile.meteosystem.activity.handler;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import cc.pogoda.mobile.meteosystem.activity.SettingsActivity;
import cc.pogoda.mobile.meteosystem.file.ConfigurationFile;

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
