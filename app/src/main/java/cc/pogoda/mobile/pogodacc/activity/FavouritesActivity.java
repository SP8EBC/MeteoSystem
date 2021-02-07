package cc.pogoda.mobile.pogodacc.activity;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.adapter.WeatherStationRecyclerViewAdapter;
import cc.pogoda.mobile.pogodacc.config.AppConfiguration;
import cc.pogoda.mobile.pogodacc.dao.AllStationsDao;

public class FavouritesActivity extends AppCompatActivity {

    RecyclerView recyclerViewFavourites;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_favourites, menu);

        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppConfiguration.favourites == null || AppConfiguration.favourites.favourites.size() == 0) {
            setContentView(R.layout.activity_favourites_empty);
        }
        else {
            setContentView(R.layout.activity_favourites);

            recyclerViewFavourites = findViewById(R.id.recyclerViewFavourites);

            if (recyclerViewFavourites != null) {
                WeatherStationRecyclerViewAdapter adapter = null;

                adapter = new WeatherStationRecyclerViewAdapter(AppConfiguration.favourites.favourites, this);

                recyclerViewFavourites.setAdapter(adapter);

                recyclerViewFavourites.setLayoutManager(new LinearLayoutManager(this));
            }
        }
    }
}
