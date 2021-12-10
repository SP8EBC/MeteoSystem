package cc.pogoda.mobile.meteosystem.file;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import cc.pogoda.mobile.meteosystem.type.WeatherStation;

public class FavouritiesFile {

    FileNames fileNames;

    public FavouritiesFile(FileNames fns) {
        fileNames = fns;
    }

    public List<WeatherStation> loadFavourites() {

        List<WeatherStation> out = new LinkedList<>();

        File file = fileNames.getFavJsonFile();

        try {
            // create an input stream to load file content
            FileInputStream fns = new FileInputStream(file);
            InputStreamReader streamReader = new InputStreamReader(fns);

            // create a place fo JSON content
            char buffer[] = new char[(int) file.length()];

            try {
                // read the content of file
                streamReader.read(buffer);

                streamReader.close();

                // parse JSON to array
                JSONArray root = new JSONArray(new String(buffer));

                if (root != null) {
                    for (int i = 0 ; i < root.length(); i++) {

                        // create new weather station data object
                        WeatherStation station = new WeatherStation();

                        // get onlu 'systemName' as the rest will be copied from current 'allStationsList'
                        station.setSystemName(root.getJSONObject(i).getString("systemName"));

                        out.add(station);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
            }

        } catch (FileNotFoundException e) {
            return null;
        }

        return out;
    }

    public void persistFavourities(List<WeatherStation> favourites) throws IOException {

        // do nothing in case that no statations have been added to favourites yet
        if (favourites == null || favourites.size() == 0)
            return;

        String jsonData = "";

        // main array for all stations
        JSONArray mainArray = new JSONArray();

        for (WeatherStation wx : favourites) {
            JSONObject obj = new JSONObject();

            try {
                obj.put("systemName", wx.getSystemName());
                obj.put("displayedName", wx.getDisplayedName());
                obj.put("displayedLocation", wx.getDisplayedLocation());
                obj.put("sponsorUrl", wx.getSponsorUrl());
                obj.put("imageUrl", wx.getImageUrl());
                obj.put("imageAlign", wx.getImageAlign());
                obj.put("lat", wx.getLat());
                obj.put("lon", wx.getLon());

                mainArray.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        jsonData = mainArray.toString();

        if (jsonData != null && jsonData.length() > 0) {
            File output = fileNames.getFavJsonFile();

            // checks if file exists and delete it if yes
            if (output.exists()) {
                output.delete();
                output.createNewFile();
            }

            FileOutputStream outputStream = new FileOutputStream(output);

            // write JSON content to file on disk
            outputStream.write(jsonData.getBytes());

            // synchronize and close stream
            outputStream.flush();
            outputStream.close();
        }
    }
}
