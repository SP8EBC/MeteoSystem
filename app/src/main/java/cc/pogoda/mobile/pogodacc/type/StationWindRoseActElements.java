package cc.pogoda.mobile.pogodacc.type;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.type.web.Summary;

public class StationWindRoseActElements implements StationActivityElements {

    public ImageView windArrow;

    public TextView windSpeed;

    public TextView windGusts;

    public TextView windDirection;

    public TextView temperature;

    public TextView pressure;

    public TextView maxGust;

    /**
     * This field is named 'minAverage' but in UX it is described as minimal wind speed which is
     * not exactly precise.
     */
    public TextView minAverage;

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

        if (pressure != null) {
            pressure.setText(activity.getResources().getString(R.string.qnh) + ": " + String.format("%d hPa", s.qnh));
        }

        if (maxGust != null) {
            maxGust.setText(activity.getResources().getString(R.string.max_1h_gust) + ": " + s.hour_gusts + "m/s");
        }

        if (minAverage != null) {
            minAverage.setText(activity.getResources().getString(R.string.min_1h_avg) + ": " + s.hour_min_average_speed + "m/s");

        }
    }

    @Override
    public void setActivity(Activity act) {
        activity = act;
    }
}
