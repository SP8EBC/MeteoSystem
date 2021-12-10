package cc.pogoda.mobile.meteosystem.activity.trend.pressure;

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

import cc.pogoda.mobile.meteosystem.R;
import cc.pogoda.mobile.meteosystem.activity.TrendActivity;

public class PressureTrendFragment extends Fragment {

    private PressureTrendViewModel pressureTrendViewModel;

    TextView stationName = null;
    TextView lastDataTimestamp = null;
    TextView currentValue = null;
    TextView twoHours = null;
    TextView fourHours = null;
    TextView sixHours = null;
    TextView eightHours = null;

    String station = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        pressureTrendViewModel =
                new ViewModelProvider(this).get(PressureTrendViewModel.class);

        pressureTrendViewModel.setStation(TrendActivity.getStation());

        // inflate the main layout of the fragment
        View root = inflater.inflate(R.layout.fragment_pressure, container, false);

        station = TrendActivity.getStation();

        // load all elements from the layout
        stationName = root.findViewById(R.id.textViewPressureTrendStationName);
        lastDataTimestamp = root.findViewById(R.id.textViewPressureTrendLastTimestampValue);
        currentValue = root.findViewById(R.id.textViewPressureTrendCurrentValue);
        twoHours = root.findViewById(R.id.textViewPressureTrendTwoHoursValue);
        fourHours = root.findViewById(R.id.textViewPressureTrendFourHoursValue);
        sixHours = root.findViewById(R.id.textViewPressureTrendSixHoursValue);
        eightHours =  root.findViewById(R.id.textViewPressureTrendEightHoursVal);

        pressureTrendViewModel.getStationName().observe(getViewLifecycleOwner(), s -> {
            if (s.length() < 18) {
                stationName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 38);
            }
            else {
                stationName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
            }

            stationName.setText(s);

        });

        pressureTrendViewModel.getLastMeasuremenetTime().observe(getViewLifecycleOwner(), s -> {
            lastDataTimestamp.setText(s);
        });

        pressureTrendViewModel.getCurrentValue().observe(getViewLifecycleOwner(), s -> {
            currentValue.setText(s);
        });

        pressureTrendViewModel.getTwoHoursValue().observe(getViewLifecycleOwner(), s -> {
            twoHours.setText(s);
        });

        pressureTrendViewModel.getFourHoursValue().observe(getViewLifecycleOwner(), s -> {
            fourHours.setText(s);
        });

        pressureTrendViewModel.getSixHoursValue().observe(getViewLifecycleOwner(), s -> {
            sixHours.setText(s);
        });

        pressureTrendViewModel.getEightHoursValue().observe(getViewLifecycleOwner(), s -> {
            eightHours.setText(s);
        });

        try {
            if (!pressureTrendViewModel.updateData()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(container.getContext());
                builder.setMessage(R.string.no_comm_with_backend);
                builder.setPositiveButton(R.string.ok, (DialogInterface var1, int var2) -> {
                    var1.dismiss();
                });
                builder.create();
                builder.show();
            }
        }
        catch (Exception e) {
            ;
        }

        return root;
    }
}