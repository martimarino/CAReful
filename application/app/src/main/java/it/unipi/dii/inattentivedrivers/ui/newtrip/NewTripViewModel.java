package it.unipi.dii.inattentivedrivers.ui.newtrip;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NewTripViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NewTripViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is New Trip fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}