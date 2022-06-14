package cc.pogoda.mobile.meteosystem.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import cc.pogoda.mobile.meteosystem.Main;
import cc.pogoda.mobile.meteosystem.R;
import cc.pogoda.mobile.meteosystem.adapter.WeatherStationRecyclerViewAdapter;
import cc.pogoda.mobile.meteosystem.type.AllStationsReceivedEvent;
import cc.pogoda.mobile.meteosystem.type.ParceableFavsCallReason;
import cc.pogoda.mobile.meteosystem.type.StartStationsRefreshEvent;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;

public class AllStationsActivity extends AppCompatActivity {

    private List<WeatherStation> allStationsList;
    private SwipeRefreshLayout refreshLayout;
    private WeatherStationRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_all_stations);

        refreshLayout = findViewById(R.id.refreshAllStationsView);
        refreshLayout.setOnRefreshListener(
                () -> ((Main) getApplication()).startGetAllStationsService()
        );

        RecyclerView recyclerViewAllStations = findViewById(R.id.recyclerViewAllStations);
        adapter = new WeatherStationRecyclerViewAdapter(
                new LinkedList<>(), this, ParceableFavsCallReason.Reason.ALL_STATIONS);
        recyclerViewAllStations.setAdapter(adapter);
        recyclerViewAllStations.setLayoutManager(new LinearLayoutManager(this));

        handleIntent(getIntent());
    }


    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            filterStationList(query);
        }
    }

    private void filterStationList(String searchQuery) {
        if (allStationsList == null || allStationsList.isEmpty())
            return;

        if(searchQuery.isEmpty())
            adapter.update(allStationsList);

        List<WeatherStation> newList = allStationsList.stream()
                .filter(station -> station.getDisplayedName()
                        .toLowerCase(Locale.ROOT).contains(searchQuery.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        adapter.update(newList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        updateStationList(((Main) getApplication()).getListOfAllStations());
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_all_stations, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterStationList(s);
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            adapter.update(allStationsList);
            return false;
        });

        return true;
    }

    private void updateStationList(List<WeatherStation> stations) {
        if (stations != null) {
            refreshLayout.setRefreshing(false);
            allStationsList = stations;
            adapter.update(stations);
        } else {
            EventBus.getDefault().post(new StartStationsRefreshEvent());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void allStationsEventHandler(@NonNull AllStationsReceivedEvent event) {
        updateStationList(event.getStations());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startStationsRefreshEventHandler(@NonNull StartStationsRefreshEvent event) {
        refreshLayout.setRefreshing(true);
        Toast.makeText(this, R.string.refreshing_station_list, Toast.LENGTH_SHORT).show();
    }
}