package cc.pogoda.mobile.pogodacc.activity.trend.temperature;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TemperatureTrendViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TemperatureTrendViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}