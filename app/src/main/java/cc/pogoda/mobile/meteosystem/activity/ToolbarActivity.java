package cc.pogoda.mobile.meteosystem.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

import cc.pogoda.mobile.meteosystem.R;

/**
 * Base class for all activities. The application is drawn edge-to-edge (enforced by Android 15+)
 * and the action bar is a toolbar placed in a layout, which extends behind the status bar.
 * Each activity layout set by {@link #setContentView(int)} is placed below this toolbar,
 * and the toolbar is set as a support action bar, so options menus and titles work as usual.
 */
public abstract class ToolbarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // the toolbar is dark in both day and night theme, so status bar icons must be always light
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT));
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(R.layout.activity_toolbar);

        ViewGroup content = findViewById(R.id.toolbarActivityContent);
        getLayoutInflater().inflate(layoutResID, content, true);

        MaterialToolbar toolbar = findViewById(R.id.toolbarActivityToolbar);
        setSupportActionBar(toolbar);

        View appBar = findViewById(R.id.toolbarActivityAppBar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbarActivityRoot), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            appBar.setPadding(insets.left, insets.top, insets.right, 0);
            content.setPadding(insets.left, 0, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }
}
