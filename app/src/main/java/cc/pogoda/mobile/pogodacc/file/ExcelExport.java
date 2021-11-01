package cc.pogoda.mobile.pogodacc.file;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import cc.pogoda.mobile.pogodacc.type.WeatherStation;
import cc.pogoda.mobile.pogodacc.type.web.ListOfStationData;
import cc.pogoda.mobile.pogodacc.type.web.StationData;

public class ExcelExport {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");

    private static double round (double in) {
        double out = 0;

        long temp = (long)(in * 10);

        out = temp / 10.0d;

        return out;
    }

    public static boolean exportToExcel(ListOfStationData data, WeatherStation station, Context context, OutputStream out) {

        if (out == null) {
            return false;
        }

        if (data == null || data.list_of_station_data.length == 0) {
            return false;
        }

        int rowNumber = 0;

        Cell cell;

        ZonedDateTime first = ZonedDateTime.ofInstant(Instant.ofEpochSecond(data.list_of_station_data[0].epoch), ZoneId.of(station.getTimezone()));
        String generalTimezone = first.format(DateTimeFormatter.ofPattern("zzzz"));

        Workbook workbook = new HSSFWorkbook();

        CellStyle left = workbook.createCellStyle();
        left.setAlignment(CellStyle.ALIGN_LEFT);

        Sheet sheet = workbook.createSheet(station.getSystemName());

        {
            Row stationame = sheet.createRow(rowNumber++);
            Cell name = stationame.createCell(0);
            name.setCellValue("Station name:");
            name = stationame.createCell(1);
            name.setCellValue(station.getDisplayedName());

            Row localization = sheet.createRow(rowNumber++);
            Cell loc = localization.createCell(0);
            loc.setCellValue("Location:");
            loc = localization.createCell(1);
            loc.setCellValue(station.getDisplayedLocation());

            Row coordinates = sheet.createRow(rowNumber++);
            Cell coord = coordinates.createCell(0);
            coord.setCellValue("Coordinates:");
            coord = coordinates.createCell(1);
            coord.setCellValue("Lat " + station.getLat() + " , Lon " + station.getLon());

            Row timezone = sheet.createRow(rowNumber++);
            Cell tz = timezone.createCell(0);
            tz.setCellValue("All date and time as in:");
            tz = timezone.createCell(1);
            tz.setCellValue(station.getTimezone());

            Row generaltz = sheet.createRow(rowNumber++);
            Cell gtz = generaltz.createCell(0);
            gtz.setCellValue("Timezone in this export:");
            gtz = generaltz.createCell(1);
            gtz.setCellValue(generalTimezone);

            Row offset = sheet.createRow(rowNumber++);
            Cell off = offset.createCell(0);
            off.setCellValue("Time offset for the timezone:");
            off = offset.createCell(1);
            off.setCellValue(first.getOffset().toString());

            Row aprscall = sheet.createRow(rowNumber++);
            Cell call = aprscall.createCell(0);
            call.setCellValue("APRS Callsign:");
            call = aprscall.createCell(1);
            call.setCellValue(station.getCallsignSsid());
        }

        rowNumber++;
        rowNumber++;
        rowNumber++;

        Row header = sheet.createRow(rowNumber++);

        cell = header.createCell(0);
        cell.setCellValue("UNIX Epoch Timestamp");
        cell.setCellStyle(left);

        cell = header.createCell(1);
        cell.setCellValue("Date / Time [DD-MM-YYYY 24HH:MM]");
        cell.setCellStyle(left);

        cell = header.createCell(2);
        cell.setCellValue("Temperature [C]");
        cell.setCellStyle(left);

        cell = header.createCell(3);
        cell.setCellValue("QNH pressure [hPa]");
        cell.setCellStyle(left);

        cell = header.createCell(4);
        cell.setCellValue("Humidity [%]");
        cell.setCellStyle(left);

        cell = header.createCell(5);
        cell.setCellValue("Wind Direction");
        cell.setCellStyle(left);

        cell = header.createCell(6);
        cell.setCellValue("Wind Average Speed [m/s]");
        cell.setCellStyle(left);

        cell = header.createCell(7);
        cell.setCellValue("Wind Gusts [m/s]");
        cell.setCellStyle(left);

        // put data into output file
        for (StationData d : data.list_of_station_data) {
            Row r = sheet.createRow(rowNumber++);

            Cell epoch = r.createCell(0);
            epoch.setCellValue(d.epoch);
            epoch.setCellStyle(left);

            ZonedDateTime zoneddatetime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(d.epoch), ZoneId.of(station.getTimezone()));
            Cell datetime = r.createCell(1);
            datetime.setCellValue(zoneddatetime.format(DateTimeFormatter.ofPattern("dd-M-yyyy HH:mm:ss")));
            datetime.setCellStyle(left);

            Cell temperature = r.createCell(2);
            temperature.setCellValue(round(d.temperature));
            temperature.setCellStyle(left);

            Cell pressure = r.createCell(3);
            pressure.setCellValue(d.pressure);
            pressure.setCellStyle(left);

            Cell humidity = r.createCell(4);
            humidity.setCellValue(d.humidity);
            humidity.setCellStyle(left);

            Cell winddir = r.createCell(5);
            winddir.setCellValue(d.winddir);
            winddir.setCellStyle(left);

            Cell windspeed = r.createCell(6);
            windspeed.setCellValue(round(d.windspeed));
            windspeed.setCellStyle(left);

            Cell windgust = r.createCell(7);
            windgust.setCellValue(round(d.windgusts));
            windgust.setCellStyle(left);
        }

        sheet.setColumnWidth(0, 6600);
        sheet.setColumnWidth(1, 5900);
        sheet.setColumnWidth(2, 3900);
        sheet.setColumnWidth(3, 4600);
        sheet.setColumnWidth(4, 2900);
        sheet.setColumnWidth(5, 3200);
        sheet.setColumnWidth(6, 3700);
        sheet.setColumnWidth(7, 3200);

        try {
            workbook.write(out);

            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

}
