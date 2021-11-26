package cc.pogoda.mobile.pogodacc.activity.trend.temperature;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.w3c.dom.Text;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.activity.TrendActivity;

public class TemperatureTrendFragment extends Fragment {

    private TemperatureTrendViewModel temperatureTrendViewModel;

    private TextView textViewTemperatureTrendLastTimestampValue;
    private TextView textViewTemperatureTrendStationName;

    private TextView textViewTemperatureTrendCurrentTValue;
    private TextView textViewTemperatureTrendCurrentHValue;

    private TextView textViewTemperatureTrendTwoHoursTValue;
    private TextView textViewTemperatureTrendTwoHoursHValue;

    private TextView textViewTemperatureTrendFourHoursTValue;
    private TextView textViewTemperatureTrendFourHoursHValue;

    private TextView textViewTemperatureTrendSixHoursTValue;
    private TextView textViewTemperatureTrendSixHoursHValue;

    private TextView textViewTemperatureTrendTrendEightHoursTVal;
    private TextView textViewTemperatureTrendEightHoursHVal;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        String station = TrendActivity.getStation();

        temperatureTrendViewModel =
                new ViewModelProvider(this).get(TemperatureTrendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_temperature, container, false);
//
        temperatureTrendViewModel.setStation(station);

        textViewTemperatureTrendLastTimestampValue = root.findViewById(R.id.textViewTemperatureTrendLastTimestampValue);
        textViewTemperatureTrendStationName = root.findViewById(R.id.textViewTemperatureTrendStationName);

        textViewTemperatureTrendCurrentTValue = root.findViewById(R.id.textViewTemperatureTrendCurrentTValue);
        textViewTemperatureTrendCurrentHValue = root.findViewById(R.id.textViewTemperatureTrendCurrentHValue);

        textViewTemperatureTrendTwoHoursTValue = root.findViewById(R.id.textViewTemperatureTrendTwoHoursTValue);
        textViewTemperatureTrendTwoHoursHValue = root.findViewById(R.id.textViewTemperatureTrendTwoHoursHValue);

        textViewTemperatureTrendFourHoursTValue = root.findViewById(R.id.textViewTemperatureTrendFourHoursTValue);
        textViewTemperatureTrendFourHoursHValue = root.findViewById(R.id.textViewTemperatureTrendFourHoursHValue);

        textViewTemperatureTrendSixHoursTValue = root.findViewById(R.id.textViewTemperatureTrendSixHoursTValue);
        textViewTemperatureTrendSixHoursHValue = root.findViewById(R.id.textViewTemperatureTrendSixHoursHValue);

        textViewTemperatureTrendTrendEightHoursTVal = root.findViewById(R.id.textViewTemperatureTrendTrendEightHoursTVal);
        textViewTemperatureTrendEightHoursHVal = root.findViewById(R.id.textViewTemperatureTrendEightHoursHVal);

        temperatureTrendViewModel.getDisplayedStationName().observe(getViewLifecycleOwner(), s -> {

            if (s.length() < 18) {
                textViewTemperatureTrendStationName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 38);
            }
            else {
                textViewTemperatureTrendStationName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
            }

            textViewTemperatureTrendStationName.setText(s);
        });

        temperatureTrendViewModel.getLastMeasuremenetTime().observe(getViewLifecycleOwner(), s -> {
            textViewTemperatureTrendLastTimestampValue.setText(s);
        });

        // current values
        temperatureTrendViewModel.getCurrentTemperatureValue().observe(getViewLifecycleOwner(), s -> {
            textViewTemperatureTrendCurrentTValue.setText(s);
        });

        temperatureTrendViewModel.getCurrentHumidityValue().observe(getViewLifecycleOwner(), s -> {
            textViewTemperatureTrendCurrentHValue.setText(s);
        });

        // two hours ago
        temperatureTrendViewModel.getTwoHoursTemperatureValue().observe(getViewLifecycleOwner(), s -> {
            textViewTemperatureTrendTwoHoursTValue.setText(s);
        });

        temperatureTrendViewModel.getTwoHoursHumidityValue().observe(getViewLifecycleOwner(), s -> {
            textViewTemperatureTrendTwoHoursHValue.setText(s);
        });

        // four hours ago
        temperatureTrendViewModel.getFourHoursTemperatureValue().observe(getViewLifecycleOwner(), s -> {
            textViewTemperatureTrendFourHoursTValue.setText(s);
        });

        temperatureTrendViewModel.getFourHoursHumidityValue().observe(getViewLifecycleOwner(), s -> {
            textViewTemperatureTrendFourHoursHValue.setText(s);
        });

        // six hours ago
        temperatureTrendViewModel.getSixHoursTemperatureValue().observe(getViewLifecycleOwner(), s -> {
            textViewTemperatureTrendSixHoursTValue.setText(s);
        });

        temperatureTrendViewModel.getSixHoursHumidityValue().observe(getViewLifecycleOwner(), s -> {
            textViewTemperatureTrendSixHoursHValue.setText(s);
        });

        //eight hours ago
        temperatureTrendViewModel.getEightHoursTemperatureValue().observe(getViewLifecycleOwner(), s -> {
            textViewTemperatureTrendTrendEightHoursTVal.setText(s);
        });

        temperatureTrendViewModel.getEightHoursHumidityValue().observe(getViewLifecycleOwner(), s -> {
            textViewTemperatureTrendEightHoursHVal.setText(s);
        });

        if(!temperatureTrendViewModel.getData()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(container.getContext());
            builder.setMessage(R.string.no_comm_with_backend);
            builder.setPositiveButton(R.string.ok, (DialogInterface var1, int var2) -> {
                var1.dismiss();
            });
            builder.create();
            builder.show();
        }

        return root;
    }
}