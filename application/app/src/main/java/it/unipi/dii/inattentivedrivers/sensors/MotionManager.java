package it.unipi.dii.inattentivedrivers.sensors;

import static android.content.Context.SENSOR_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import it.unipi.dii.inattentivedrivers.databinding.MotionActivityBinding;
import it.unipi.dii.inattentivedrivers.ui.newtrip.CameraActivity;
import it.unipi.dii.inattentivedrivers.ui.newtrip.MotionActivity;
import it.unipi.dii.inattentivedrivers.ui.newtrip.StartTrip;

public class MotionManager {

    public MotionActivityBinding motionActivityBinding;
    public static SensorEventListener accelerometerEventListener;
    public static SensorEventListener gyroscopeEventListener;
    public static SensorEventListener magnetometerEventListener;
    public MotionActivity motionActivity;
    public static SensorManager sensorManager;
    public static Sensor accelerometerSensor;
    public static Sensor gyroscopeSensor;
    public static Sensor magnetometerSensor;
    public StartTrip startTrip;
    public Context context;
    public ArrayList<Float> array;
    public ArrayList<Float> azimuts;
    public static int countFall;
    public boolean fall;
    public boolean usageDetected;
    public static final double highThreshold = 18.0;
    public static final double lowThreshold = 5.0;
    public static final float tol = 0.15F;
    public static final int size = 8;
    public static float azimut;
    public float[] mGeomagnetic;
    public float[] mGravity;
    public static int curvatureIndex;




    public MotionManager(MotionActivity motionActivity, MotionActivityBinding motionActivityBinding, Context context) {
        array = new ArrayList<>();
        azimuts = new ArrayList<>();
        fall = false;
        countFall = 0;
        this.motionActivity = motionActivity;
        this.motionActivityBinding = motionActivityBinding;
        this.context = context;

//        final Handler mHandler = new Handler();
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //Log.d("azimut", String.valueOf(azimut));
//                //Toast.makeText(motionActivity, String.valueOf(azimut), Toast.LENGTH_SHORT).show();
//                mHandler.postDelayed(this, 100);
//            }
//        }, 100);
        initializeMotion(this.motionActivity);
    }

    public void initializeMotion(Activity activity){

        sensorManager = (SensorManager) this.context.getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (accelerometerSensor == null || gyroscopeSensor == null && magnetometerSensor == null){
            Toast.makeText(activity,"The device has no sensors to detect motion", Toast.LENGTH_SHORT).show();
        }
        accelerometerEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                mGravity = sensorEvent.values;
//                if (mGravity != null && mGeomagnetic != null){
//                    getOrientation();
//                }
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
                    //Log.d("vettore", String.valueOf(array));
                    array.clear();
                    fall = false;
                    countFall = countFall + 1;
                    if (activity instanceof MotionActivity) {
                        TextView tv = motionActivityBinding.textView;
                        tv.setText("Falls detected: " + Integer.toString(countFall));
                    }
                    else {
                        Log.d("caduto", "si");
                    }
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
                    if(activity instanceof MotionActivity) {
                        TextView tv = motionActivityBinding.textView2;
                        tv.setText("Usage detected: " + usageDetected);
                    } else {
                        Log.d("usato", "si");
                    }
                    usageDetected = false;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        magnetometerEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                mGeomagnetic = sensorEvent.values;
                if (mGravity != null && mGeomagnetic != null){
                    getOrientation(activity);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        sensorManager.registerListener(accelerometerEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(gyroscopeEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(magnetometerEventListener, magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void getOrientation(Activity activity){
        float R[] = new float[9];
        float I[] = new float[9];
        boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
        if (success) {
            float orientation[] = new float[3];
            SensorManager.getOrientation(R, orientation);
            azimut = Math.abs(orientation[0]); // orientation contains: azimut, pitch and roll
            azimuts.add(azimut);
            if(azimuts.size()==100){
                float sum = 0;
                Log.d("azimuts", String.valueOf(azimuts));
                for (int i = 0; i<azimuts.size()-1; i++){
                    if (Math.abs(azimuts.get(i) - azimuts.get(i+1)) > tol){
                        sum = sum + Math.abs(azimuts.get(i) - azimuts.get(i+1));
                    }
                }
                if (sum<10){
                    curvatureIndex = 1;
                }
                else if (sum>=10 && sum<=60) {
                    curvatureIndex = 2;
                }
                else{
                    curvatureIndex = 3;
                }
                if(activity instanceof MotionActivity) {
                    Toast.makeText(activity,"Sum: " + String.valueOf(sum) + " CurvatureIndex: " + String.valueOf(curvatureIndex), Toast.LENGTH_LONG).show();
                }
                Log.d("sum", String.valueOf(sum));
                Log.d("riskIndex", String.valueOf(curvatureIndex));
                azimuts.clear();
            }
        }



    }

    public MotionManager(StartTrip startTrip, Context context) {

        array = new ArrayList<>();
        azimuts = new ArrayList<>();
        fall = false;
        countFall = 0;
        this.startTrip = startTrip;
        this.context = context;

//        final Handler mHandler = new Handler();
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //Log.d("azimut", String.valueOf(azimut));
//                Toast.makeText(startTrip, String.valueOf(azimut), Toast.LENGTH_SHORT).show();
//                mHandler.postDelayed(this, 100);
//            }
//        }, 100);
        initializeMotion(this.startTrip);
    }

    public static int getCountFall() {
        return countFall;
    }

    public static float getAzimut() {
        return azimut;
    }

    // RICORDARE ONPAUSE E ONRESUME!!!!!!!!!!!!!!!!!!!!!!
}
