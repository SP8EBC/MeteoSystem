package cc.pogoda.mobile.pogodacc.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
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

    EditText enditTextMinutesPeriod;

    private static String languageNameFromShort(String shortName) {
        switch (shortName) {
            case "en-rUS": return "English";
            case "pl": return "Polski";
            case "cs": return "Čeština";
            case "uk": return "Українська мова";
            case "ru": return "Русский";
            case "lv": return "Latviešu";
            case "de": return "Deutsch";
            default: return "AUTO";
        }
    }

    private void updateWindspdUnitTv(boolean b) {
        if (windspdUnitDisplayTv != null) {
            if (b) {
                windspdUnitDisplayTv.setText(R.string.knots_long);
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

        enditTextMinutesPeriod = (EditText) findViewById(R.id.editTextNumberSettingsMinTimeRes);
        enditTextMinutesPeriod.setText(Integer.toString(AppConfiguration.decimationPeriod));
        if (enditTextMinutesPeriod != null) {
            enditTextMinutesPeriod.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (i2 > 0) {
                        try {
                            int newValue = Integer.valueOf(String.valueOf(charSequence), 10);

                            if (newValue > 60) {
                                newValue = 60;

                                enditTextMinutesPeriod.setText(Integer.toString(newValue));
                            }

                            AppConfiguration.decimationPeriod = newValue;

                            confFile.storeToFile();
                        }
                        catch (NumberFormatException e) {
                            AppConfiguration.decimationPeriod = 0;
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

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
                        case "Deutsch": AppConfiguration.locale = "de"; break;
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