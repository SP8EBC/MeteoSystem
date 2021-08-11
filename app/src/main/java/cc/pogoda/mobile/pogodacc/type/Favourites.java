package cc.pogoda.mobile.pogodacc.type;

import android.content.Context;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import cc.pogoda.mobile.pogodacc.config.AppConfiguration;
import cc.pogoda.mobile.pogodacc.file.FileNames;

public class Favourites {

    private List<WeatherStation> favourites;

    private FileNames fileNames;

    public Favourites(FileNames fns) {

        favourites = new LinkedList<WeatherStation>();

        fileNames = fns;
    }

    private void storeOnDisk() {

    }

    public boolean addFav(WeatherStation s) {
        boolean out = false;

        if (AppConfiguration.favourites != null && AppConfiguration.favourites.getFavourites() != null) {

            if (!AppConfiguration.favourites.getFavourites().contains(s)) {
                AppConfiguration.favourites.getFavourites().add(s);
                out = true;
            }
            else {
                ;
            }
        }

        return out;
    }

    public boolean removeFav(WeatherStation s) {
        boolean out = false;

        if (AppConfiguration.favourites != null && AppConfiguration.favourites.getFavourites() != null) {
            out = AppConfiguration.favourites.getFavourites().remove(s);
        }

        return out;
    }

    public List<WeatherStation> getFavourites() {
        return favourites;
    }
}
