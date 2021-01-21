package cc.pogoda.mobile.pogodacc.activity;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import cc.pogoda.mobile.pogodacc.R;

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

        setContentView(R.layout.activity_favourites);

        recyclerViewFavourites = findViewById(R.id.recyclerViewFavourites);

        if (recyclerViewFavourites != null) {
            
        }
    }
}
