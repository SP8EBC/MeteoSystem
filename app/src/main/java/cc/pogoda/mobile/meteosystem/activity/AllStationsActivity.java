package cc.pogoda.mobile.meteosystem.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cc.pogoda.mobile.meteosystem.R;
import cc.pogoda.mobile.meteosystem.adapter.WeatherStationRecyclerViewAdapter;
import cc.pogoda.mobile.meteosystem.type.ParceableFavsCallReason;
import cc.pogoda.mobile.meteosystem.type.ParceableStationsList;
import cc.pogoda.mobile.meteosystem.type.web.Summary;

import android.os.Bundle;

public class AllStationsActivity extends AppCompatActivity {

    RecyclerView recyclerViewAllStations;

    Summary test;

    ParceableStationsList allStationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_stations);

        allStationsList = getIntent().getParcelableExtra("all_stations");

        recyclerViewAllStations = findViewById(R.id.recyclerViewAllStations);

        WeatherStationRecyclerViewAdapter adapter = null;

        adapter = new WeatherStationRecyclerViewAdapter(allStationsList.getList(), this, ParceableFavsCallReason.Reason.ALL_STATIONS);

        recyclerViewAllStations.setAdapter(adapter);

        recyclerViewAllStations.setLayoutManager(new LinearLayoutManager(this));
    }
}