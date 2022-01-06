package cc.pogoda.mobile.meteosystem.file;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.tinylog.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import cc.pogoda.mobile.meteosystem.config.AppConfiguration;

public class ConfigurationFile {

    FileNames fileNames;

    public ConfigurationFile(Context context) {
        fileNames = new FileNames(context);
    }

    public void restoreFromFile() {

        File file = fileNames.getAppConfigurationFile();

        // create an input stream to load file content
        FileInputStream fns = null;
        try {
            fns = new FileInputStream(file);
            InputStreamReader streamReader = new InputStreamReader(fns);

            // create a place fo JSON content
            char buffer[] = new char[(int) file.length()];

            // read the content of file
            streamReader.read(buffer);

            streamReader.close();

            JSONObject mainObject = new JSONObject(String.valueOf(buffer));

            AppConfiguration.replaceMsWithKnots = mainObject.getBoolean("replaceMsWithKnots");
            AppConfiguration.locale = mainObject.getString("locale");
            AppConfiguration.decimationPeriod = mainObject.getInt("decimationPeriodMinutes");

        } catch (IOException | JSONException e) {
            e.printStackTrace();

            Logger.error("[ConfigurationFile][restoreFromFile][e = " + e.getLocalizedMessage() +"]");

            AppConfiguration.locale = "default";
            AppConfiguration.replaceMsWithKnots = false;
            AppConfiguration.decimationPeriod = 0;
        }

    }

    public void storeToFile() {
        String jsonData = "";

        try {
            FileOutputStream outputStream = new FileOutputStream(fileNames.getAppConfigurationFile());

            JSONObject masterObject = new JSONObject();

            masterObject.put("replaceMsWithKnots", AppConfiguration.replaceMsWithKnots);
            masterObject.put("locale", AppConfiguration.locale);
            masterObject.put("decimationPeriodMinutes", AppConfiguration.decimationPeriod);

            jsonData = masterObject.toString();

            // write JSON content to file on disk
            outputStream.write(jsonData.getBytes());

            // synchronize and close stream
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
