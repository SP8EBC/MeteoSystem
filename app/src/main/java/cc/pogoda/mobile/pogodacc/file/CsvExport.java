package cc.pogoda.mobile.pogodacc.file;

import android.content.Context;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import cc.pogoda.mobile.pogodacc.type.WeatherStation;
import cc.pogoda.mobile.pogodacc.type.web.ListOfStationData;
import cc.pogoda.mobile.pogodacc.type.web.StationData;

public class CsvExport {

    public static boolean exportToCsv(ListOfStationData data, WeatherStation station, Context context, OutputStream out) {

        OutputStreamWriter writer = new OutputStreamWriter(out);

        try {
            writer.write("epoch,temperature,pressure,humidity,winddirection,windspeed,windgusts\r\n");


            for (StationData d : data.list_of_station_data) {

                writer.write(   String.valueOf(d.epoch) + "," +
                                    String.valueOf(d.temperature) + "," +
                                    String.valueOf(d.pressure) + "," +
                                    String.valueOf(d.humidity) + "," +
                                    String.valueOf(d.winddir) + "," +
                                    String.valueOf(d.windspeed) + "," +
                                    String.valueOf(d.windgusts) + "\r\n");

            }

            writer.flush();
            writer.close();

            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }
}
