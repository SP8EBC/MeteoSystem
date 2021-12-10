package cc.pogoda.mobile.meteosystem.type;

import android.app.Activity;

import cc.pogoda.mobile.meteosystem.type.web.Summary;

/**
 * This is an interface which is used to implement classes used for updating the activity content
 * asynchronously (in the background)
 */
public interface StationActivityElements {

    public void updateFromSummary(Summary s, AvailableParameters enabledForStation);

    public void setActivity(Activity act);
}
