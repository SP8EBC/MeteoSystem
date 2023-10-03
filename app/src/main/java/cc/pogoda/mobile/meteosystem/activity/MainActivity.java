package cc.pogoda.mobile.meteosystem.activity;

// https://www.softicons.com/web-icons/vector-stylish-weather-icons-by-bartosz-kaszubowski/sun-rays-cloud-icon#google_vignette

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.color.MaterialColors;

import org.greenrobot.eventbus.EventBus;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.tinylog.Logger;

import java.io.FileNotFoundException;
import java.util.Locale;

import cc.pogoda.mobile.meteosystem.Main;
import cc.pogoda.mobile.meteosystem.R;
import cc.pogoda.mobile.meteosystem.activity.handler.MainActImageButtonAllStationsClickEvent;
import cc.pogoda.mobile.meteosystem.activity.handler.MainActImageButtonExportClickEvent;
import cc.pogoda.mobile.meteosystem.activity.handler.MainActImageButtonFavouritesClickEvent;
import cc.pogoda.mobile.meteosystem.activity.handler.MainActImageButtonSettingsClickEvent;
import cc.pogoda.mobile.meteosystem.config.AppConfiguration;
import cc.pogoda.mobile.meteosystem.file.CopyLog;
import cc.pogoda.mobile.meteosystem.type.ThemeColours;

public class MainActivity extends AppCompatActivity {

    private Main main;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Logger.info("[onDestroy]");
    }

    /**
     * Called when a user goes back to the main screen
     */
    @Override
    protected void onResume() {
        super.onResume();
        Logger.info("[onResume]");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.info("[onCreate]");

        main = (Main) getApplication();

        if (AppConfiguration.locale != null && !AppConfiguration.locale.equals("default")) {
            Logger.debug("[AppConfiguration.locale = "
                    + AppConfiguration.locale + "]");
            Locale locale = new Locale(AppConfiguration.locale);
            Locale.setDefault(locale);
            Resources resources = this.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            Logger.debug("[locale = " + locale.toLanguageTag() + "]");
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }

        setContentView(R.layout.activity_main);

        ImageButton imageButtonAllStations = findViewById(R.id.imageButtonAllStations);
        imageButtonAllStations.setOnClickListener(
                    new MainActImageButtonAllStationsClickEvent(this));

        ImageButton imageButtonFavourites = findViewById(R.id.imageButtonFavourites);
        imageButtonFavourites.setOnClickListener(
                new MainActImageButtonFavouritesClickEvent(this));

        ImageButton exportButton = findViewById(R.id.imageButtonExport);
        exportButton.setOnClickListener(new MainActImageButtonExportClickEvent(this));

        ImageButton settingsButton = findViewById(R.id.imageButtonSettings);
        settingsButton.setOnClickListener(
                new MainActImageButtonSettingsClickEvent(this, main.getConfFile()));

        ThemeColours colours = ((Main) getApplication()).getThemeColours();

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();

        //MaterialColors.getColor(, R.attr.colorOnPrimary);

        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        TypedArray arr = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
        colours.colorPrimary = arr.getColor(0, -1);
        arr.recycle();

        theme.resolveAttribute(R.attr.colorPrimaryVariant, typedValue, true);
        arr = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimaryVariant});
        colours.colorPrimaryVariant = arr.getColor(0, -1);
        arr.recycle();

        theme.resolveAttribute(R.attr.colorOnPrimary, typedValue, true);
        arr = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorOnPrimary});
        colours.colorOnPrimary = arr.getColor(0, -1);
        arr.recycle();

        theme.resolveAttribute(R.attr.colorSecondary, typedValue, true);
        arr = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorSecondary});
        colours.colorSecondary = arr.getColor(0, -1);
        arr.recycle();

        theme.resolveAttribute(R.attr.colorSecondaryVariant, typedValue, true);
        arr = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorSecondaryVariant});
        colours.colorSecondaryVariant = arr.getColor(0, -1);
        arr.recycle();

        theme.resolveAttribute(R.attr.colorOnSecondary, typedValue, true);
        arr = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorOnSecondary});
        colours.colorOnSecondary = arr.getColor(0, -1);
        arr.recycle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 123 && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            Logger.debug("[requestCode = 123][uri.getPath() = "
                    + uri.getPath() + "]");

            grantUriPermission(getPackageName(), uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            try {
                CopyLog.forDay(main.getFileNames(), LocalDateTime.now(),
                        getContentResolver().openOutputStream(uri));
            } catch (FileNotFoundException e) {
                Logger.error("[FileNotFoundException][e = " + e.toString() +"]");
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //return super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.menu_item_translation_authors: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("ENG: Mateusz Lubecki\r\n" +
                        "CZE: Sylwiusz Pachel\r\n" +
                        "GER: Jakub Fiałek\r\n" +
                        "LAT: Andris Stikāns\r\n" +
                        "UKR, RUS: Влад Поливач \r\n" +
                        "(Wład Polywacz)\r\n\r\nProgram Icon: Bartosz Kaszubowski");
                builder.setPositiveButton(R.string.ok, (DialogInterface var1, int var2) -> {
                    var1.dismiss();
                });
                builder.create();
                builder.show();

                break;
            }

            case (R.id.menu_item_log_export): {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TITLE, "meteosystem_" +
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".log");

                startActivityForResult(intent, 123);

                break;
            }
        }

        return true;
    }

    public boolean listOfAllStationsReady() {
        return main.listOfAllStationsReady() && main != null;
    }

    public boolean listOfAllFavsReady() {
        return main.listOfFavsReady() && main != null;
    }

}