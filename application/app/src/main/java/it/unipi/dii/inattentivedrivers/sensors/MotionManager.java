package it.unipi.dii.inattentivedrivers.sensors;

import static android.content.Context.SENSOR_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import it.unipi.dii.inattentivedrivers.databinding.ActivityMapsBinding;
import it.unipi.dii.inattentivedrivers.databinding.ActivityMotionBinding;
import it.unipi.dii.inattentivedrivers.ui.newtrip.MapsActivity;
import it.unipi.dii.inattentivedrivers.ui.newtrip.MotionActivity;
import it.unipi.dii.inattentivedrivers.ui.newtrip.StartTrip;

public class MotionManager {

    public static final float usageThreshold = 1.9f;
    public static final int firstIndexThreshold = 10;
    public static final int secondIndexThreshold = 60;

    public ActivityMotionBinding motionActivityBinding;
    public MotionActivity motionActivity;
    public MapsActivity mapsActivity;
    public StartTrip startTrip;
    public Context context;
    public ActivityMapsBinding activityMapsBinding;

    public static SensorEventListener accelerometerEventListener;
    public static SensorEventListener gyroscopeEventListener;
    public static SensorEventListener magnetometerEventListener;

    public static SensorManager sensorManager;
    public static Sensor accelerometerSensor;
    public static Sensor gyroscopeSensor;
    public static Sensor magnetometerSensor;

    public ArrayList<Float> array;
    public ArrayList<Float> azimuts;
    public int countFall;
    public boolean fall;
    public boolean usageDetected;
    public static final double highThreshold = 18.0;
    public static final double lowThreshold = 5.0;
    public static final float tol = 0.15F;
    public static final int size = 8;
    public float azimut;
    public float[] mGeomagnetic;
    public float[] mGravity;
    public int curvatureIndex;

    public MotionManager(MotionActivity motionActivity, ActivityMotionBinding motionActivityBinding, Context context) {

        array = new ArrayList<>();
        azimuts = new ArrayList<>();
        fall = false;
        countFall = 0;
        this.motionActivity = motionActivity;
        this.motionActivityBinding = motionActivityBinding;
        this.context = context;

        initializeMotion(this.motionActivity, true, false);

    }

    public MotionManager(MapsActivity mapsActivity, ActivityMapsBinding activityMapsBinding, Context context) {

        array = new ArrayList<>();
        azimuts = new ArrayList<>();
        fall = false;
        countFall = 0;
        this.mapsActivity = mapsActivity;
        this.activityMapsBinding = activityMapsBinding;
        this.context = context;

        initializeMotion(this.mapsActivity, false, true);

    }

    public MotionManager(StartTrip startTrip, Context context, boolean accGyro, boolean magn) {

        array = new ArrayList<>();
        azimuts = new ArrayList<>();
        fall = false;
        countFall = 0;
        this.startTrip = startTrip;
        this.context = context;

        initializeMotion(this.startTrip, accGyro, magn);
    }

    public void initializeMotion(Activity activity, boolean accGyro, boolean magn) {

        sensorManager = (SensorManager) this.context.getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (accelerometerSensor == null && gyroscopeSensor == null && magnetometerSensor == null) {
            Toast.makeText(activity, "The device has no sensors to detect motion", Toast.LENGTH_SHORT).show();
            return;
        }

        accelerometerEventListener = new SensorEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                mGravity = sensorEvent.values;

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

                if (fall) {
                    array.clear();
                    fall = false;
                    countFall = countFall + 1;
                    if (activity instanceof MotionActivity) {
                        TextView tv = motionActivityBinding.tvFalls;
                        tv.setText("Falls detected: " + Integer.toString(countFall));
                    } else {
                        Log.d("Fall detection:", String.valueOf(countFall));
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        sensorManager.registerListener(accelerometerEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);


        if(accGyro) {
            gyroscopeEventListener = new SensorEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    if ((sensorEvent.values[0] > usageThreshold) ||
                            (sensorEvent.values[0] < -usageThreshold) ||
                            (sensorEvent.values[1] > usageThreshold) ||
                            (sensorEvent.values[1] < -usageThreshold) ||
                            (sensorEvent.values[2] > usageThreshold) ||
                            (sensorEvent.values[2] < -usageThreshold))
                        usageDetected = true;

                    if (usageDetected) {
                        if (activity instanceof MotionActivity) {
                            TextView tv = motionActivityBinding.tvUsages;
                            tv.setText("Usage detected: " + usageDetected);
                        } else {
                            Log.d("used", String.valueOf(usageDetected));
                        }
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {
                }
            };
            sensorManager.registerListener(gyroscopeEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if(magn) {

            magnetometerEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {

                    mGeomagnetic = sensorEvent.values;

                    if (mGravity != null && mGeomagnetic != null) {
                        getOrientation(activity);
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {
                }
            };
            sensorManager.registerListener(magnetometerEventListener, magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @SuppressLint("SetTextI18n")
    public void getOrientation(Activity activity){

        float R[] = new float[9];
        float I[] = new float[9];
        boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

        if (success) {
            float[] orientation = new float[3];
            SensorManager.getOrientation(R, orientation);
            azimut = Math.abs(orientation[0]); // orientation contains: azimut, pitch and roll
            azimuts.add(azimut);
            if(azimuts.size() == 300){
                float sum = 0;
                Log.d("azimuts", String.valueOf(azimuts));
                for (int i = 0; i < azimuts.size()-1; i++){
                    if (Math.abs(azimuts.get(i) - azimuts.get(i+1)) > tol){
                        sum = sum + Math.abs(azimuts.get(i) - azimuts.get(i+1));
                    }
                }
                if (sum< firstIndexThreshold){
                    curvatureIndex = 1;
                }
                else if (sum>=firstIndexThreshold && sum<= secondIndexThreshold) {
                    curvatureIndex = 2;
                }
                else{
                    curvatureIndex = 3;
                }
                if(activity instanceof MapsActivity) {
                    TextView tv = activityMapsBinding.tvTortuosity;
                    tv.setText("Sum: " + sum + "\nCurvatureIndex: " + curvatureIndex);
                }
                Log.d("sum", String.valueOf(sum));
                Log.d("riskIndex", String.valueOf(curvatureIndex));
                azimuts.clear();
            }
        }
    }

    public int getCountFall() {
        return countFall;
    }

    public int getCurvatureIndex() {
        return curvatureIndex;
    }

    public boolean getUsageDetected() {
        return usageDetected;
    }

    public void setCountFall(int countFall) {
        this.countFall = countFall;
    }

    public void setUsageDetected(boolean usageDetected) {
        this.usageDetected = usageDetected;
    }

    // RICORDARE ONPAUSE E ONRESUME!!!!!!!!!!!!!!!!!!!!!!
}
