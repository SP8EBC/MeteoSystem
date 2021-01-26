package cc.pogoda.mobile.pogodacc.activity.trend.temperature;

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

public class TemperatureTrendFragment extends Fragment {

    private TemperatureTrendViewModel temperatureTrendViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        temperatureTrendViewModel =
                new ViewModelProvider(this).get(TemperatureTrendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_temperature, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        temperatureTrendViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}