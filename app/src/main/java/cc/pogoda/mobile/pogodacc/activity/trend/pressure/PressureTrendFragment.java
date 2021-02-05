package cc.pogoda.mobile.pogodacc.activity.trend.pressure;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import cc.pogoda.mobile.pogodacc.R;

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
        

        // inflate the main layout of the fragment
        View root = inflater.inflate(R.layout.fragment_pressure, container, false);

        Bundle arg = this.getArguments();

        if (arg != null) {
            station = arg.getString("station");
        }

        // load all elements from the layout
        stationName = root.findViewById(R.id.textViewPressureTrendStationName);
        lastDataTimestamp = root.findViewById(R.id.textViewPressureTrendLastTimestampValue);
        currentValue = root.findViewById(R.id.textViewPressureTrendCurrentValue);
        twoHours = root.findViewById(R.id.textViewPressureTrendTwoHoursValue);
        fourHours = root.findViewById(R.id.textViewPressureTrendTwoHoursValue);
        sixHours = root.findViewById(R.id.textViewPressureTrendSixHoursValue);
        eightHours =  root.findViewById(R.id.textViewPressureTrendEightHoursVal);

        pressureTrendViewModel.getStationName().observe(getViewLifecycleOwner(), s -> {
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

        //pressureTrendViewModel.updateData();

        return root;
    }
}