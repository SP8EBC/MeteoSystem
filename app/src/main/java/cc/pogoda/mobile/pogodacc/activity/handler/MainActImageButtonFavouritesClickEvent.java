package cc.pogoda.mobile.pogodacc.activity.handler;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import cc.pogoda.mobile.pogodacc.activity.FavouritesActivity;
import cc.pogoda.mobile.pogodacc.type.ParceableStationsList;

public class MainActImageButtonFavouritesClickEvent implements View.OnClickListener{

    AppCompatActivity parent;

    Intent intent;

    public MainActImageButtonFavouritesClickEvent(AppCompatActivity parent, ParceableStationsList favs) {
        this.parent = parent;

        intent = new Intent(this.parent, FavouritesActivity.class);
    }

    @Override
    public void onClick(View view) {

        parent.startActivity(intent);
    }
}
