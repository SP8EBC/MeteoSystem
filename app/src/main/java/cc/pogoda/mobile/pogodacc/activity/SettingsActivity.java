package cc.pogoda.mobile.pogodacc.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.config.AppConfiguration;

public class SettingsActivity extends AppCompatActivity {

    Switch windspdUnitSwitch;

    TextView windspdUnitDisplayTv;

    private void updateWindspdUnitTv(boolean b) {
        if (windspdUnitDisplayTv != null) {
            if (b) {
                windspdUnitDisplayTv.setText(R.string.knots);
            }
            else {
                windspdUnitDisplayTv.setText(R.string.meters_per_second);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        windspdUnitDisplayTv = (TextView) findViewById(R.id.textViewSettingsWindspeedUnitDisp);
        updateWindspdUnitTv(AppConfiguration.replaceMsWithKnots);

        windspdUnitSwitch = (Switch) findViewById(R.id.switchKnots);
        if (windspdUnitSwitch != null) {
            windspdUnitSwitch.setChecked(AppConfiguration.replaceMsWithKnots);
            windspdUnitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    AppConfiguration.replaceMsWithKnots = b;

                    updateWindspdUnitTv(b);
                }
            });
        }
    }
}