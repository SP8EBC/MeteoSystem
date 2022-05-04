package cc.pogoda.mobile.meteosystem.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import cc.pogoda.mobile.meteosystem.Main;
import cc.pogoda.mobile.meteosystem.R;
import cc.pogoda.mobile.meteosystem.adapter.WeatherStationRecyclerViewAdapter;
import cc.pogoda.mobile.meteosystem.type.AllStationsReceivedEvent;
import cc.pogoda.mobile.meteosystem.type.ParceableFavsCallReason;
import cc.pogoda.mobile.meteosystem.type.StartStationsRefreshEvent;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;

public class FavouritesActivity extends AppCompatActivity {

    RecyclerView recyclerViewFavourites;

    private SwipeRefreshLayout refreshLayout;

    List<WeatherStation> favourites = new LinkedList<>();

    List<WeatherStation> sortedFavourites;

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
        }

        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        recyclerViewFavourites = findViewById(R.id.recyclerViewFavourites);
        refreshLayout = findViewById(R.id.refreshViewFavourites);
        refreshLayout.setOnRefreshListener(
                () -> ((Main) getApplication()).startGetAllStationsService()
        );

        callReason = getIntent().getParcelableExtra("callReason");
        adapter = new WeatherStationRecyclerViewAdapter(favourites,
                this, callReason.getReason());
        recyclerViewFavourites.setAdapter(adapter);
        recyclerViewFavourites.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        updateStationList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        adapter.stopUpdater();
        super.onDestroy();
    }

    private void updateStationList() {
        List favList = ((Main) getApplication()).getFavs();

        if(favList != null) {
            favourites.clear();
            favourites.addAll(favList);
            refreshLayout.setRefreshing(false);
            sortedFavourites = new ArrayList<>(favourites);
            sortedFavourites.sort(new WxStationComparator());
            adapter.notifyDataSetChanged();
            if (!favList.isEmpty()) {
                adapter.createAndStartUpdater();
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void allStationsEventHandler(@NonNull AllStationsReceivedEvent event) {
        updateStationList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startStationsRefreshEventHandler(@NonNull StartStationsRefreshEvent event) {
        refreshLayout.setRefreshing(true);
        Toast.makeText(this, R.string.refreshing_station_list, Toast.LENGTH_SHORT).show();
    }
}
