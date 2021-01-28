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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        pressureTrendViewModel =
                new ViewModelProvider(this).get(PressureTrendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_pressure, container, false);
        //final TextView textView = root.findViewById(R.id.text_notifications);
        pressureTrendViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }
}