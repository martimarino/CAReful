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

    /*
    alfa = 0.5

    Iold = 70  =>  v * t *(testa +, occhi, mic, caduta, gyro) da 0 a 1 min
    Inew = 78  =>  alfa*(v1 * t1 *(testa1, occhi1, mic1, caduta1, gyro1) + (1-alfa)*Iold
    Inew1= 60  =>  alfa*(v2 * t2 *(testa1, occhi1, mic1, caduta1, gyro1) + (1-alfa)*Inew
     */


    @Override
    protected void onResume(){
        super.onResume();
        MotionManager.sensorManager.registerListener(MotionManager.accelerometerEventListener, MotionManager.accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        MotionManager.sensorManager.registerListener(MotionManager.gyroscopeEventListener, MotionManager.gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        MotionManager.sensorManager.registerListener(MotionManager.magnetometerEventListener, MotionManager.magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }


    @Override
    protected void onPause(){
        super.onPause();
        MotionManager.sensorManager.unregisterListener(MotionManager.accelerometerEventListener);
        MotionManager.sensorManager.unregisterListener(MotionManager.gyroscopeEventListener);
        MotionManager.sensorManager.unregisterListener(MotionManager.magnetometerEventListener);

    }
}