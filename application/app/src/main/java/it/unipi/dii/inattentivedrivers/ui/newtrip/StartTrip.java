package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StartTrip extends AppCompatActivity {

    public static Location startPoint;
    public static Location endPoint;

    public StartTrip() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startPoint = MapsActivity.currentLocation;
    }

    @Override
    protected void onDestroy() {
        endPoint = MapsActivity.currentLocation;
        super.onDestroy();
    }
}
