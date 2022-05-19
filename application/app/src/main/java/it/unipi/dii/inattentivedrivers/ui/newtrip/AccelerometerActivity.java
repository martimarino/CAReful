package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import it.unipi.dii.inattentivedrivers.MainActivity;
import it.unipi.dii.inattentivedrivers.R;

public class AccelerometerActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SensorEventListener gyroscopeEventListener;
    private ArrayList<Float> array;
    private int fallDetected;
    private int countFall;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accelerometer_activity);
        array = new ArrayList<>();
        fallDetected = 0;
        countFall = 0;
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (gyroscopeSensor == null){
            Toast.makeText(this, "The device has no Gyroscope", Toast.LENGTH_SHORT).show();
            finish();
        }
        gyroscopeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                int index = 0;
                //Y-Axis
                if (array.size()<70){
                    array.add(sensorEvent.values[1]);
                }
                else{
                    array.remove(0);
                    array.add(sensorEvent.values[1]);
                }
                for (int i=0; i<array.size()-1; i++){
                    if(array.get(i)>0 && array.get(i+1)<0){
                        fallDetected = 1;                       //negative acceleration
                        break;
                    }
                    if(fallDetected==1){
                        if(array.get(i)<0 && array.get(i+1)>0){
                            fallDetected = 2;                   //positive acceleration
                            index = i;
                            break;
                        }
                    }
                    if(fallDetected==2){
                        for (int j = index; j< array.size()-1; j++){
                            if(array.get(j)>3 && array.get(j+1)<3){
                                fallDetected = 3;                //final acceleration
                                break;
                            }
                        }

                    }
                }
                if (fallDetected==3){
                    //Log.d("array", String.valueOf(array));
                    array.clear();
                    fallDetected=0;
                    countFall = countFall + 1;
                    TextView tv = findViewById(R.id.textView);
                    tv.setText("Falls detected: " + Integer.toString(countFall));
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(gyroscopeEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(gyroscopeEventListener);
    }
}
