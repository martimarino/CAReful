package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import it.unipi.dii.inattentivedrivers.databinding.AccelerometerActivityBinding;
import it.unipi.dii.inattentivedrivers.sensors.AccelerometerManager;

public class AccelerometerActivity extends AppCompatActivity {
    public static AccelerometerActivityBinding binding;
    public AccelerometerManager acc;
    public Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = AccelerometerActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();

        acc = new AccelerometerManager(this, binding, context);

    }
}