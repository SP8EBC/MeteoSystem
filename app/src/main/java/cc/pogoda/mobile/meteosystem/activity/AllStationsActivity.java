package cc.pogoda.mobile.meteosystem.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cc.pogoda.mobile.meteosystem.Main;
import cc.pogoda.mobile.meteosystem.R;
import cc.pogoda.mobile.meteosystem.adapter.WeatherStationRecyclerViewAdapter;
import cc.pogoda.mobile.meteosystem.type.ParceableFavsCallReason;
import cc.pogoda.mobile.meteosystem.type.ParceableStationsList;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;
import cc.pogoda.mobile.meteosystem.type.web.Summary;

import android.os.Bundle;

import java.util.List;

public class AllStationsActivity extends AppCompatActivity {

    RecyclerView recyclerViewAllStations;

    Summary test;

    private List<WeatherStation> allStationsList;

    Main main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_stations);

        main = (Main) getApplication();

        //allStationsList = getIntent().getParcelableExtra("all_stations");
        allStationsList = main.getListOfAllStations();

        recyclerViewAllStations = findViewById(R.id.recyclerViewAllStations);

        WeatherStationRecyclerViewAdapter adapter = null;

        adapter = new WeatherStationRecyclerViewAdapter(allStationsList, this, ParceableFavsCallReason.Reason.ALL_STATIONS);

        recyclerViewAllStations.setAdapter(adapter);

        recyclerViewAllStations.setLayoutManager(new LinearLayoutManager(this));
    }
}