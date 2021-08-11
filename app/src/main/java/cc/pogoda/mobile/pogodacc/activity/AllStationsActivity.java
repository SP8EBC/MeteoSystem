package cc.pogoda.mobile.pogodacc.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cc.pogoda.mobile.pogodacc.R;
import cc.pogoda.mobile.pogodacc.adapter.WeatherStationRecyclerViewAdapter;
import cc.pogoda.mobile.pogodacc.dao.AllStationsDao;
import cc.pogoda.mobile.pogodacc.dao.LastStationDataDao;
import cc.pogoda.mobile.pogodacc.dao.SummaryDao;
import cc.pogoda.mobile.pogodacc.dao.mock.AllStationsDaoMock;
import cc.pogoda.mobile.pogodacc.type.ParceableStationsList;
import cc.pogoda.mobile.pogodacc.type.web.ListOfStationData;
import cc.pogoda.mobile.pogodacc.type.web.Summary;

import android.os.Bundle;

import java.io.IOException;

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

        adapter = new WeatherStationRecyclerViewAdapter(allStationsList.getList(), this);

        recyclerViewAllStations.setAdapter(adapter);

        recyclerViewAllStations.setLayoutManager(new LinearLayoutManager(this));
    }
}