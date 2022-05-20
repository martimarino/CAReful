package it.unipi.dii.inattentivedrivers.sensors;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class AccelerometerManager {

    public SensorEventListener accelerometerEventListener;
    public ArrayList<Float> array;
    public int fallDetected;
    public static int countFall;
    public SensorManager sensorManager;
    public Sensor accelerometerSensor;

    public AccelerometerManager(Activity activity) {

        array = new ArrayList<>();
        fallDetected = 0;
        countFall = 0;

        accelerometerEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                Log.d("count fall: ", String.valueOf(countFall));

                int index = 0;
                //Y-Axis
                if (array.size() < 70) {
                    array.add(sensorEvent.values[1]);
                } else {
                    array.remove(0);
                    array.add(sensorEvent.values[1]);
                }
                for (int i = 0; i < array.size() - 1; i++) {
                    if (array.get(i) > 0 && array.get(i + 1) < 0) {
                        fallDetected = 1;                       //negative acceleration
                        break;
                    }
                    if (fallDetected == 1) {
                        if (array.get(i) < 0 && array.get(i + 1) > 0) {
                            fallDetected = 2;                   //positive acceleration
                            index = i;
                            break;
                        }
                    }
                    if (fallDetected == 2) {
                        for (int j = index; j < array.size() - 1; j++) {
                            if (array.get(j) > 3 && array.get(j + 1) < 3) {
                                fallDetected = 3;                //final acceleration
                                break;
                            }
                        }

                    }
                }
                if (fallDetected == 3) {
                    //Log.d("array", String.valueOf(array));
                    Toast.makeText(activity, "Count fall: " + AccelerometerManager.countFall, Toast.LENGTH_SHORT).show();

                    Log.d("count fall: ", String.valueOf(countFall));
                    array.clear();
                    fallDetected = 0;
                    countFall = countFall + 1;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

        };

    }

}
