package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import it.unipi.dii.inattentivedrivers.databinding.MotionActivityBinding;
import it.unipi.dii.inattentivedrivers.sensors.MotionManager;

public class MotionActivity extends AppCompatActivity {

    MotionManager mot;
    MotionActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = MotionActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mot = new MotionManager(this, binding, getApplicationContext());

    }

    @Override
    protected void onResume(){
        super.onResume();
        MotionManager.sensorManager.registerListener(MotionManager.accelerometerEventListener, MotionManager.accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
        MotionManager.sensorManager.registerListener(MotionManager.gyroscopeEventListener, MotionManager.gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }


    @Override
    protected void onPause(){
        super.onPause();
        MotionManager.sensorManager.unregisterListener(MotionManager.accelerometerEventListener);
        MotionManager.sensorManager.unregisterListener(MotionManager.gyroscopeEventListener);
    }
}