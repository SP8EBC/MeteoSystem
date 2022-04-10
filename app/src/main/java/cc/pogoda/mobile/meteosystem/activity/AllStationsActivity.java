package cc.pogoda.mobile.meteosystem.activity;

import android.os.Bundle;
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

import cc.pogoda.mobile.meteosystem.Main;
import cc.pogoda.mobile.meteosystem.R;
import cc.pogoda.mobile.meteosystem.adapter.WeatherStationRecyclerViewAdapter;
import cc.pogoda.mobile.meteosystem.type.AllStationsReceivedEvent;
import cc.pogoda.mobile.meteosystem.type.ParceableFavsCallReason;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;

public class AllStationsActivity extends AppCompatActivity {

    private final List<WeatherStation> allStationsList = new LinkedList<>();
    private SwipeRefreshLayout refreshLayout;
    private WeatherStationRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_all_stations);


        refreshLayout = findViewById(R.id.refreshAllStationsView);
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        ((Main) getApplication()).startGetAllStationsService();
                    }
                }
        );

        RecyclerView recyclerViewAllStations = findViewById(R.id.recyclerViewAllStations);
        adapter = new WeatherStationRecyclerViewAdapter(
                allStationsList, this, ParceableFavsCallReason.Reason.ALL_STATIONS);
        recyclerViewAllStations.setAdapter(adapter);
        recyclerViewAllStations.setLayoutManager(new LinearLayoutManager(this));

        updateStationList(((Main) getApplication()).getListOfAllStations());
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void updateStationList(List<WeatherStation> stations){
        if (stations != null) {
            allStationsList.clear();
            allStationsList.addAll(stations);
            refreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
        } else {
            refreshLayout.setRefreshing(true);
            Toast.makeText(this,"Odświeżanie listy stacji", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void allStationsEventHandler(@NonNull AllStationsReceivedEvent event) {
        updateStationList(event.getStations());
    }
}