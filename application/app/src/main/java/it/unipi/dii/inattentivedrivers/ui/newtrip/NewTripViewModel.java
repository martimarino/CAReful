package it.unipi.dii.inattentivedrivers.ui.newtrip;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NewTripViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NewTripViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}