package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleObserver;

public class SmartphoneActivity extends AppCompatActivity implements LifecycleObserver {

    public static int resumeTimes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.start_trip);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        resumeTimes++;
        Toast.makeText(getApplicationContext(), "Times resumed: " + resumeTimes,
                Toast.LENGTH_LONG).show();
    }
}