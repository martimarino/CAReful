package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import it.unipi.dii.inattentivedrivers.MainActivity;
import it.unipi.dii.inattentivedrivers.R;

public class StartTrip extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SensorEventListener gyroscopeEventListener;
    private ImageButton button;
    private ArrayList<Float> array;
    private int fallDetected;
    private int countFall;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_trip);
        button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity();
            }
        });
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
                //Y-Axis
                array.add(sensorEvent.values[1]);
                if (array.size()>100){
                    array.remove(0);
                    array.add(sensorEvent.values[1]);
                }
                if(sensorEvent.values[1] > 3){
                    for (int i=0; i<array.size()-1; i++){
                        if(array.get(i)>0 && array.get(i+1)<0){
                            fallDetected = 1;
                        }
                        if(fallDetected==1){
                            if(array.get(i)<0 && array.get(i+1)>0){
                                fallDetected = 2;
                                break;
                            }
                        }
                    }
                }
                if (fallDetected==2){
                    array.clear();
                    fallDetected=0;
                    countFall = countFall + 1;
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

    public void openActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
