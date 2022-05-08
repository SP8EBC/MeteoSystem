package cc.pogoda.mobile.meteosystem.service;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import org.greenrobot.eventbus.EventBus;
import org.tinylog.Logger;

import java.util.List;

import cc.pogoda.mobile.meteosystem.dao.AllStationsDao;
import cc.pogoda.mobile.meteosystem.type.AllStationsReceivedEvent;
import cc.pogoda.mobile.meteosystem.type.StartStationsRefreshEvent;
import cc.pogoda.mobile.meteosystem.type.WeatherStation;

public class GetAllStationsService extends JobIntentService {
    private static final int JOB_ID = 1;

    public static void enqueueWork(@NonNull Context context, @NonNull Intent intent) {
        enqueueWork(context, GetAllStationsService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        EventBus.getDefault().post(new StartStationsRefreshEvent());
        List<WeatherStation> allStations = new AllStationsDao().getAllStations();
        if (allStations != null){
            EventBus.getDefault().post(new AllStationsReceivedEvent(allStations));
            Logger.debug("onHandleWork done. allStations size:" + allStations.size());
        }
    }

}