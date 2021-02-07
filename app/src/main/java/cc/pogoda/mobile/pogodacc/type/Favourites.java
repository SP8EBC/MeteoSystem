package cc.pogoda.mobile.pogodacc.type;

import java.util.LinkedList;
import java.util.List;

import cc.pogoda.mobile.pogodacc.config.AppConfiguration;

public class Favourites {

    public Favourites() {
        favourites = new LinkedList<WeatherStation>();
    }

    public List<WeatherStation> favourites;

    public boolean addFav(WeatherStation s) {
        boolean out = false;

        if (AppConfiguration.favourites != null && AppConfiguration.favourites.favourites != null) {

            if (!AppConfiguration.favourites.favourites.contains(s)) {
                AppConfiguration.favourites.favourites.add(s);
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

        if (AppConfiguration.favourites != null && AppConfiguration.favourites.favourites != null) {
            out = AppConfiguration.favourites.favourites.remove(s);
        }

        return out;
    }
}
