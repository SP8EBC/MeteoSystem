package cc.pogoda.mobile.meteosystem.activity.handler;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import cc.pogoda.mobile.meteosystem.activity.FavouritesActivity;
import cc.pogoda.mobile.meteosystem.type.ParceableFavsCallReason;
import cc.pogoda.mobile.meteosystem.type.ParceableStationsList;

public class MainActImageButtonFavouritesClickEvent implements View.OnClickListener{

    AppCompatActivity parent;

    Intent intent;

    public MainActImageButtonFavouritesClickEvent(AppCompatActivity parent) {
        this.parent = parent;

        intent = new Intent(this.parent, FavouritesActivity.class);

        ParceableFavsCallReason callReason = new ParceableFavsCallReason(ParceableFavsCallReason.Reason.FAVOURITES);
        intent.putExtra("callReason", callReason);
    }

    @Override
    public void onClick(View view) {

        parent.startActivity(intent);
    }
}
