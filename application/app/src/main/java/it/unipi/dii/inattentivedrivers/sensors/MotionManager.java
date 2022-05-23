package it.unipi.dii.inattentivedrivers.sensors;

import static android.content.Context.SENSOR_SERVICE;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import it.unipi.dii.inattentivedrivers.databinding.MotionActivityBinding;
import it.unipi.dii.inattentivedrivers.ui.newtrip.MotionActivity;
import it.unipi.dii.inattentivedrivers.ui.newtrip.StartTrip;

public class MotionManager {

    public MotionActivityBinding motionActivityBinding;
    public SensorEventListener accelerometerEventListener;
    public SensorEventListener gyroscopeEventListener;
    public MotionActivity motionActivity;
    public SensorManager sensorManager;
    public Sensor accelerometerSensor;
    public Sensor gyroscopeSensor;
    public StartTrip startTrip;
    public Context context;
    public ArrayList<Float> array;
    public static int countFall;
    public boolean fall;
    public boolean usageDetected;
    public static final double highThreshold = 10.0;
    public static final double lowThreshold = 8.0;
    public static final int size = 8;


    public MotionManager(MotionActivity motionActivity, MotionActivityBinding motionActivityBinding, Context context) {
        array = new ArrayList<>();
        fall = false;
        countFall = 0;
        this.motionActivity = motionActivity;
        this.motionActivityBinding = motionActivityBinding;
        this.context = context;
        sensorManager = (SensorManager) this.context.getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (accelerometerSensor == null){
            Toast.makeText(motionActivity,"The device has no Accelerometer", Toast.LENGTH_SHORT).show();
        }
        accelerometerEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                double magnitude = Math.sqrt((sensorEvent.values[0] * sensorEvent.values[0]) + (sensorEvent.values[1] * sensorEvent.values[1]) + (sensorEvent.values[2] * sensorEvent.values[2]));
                if (array.size() < size) {
                    array.add((float) magnitude);
                } else {
                    array.clear();
                    array.add((float) magnitude);
                }

                for (int i = 0; i < array.size(); i++) {
                    if (array.get(i) < lowThreshold) {
                        for (int j = i + 1; j < array.size(); j++) {
                            if (array.get(j) > highThreshold) {
                                fall = true;
                                break;
                            }
                        }
                    }
                }

                if (fall==true) {
                    Log.d("vettore", String.valueOf(array));
                    array.clear();
                    fall = false;
                    countFall = countFall + 1;
                    TextView tv = motionActivityBinding.textView;
                    tv.setText("Falls detected: " + Integer.toString(countFall));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        gyroscopeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if(sensorEvent.values[0]>1.5f){
                    usageDetected = true;
                }
                if(sensorEvent.values[0]<-1.5f){
                    usageDetected = true;
                }
                if(sensorEvent.values[1]>1.5f){
                    usageDetected = true;
                }
                if(sensorEvent.values[1]<-1.5f){
                    usageDetected = true;
                }
                if(sensorEvent.values[2]>1.5f){
                    usageDetected = true;
                }
                if(sensorEvent.values[2]<-1.5f){
                    usageDetected = true;
                }

                if (usageDetected==true){
                    TextView tv = motionActivityBinding.textView2;
                    tv.setText("Usage detected: " + usageDetected);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        sensorManager.registerListener(accelerometerEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(gyroscopeEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public MotionManager(StartTrip startTrip) {
        array = new ArrayList<>();
        fall = false;
        countFall = 0;
        this.startTrip = startTrip;
        accelerometerEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                double magnitude = Math.sqrt((sensorEvent.values[0] * sensorEvent.values[0]) + (sensorEvent.values[1] * sensorEvent.values[1]) + (sensorEvent.values[2] * sensorEvent.values[2]));
                if (array.size() < size) {
                    array.add((float) magnitude);
                } else {
                    array.clear();
                    array.add((float) magnitude);
                }

                for (int i = 0; i < array.size(); i++) {
                    if (array.get(i) < lowThreshold) {
                        for (int j = i + 1; j < array.size(); j++) {
                            if (array.get(j) > highThreshold) {
                                fall = true;
                                break;
                            }
                        }
                    }
                }

                if (fall==true) {
                    array.clear();
                    fall = false;
                    countFall = countFall + 1;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
    }

    public static int getCountFall() {
        return countFall;
    }


    // RICORDARE ONPAUSE E ONRESUME!!!!!!!!!!!!!!!!!!!!!!
}
