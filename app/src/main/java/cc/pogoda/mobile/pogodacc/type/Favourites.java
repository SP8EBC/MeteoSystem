package cc.pogoda.mobile.pogodacc.type;

import java.util.LinkedList;
import java.util.List;

public class Favourites {

    public Favourites() {
        favourites = new LinkedList<WeatherStation>();
    }

    public List<WeatherStation> favourites;
}
