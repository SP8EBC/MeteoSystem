package cc.pogoda.mobile.pogodacc.file;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cc.pogoda.mobile.pogodacc.type.WeatherStation;
import cc.pogoda.mobile.pogodacc.type.web.ListOfStationData;

public class ExcelExport {


    public static boolean exportToExcel(ListOfStationData data, WeatherStation station, Context context) {

        Cell cell;

        Workbook workbook = new HSSFWorkbook();

        Sheet sheet = workbook.createSheet(station.getSystemName());

        Row header = sheet.createRow(3);

        cell = header.createCell(0);
        cell.setCellValue("UNIX Epoch Timestamp");

        cell = header.createCell(1);
        cell.setCellValue("Date / Time");

        cell = header.createCell(2);
        cell.setCellValue("Temperature");

        cell = header.createCell(3);
        cell.setCellValue("QNH pressure");

        cell = header.createCell(4);
        cell.setCellValue("Humidity");

        cell = header.createCell(5);
        cell.setCellValue("Wind Direction");

        cell = header.createCell(6);
        cell.setCellValue("Wind Average Speed");

        cell = header.createCell(7);
        cell.setCellValue("Wind Gusts");

        File file = new File(context.getExternalFilesDir(null), "test.xls");
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);

            workbook.write(fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

}
