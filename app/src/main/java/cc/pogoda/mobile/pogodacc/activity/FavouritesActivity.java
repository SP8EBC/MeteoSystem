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
import cc.pogoda.mobile.pogodacc.type.ParceableFavsCallReason;
import cc.pogoda.mobile.pogodacc.type.ParceableStationsList;

public class FavouritesActivity extends AppCompatActivity {

    RecyclerView recyclerViewFavourites;

    ParceableStationsList favourites;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_favourites, menu);

        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        favourites = getIntent().getParcelableExtra("favs");

        ParceableFavsCallReason callReason = getIntent().getParcelableExtra("callReason");

        if (favourites == null || favourites.getList().size() == 0) {
            setContentView(R.layout.activity_favourites_empty);
        }
        else {
            setContentView(R.layout.activity_favourites);

            recyclerViewFavourites = findViewById(R.id.recyclerViewFavourites);

            if (recyclerViewFavourites != null) {
                WeatherStationRecyclerViewAdapter adapter = null;

                adapter = new WeatherStationRecyclerViewAdapter(favourites.getList(), this, callReason.getReason());

                recyclerViewFavourites.setAdapter(adapter);

                recyclerViewFavourites.setLayoutManager(new LinearLayoutManager(this));
            }
        }
    }
}
