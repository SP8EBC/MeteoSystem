package cc.pogoda.mobile.pogodacc.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Locale;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.config.AppConfiguration;
import cc.pogoda.mobile.pogodacc.file.ConfigurationFile;

public class SettingsActivity extends AppCompatActivity {

    Switch windspdUnitSwitch;

    TextView windspdUnitDisplayTv;

    ConfigurationFile confFile;

    Spinner language;

    private static String languageNameFromShort(String shortName) {
        switch (shortName) {
            case "en-rUS": return "English";
            case "pl": return "Polski";
            case "cs": return "Čeština";
            case "uk": return "Українська мова";
            case "ru": return "Русский";
            case "lv": return "Latviešu";
            default: return "AUTO";
        }
    }

    private void updateWindspdUnitTv(boolean b) {
        if (windspdUnitDisplayTv != null) {
            if (b) {
                windspdUnitDisplayTv.setText(R.string.knots);
            }
            else {
                windspdUnitDisplayTv.setText(R.string.meters_per_second);
            }

            confFile.storeToFile();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        confFile = new ConfigurationFile(getBaseContext());

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

        language = (Spinner) findViewById(R.id.spinnerSettingsLanguage);
        if (language != null) {
            ArrayAdapter spinnerLanguageAdapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.languages, R.layout.spinner_item);

            int currentLanguagePosition = spinnerLanguageAdapter.getPosition(SettingsActivity.languageNameFromShort(AppConfiguration.locale));

            language.setAdapter(spinnerLanguageAdapter);

            // if an item has been found (if no -1 is returned by 'getPosition'
            if (currentLanguagePosition >= 0) {
                language.setSelection(currentLanguagePosition);
            }

            language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String languageSelected = adapterView.getItemAtPosition(i).toString();

                    switch (languageSelected) {
                        case "English": AppConfiguration.locale = "en-rUS"; break;
                        case "Polski": AppConfiguration.locale = "pl"; break;
                        case "Čeština": AppConfiguration.locale = "cs"; break;
                        case "Українська мова": AppConfiguration.locale = "uk"; break;
                        case "Русский": AppConfiguration.locale = "ru"; break;
                        case "Latviešu": AppConfiguration.locale = "lv"; break;
                        default: AppConfiguration.locale = "default";
                    }

                    confFile.storeToFile();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }
}