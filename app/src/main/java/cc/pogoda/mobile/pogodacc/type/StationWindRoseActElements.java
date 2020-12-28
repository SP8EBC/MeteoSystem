package cc.pogoda.mobile.pogodacc.type;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.type.web.Summary;

public class StationWindRoseActElements implements StationActivityElements {

    public ImageView windArrow;

    public TextView windSpeed;

    public TextView windGusts;

    public TextView windDirection;

    public TextView temperature;

    public TextView pressure;

    Activity activity;

    public StationWindRoseActElements() {
        windArrow = null;
        windGusts = null;
        windSpeed = null;
        temperature = null;
        pressure = null;
        activity = null;
    }

    @Override
    public void updateFromSummary(Summary s) {

        if (activity == null ) {
            return;
        }

        if (windArrow != null) {
            windArrow.setRotation(s.direction - 225.0f);
        }

        if (windSpeed != null) {
            windSpeed.setText(activity.getResources().getString(R.string.mean_value) +  '\n' + s.average_speed + "m/s");
        }

        if (windGusts != null) {
            windGusts.setText(activity.getResources().getString(R.string.wind_gust_short) +  '\n' + s.gusts + "m/s");
        }

        if (windDirection != null) {
            windDirection.setText(activity.getResources().getString(R.string.wind_direction_short) +  '\n' + s.direction + activity.getResources().getString(R.string.degrees_sign));
        }

        if (temperature != null) {
            temperature.setText(activity.getResources().getString(R.string.temperature_short) +  '\n' + String.format("%.1f", s.avg_temperature) + "°C");

        }
    }

    @Override
    public void setActivity(Activity act) {
        activity = act;
    }
}
