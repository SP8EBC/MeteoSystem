package cc.pogoda.mobile.pogodacc.activity.trend.pressure;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PressureTrendViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PressureTrendViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}