package cc.pogoda.mobile.pogodacc.type;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;
import org.w3c.dom.Text;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.type.web.QualityFactor;
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
    public void updateFromSummary(Summary s, AvailableParameters enabledForStation) {

        // data to be displayed
        Summary data;

        // set to true if no_data string shall be displayed instead of parameters
        boolean no_data = false;

        LocalDateTime last_station_data;

        // set to true if the data is old (older than 2 hours)
        boolean old_data = false;

        if (activity == null) {
            return;
        }

        // check if any data has been passed to this method
        if (s == null) {
            data = new Summary();

            // set to 180 to rotate the arrow towards the top of a screen
            data.direction = 180;

            // set the flag to true to show '---' or 'no data' instead of zeros
            no_data = true;
        } else {
            data = s;

            // convert the integer with unix epoch timestamp to LocalDateTime in current system Time Zone
            last_station_data = LocalDateTime.ofEpochSecond(data.last_timestamp, 0, ZonedDateTime.now().getOffset());

            // current date and time (in current time zone set in system configuration)
            LocalDateTime current = LocalDateTime.now();

            // calculate the duration between
            Duration duration = Duration.between(last_station_data, current);

            // if station is not communicating for longer than 2 hours
            if (duration.getSeconds() > 7200) {
                old_data = true;
            }
        }

        // create strings with wind speed, gusts etc
        String average_speed = String.format("%s", data.getWindspeedStr(true));
        String gusts_speed = String.format("%s", data.getWindgustsStr(true));

        // check if wind data is avaliable in the input data set
        if (!no_data && !data.wind_qf_native.equals(QualityFactor.NOT_AVALIABLE)) {
            windArrow.setRotation(data.direction - 225.0f);
        } else {
            // if now wind data is avaliable in the input set move the arrow
            // to point towards the N
            windArrow.setRotation(180.0f - 225.0f);
        }

        if (!no_data && !data.wind_qf_native.equals(QualityFactor.NOT_AVALIABLE)) {
            windSpeed.setText(activity.getResources().getString(R.string.mean_value) + '\n' + average_speed);
        } else {
            windSpeed.setText(activity.getResources().getString(R.string.mean_value) + '\n' + "---");
        }

        if (!no_data && !data.wind_qf_native.equals(QualityFactor.NOT_AVALIABLE)) {
            windGusts.setText(activity.getResources().getString(R.string.wind_gust_short) + '\n' + gusts_speed);
        } else {
            windGusts.setText(activity.getResources().getString(R.string.wind_gust_short) + '\n' + "---");

        }

        if (!no_data && !data.wind_qf_native.equals(QualityFactor.NOT_AVALIABLE)) {
            windDirection.setText(activity.getResources().getString(R.string.wind_direction_short) + '\n' + data.direction + activity.getResources().getString(R.string.degrees_sign));
        } else {
            windDirection.setText(activity.getResources().getString(R.string.wind_direction_short) + '\n' + "---");
        }

        // check if temperature is avaliable in input data set
        if (!no_data && !data.temperature_qf_native.equals(QualityFactor.NOT_AVALIABLE)) {
            temperature.setText(activity.getResources().getString(R.string.temperature_short) + '\n' + String.format("%s", data.getTemperatureStr(true, false)));
        } else {
            temperature.setText(activity.getResources().getString(R.string.temperature_short) + '\n' + "---");
        }

        if (!no_data && !old_data) {
            String hour_max_gusts = String.format("%s", data.getHourWindgustsStr(true));
            String hour_min_avg = String.format("%s", data.getHourMinWindspeedStr(true));

            pressure.setText(activity.getResources().getString(R.string.qnh) + ": " + String.format("%d hPa", data.qnh));
            maxGust.setText(activity.getResources().getString(R.string.max_1h_gust) + ": " + hour_max_gusts);
            minAverage.setText(activity.getResources().getString(R.string.min_1h_avg) + ": " + hour_min_avg);
        } else if (!no_data && old_data) {
            maxGust.setText(activity.getResources().getString(R.string.warning));
            maxGust.setTextColor(Color.RED);
            minAverage.setText(activity.getResources().getString(R.string.station_doesnt_transmit));
            pressure.setText(activity.getResources().getString(R.string.for_longer_than_2_hours));
        } else {
            maxGust.setText(activity.getResources().getString(R.string.no_data));
            maxGust.setTextColor(Color.RED);
            minAverage.setText("");
            minAverage.setTextColor(Color.RED);
            pressure.setText("");
            pressure.setTextColor(Color.RED);
        }
    }

    @Override
    public void setActivity(Activity act) {
        activity = act;
    }
}
