package cc.pogoda.mobile.pogodacc.activity.trend.wind;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WindTrendViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public WindTrendViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}