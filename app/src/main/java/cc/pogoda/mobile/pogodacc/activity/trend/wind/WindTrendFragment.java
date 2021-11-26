package cc.pogoda.mobile.pogodacc.activity.trend.wind;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.activity.TrendActivity;

public class WindTrendFragment extends Fragment {

    private WindTrendViewModel windTrendViewModel;

    TextView textViewWindTrendStationName = null;
    TextView textViewWindTrendLastTimestampValue = null;
    TextView textViewWindTrendCurrentAverageValue = null;
    TextView textViewWindTrendCurrentGustValue = null;
    TextView textViewWindTrendTwoHoursAverageValue = null;
    TextView textViewWindTrendTwoHoursGustsValue = null;
    //TextView eightHours = null;
    TextView textViewWindTrendFourHoursAverageValue = null;
    TextView textViewWindTrendFourHoursGustValue = null;
    TextView textViewWindTrendSixHoursAverageValue = null;
    TextView textViewWindTrendSixHoursGustValue = null;
    TextView textViewWindTrendEightHoursAverageVal = null;
    TextView textViewWindTrendEightHoursGustsVal = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        String station = TrendActivity.getStation();

        windTrendViewModel =
                new ViewModelProvider(this).get(WindTrendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_wind, container, false);

        windTrendViewModel.setStation(TrendActivity.getStation());

        // load layoyt elements
        textViewWindTrendStationName = root.findViewById(R.id.textViewTemperatureTrendStationName);
        textViewWindTrendLastTimestampValue = root.findViewById(R.id.textViewTemperatureTrendLastTimestampValue);

        textViewWindTrendCurrentAverageValue = root.findViewById(R.id.textViewTemperatureTrendCurrentTValue);
        textViewWindTrendCurrentGustValue = root.findViewById(R.id.textViewTemperatureTrendCurrentHValue);

        textViewWindTrendTwoHoursAverageValue = root.findViewById(R.id.textViewTemperatureTrendTwoHoursTValue);
        textViewWindTrendTwoHoursGustsValue = root.findViewById(R.id.textViewTemperatureTrendTwoHoursHValue);

        textViewWindTrendFourHoursAverageValue = root.findViewById(R.id.textViewTemperatureTrendFourHoursTValue);
        textViewWindTrendFourHoursGustValue = root.findViewById(R.id.textViewTemperatureTrendFourHoursHValue);

        textViewWindTrendSixHoursAverageValue = root.findViewById(R.id.textViewTemperatureTrendSixHoursTValue);
        textViewWindTrendSixHoursGustValue = root.findViewById(R.id.textViewTemperatureTrendSixHoursHValue);

        textViewWindTrendEightHoursAverageVal = root.findViewById(R.id.textViewTemperatureTrendTrendEightHoursTVal);
        textViewWindTrendEightHoursGustsVal = root.findViewById(R.id.textViewTemperatureTrendEightHoursHVal);

        windTrendViewModel.getDisplayedStationName().observe(getViewLifecycleOwner(), s -> {
            if (s.length() < 18) {
                textViewWindTrendStationName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 38);
            }
            else {
                textViewWindTrendStationName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
            }

            textViewWindTrendStationName.setText(s);
        });

        windTrendViewModel.getLastMeasuremenetTime().observe(getViewLifecycleOwner(), s -> {
            textViewWindTrendLastTimestampValue.setText(s);
        });

        // current values
        windTrendViewModel.getCurrentMeanValue().observe(getViewLifecycleOwner(), s -> {
            textViewWindTrendCurrentAverageValue.setText(s);
        });

        windTrendViewModel.getCurrentGustValue().observe(getViewLifecycleOwner(), s -> {
            textViewWindTrendCurrentGustValue.setText(s);
        });

        // two hours ago
        windTrendViewModel.getTwoHoursMeanValue().observe(getViewLifecycleOwner(), s -> {
            textViewWindTrendTwoHoursAverageValue.setText(s);
        });

        windTrendViewModel.getTwoHoursGustValue().observe(getViewLifecycleOwner(), s -> {
            textViewWindTrendTwoHoursGustsValue.setText(s);
        });

        // four hours ago
        windTrendViewModel.getFourHoursMeanValue().observe(getViewLifecycleOwner(), s -> {
            textViewWindTrendFourHoursAverageValue.setText(s);
        });

        windTrendViewModel.getFourHoursGustValue().observe(getViewLifecycleOwner(), s -> {
            textViewWindTrendFourHoursGustValue.setText(s);
        });

        // six hours ago
        windTrendViewModel.getSixHoursMeanValue().observe(getViewLifecycleOwner(), s -> {
            textViewWindTrendSixHoursAverageValue.setText(s);
        });

        windTrendViewModel.getSixHoursGustValue().observe(getViewLifecycleOwner(), s -> {
            textViewWindTrendSixHoursGustValue.setText(s);
        });

        //eight hours ago
        windTrendViewModel.getEightHoursMeanValue().observe(getViewLifecycleOwner(), s -> {
            textViewWindTrendEightHoursAverageVal.setText(s);
        });

        windTrendViewModel.getEightHoursGustValue().observe(getViewLifecycleOwner(), s -> {
            textViewWindTrendEightHoursGustsVal.setText(s);
        });

        if (!windTrendViewModel.updateData()) {
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