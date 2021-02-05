package cc.pogoda.mobile.pogodacc.activity.trend.wind;

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

public class WindTrendFragment extends Fragment {

    private WindTrendViewModel windTrendViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        String station = "";

        windTrendViewModel =
                new ViewModelProvider(this).get(WindTrendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_wind, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        windTrendViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        Bundle arg = this.getArguments();

        if (arg != null) {
            station = arg.getString("station");
        }

        return root;
    }
}