package cc.pogoda.mobile.meteosystem.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cc.pogoda.mobile.meteosystem.Main;
import cc.pogoda.mobile.meteosystem.R;
import cc.pogoda.mobile.meteosystem.adapter.WeatherStationRecyclerViewAdapter;
import cc.pogoda.mobile.meteosystem.type.ParceableFavsCallReason;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;

public class FavouritesActivity extends AppCompatActivity {

    Main main;

    RecyclerView recyclerViewFavourites;

    List<WeatherStation> favourites, sortedFavourites;

    boolean sorting = false;

    WeatherStationRecyclerViewAdapter adapter = null;

    ParceableFavsCallReason callReason;

    private static class WxStationComparator implements Comparator<WeatherStation> {

        @Override
        public int compare(WeatherStation station, WeatherStation t1) {
            return (station.getDisplayedName().compareTo(t1.getDisplayedName()));
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
                    adapter = new WeatherStationRecyclerViewAdapter(sortedFavourites, this, callReason.getReason());

                    adapter.createAndStartUpdater();

                    recyclerViewFavourites.setAdapter(adapter);
                }

                break;
            case R.id.fav_sort_add_order:
                sorting = false;

                if (recyclerViewFavourites != null) {
                    adapter = new WeatherStationRecyclerViewAdapter(favourites, this, callReason.getReason());

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
        main = (Main)getApplication();

        favourites = main.getFavs();
        if(favourites == null){
            return;
        }

        sortedFavourites = new ArrayList<>(favourites);

        sortedFavourites.sort(new WxStationComparator());

        callReason = getIntent().getParcelableExtra("callReason");

        if (favourites == null || favourites.size() == 0) {
            setContentView(R.layout.activity_favourites_empty);
        }
        else {
            setContentView(R.layout.activity_favourites);

            recyclerViewFavourites = findViewById(R.id.recyclerViewFavourites);

            if (recyclerViewFavourites != null) {
                adapter = new WeatherStationRecyclerViewAdapter(favourites, this, callReason.getReason());

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
