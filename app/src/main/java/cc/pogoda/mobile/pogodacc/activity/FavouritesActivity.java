package cc.pogoda.mobile.pogodacc.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Comparator;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.adapter.WeatherStationRecyclerViewAdapter;
import cc.pogoda.mobile.pogodacc.config.AppConfiguration;
import cc.pogoda.mobile.pogodacc.type.ParceableFavsCallReason;
import cc.pogoda.mobile.pogodacc.type.ParceableStationsList;
import cc.pogoda.mobile.pogodacc.type.WeatherStation;

public class FavouritesActivity extends AppCompatActivity {

    RecyclerView recyclerViewFavourites;

    ParceableStationsList favourites, sortedFavourites;

    boolean sorting = false;

    WeatherStationRecyclerViewAdapter adapter = null;

    ParceableFavsCallReason callReason;

    private class WxStationComparator implements Comparator<WeatherStation> {

        @Override
        public int compare(WeatherStation station, WeatherStation t1) {
            String name = station.getDisplayedName();
            String name1 = t1.getDisplayedName();

            return (name.compareTo(name1));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_favourites, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //return super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.fav_sort_alph_oder:
                sorting = true;

                if (recyclerViewFavourites != null) {
                    adapter = new WeatherStationRecyclerViewAdapter(sortedFavourites.getList(), this, callReason.getReason());

                    adapter.createAndStartUpdater();

                    recyclerViewFavourites.setAdapter(adapter);
                }

                break;
            case R.id.fav_sort_add_order:
                sorting = false;

                if (recyclerViewFavourites != null) {
                    adapter = new WeatherStationRecyclerViewAdapter(favourites.getList(), this, callReason.getReason());

                    adapter.createAndStartUpdater();

                    recyclerViewFavourites.setAdapter(adapter);
                }

                break;
            case R.id.fav_remove_noext:
                break;
        }

        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        favourites = getIntent().getParcelableExtra("favs");

        sortedFavourites = new ParceableStationsList(favourites);

        sortedFavourites.getList().sort(new WxStationComparator());

        callReason = getIntent().getParcelableExtra("callReason");

        if (favourites == null || favourites.getList().size() == 0) {
            setContentView(R.layout.activity_favourites_empty);
        }
        else {
            setContentView(R.layout.activity_favourites);

            recyclerViewFavourites = findViewById(R.id.recyclerViewFavourites);

            if (recyclerViewFavourites != null) {
                adapter = new WeatherStationRecyclerViewAdapter(favourites.getList(), this, callReason.getReason());

                adapter.createAndStartUpdater();

                recyclerViewFavourites.setAdapter(adapter);

                recyclerViewFavourites.setLayoutManager(new LinearLayoutManager(this));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (adapter != null) {
            adapter.stopUpdater();
        }
    }
}
