package cc.pogoda.mobile.meteosystem.type;

import com.github.mikephil.charting.data.Entry;

public interface StationDetailsPlot {

    void updateLabels(String date, Entry entry);
}
