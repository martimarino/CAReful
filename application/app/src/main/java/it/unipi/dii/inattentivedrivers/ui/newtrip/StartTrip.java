package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

import it.unipi.dii.inattentivedrivers.R;
import it.unipi.dii.inattentivedrivers.databinding.StartTripBinding;
import it.unipi.dii.inattentivedrivers.sensors.CameraManager;
import it.unipi.dii.inattentivedrivers.sensors.GpsManager;
import it.unipi.dii.inattentivedrivers.sensors.MicrophoneManager;
import it.unipi.dii.inattentivedrivers.sensors.MotionManager;
import it.unipi.dii.inattentivedrivers.ui.history.Trip;


public class StartTrip extends AppCompatActivity implements OnMapReadyCallback {

    boolean camSelected, gpsSelected, motSelected, micSelected;

    MotionManager mot;
    CameraManager cam;
    GpsManager gps;
    MicrophoneManager mic;

    /* Map variables */
    public static GoogleMap mMap;
    public static StartTripBinding binding;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private static final int REQUEST_CODE = 101;

    /* General variables */
    private boolean foregroundActivity;
    public int resumeTimes = -1;        //avoid first activity start call

    Timer t;
    public float disattentionLevel;
    public float prevDisattentionLevel = 0;
    public float attentionLevel = 100f;
    public static int samplingPeriod = 60;     //in seconds
    float alpha = 0.7f;
    float noise, drow, head, fall, usage;
    float curv, speed;
    float progressBar;

    Button stop;
    public LocalDateTime timeDeparture;
    public LocalDateTime timeArrival;
    public float duration;
    public int score;
    public String departure = null;
    public String arrival;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = StartTripBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();

        timeDeparture = LocalDateTime.now();
        score = 0;

        if (extras != null) {
            micSelected = extras.getBoolean("mic");
            gpsSelected = extras.getBoolean("gps");
            camSelected = extras.getBoolean("cam");
            motSelected = extras.getBoolean("mot");
        }

        if(micSelected)
            mic = new MicrophoneManager(this);

        if(gpsSelected)
            gps = new GpsManager(this);

        if(motSelected)
            mot = new MotionManager(this, getApplicationContext());

        if(camSelected)
            cam = new CameraManager(this, binding);

        setStopButton();
        getCurrentLocation();
        startAttentionDetection();

    }

    public void startAttentionDetection(){
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                noise = mic.getNoiseDetections();       // 12/30
                drow = cam.getDrowsinessCounter();      // 30/30
                head = cam.getTurnedHeadCounter();      // 12/30
                usage = (mot.getUsageDetected()) ? 1 : 0;        // 1/30
                fall = mot.getCountFall();             // 5/30
                curv = mot.getCurvatureIndex();         // 1 - 3
                speed = gps.getAvgSpeed();          // 1 - 4

                disattentionLevel = (float) ((speed/4 + curv/3)* (head/12 + drow/30 + usage + noise/12 + fall/5));
                disattentionLevel = (float) (alpha * disattentionLevel + (1-alpha) * prevDisattentionLevel);
                prevDisattentionLevel = disattentionLevel;
                attentionLevel = (float) (10 - (disattentionLevel));

                progressBar = (float) attentionLevel *100 / 10;
                binding.determinateBar.setProgress((int)progressBar);

                mic.setNoiseDetections(0);
                cam.setDrowsinessCounter(0);
                cam.setTurnedHeadCounter(0);
                mot.setUsageDetected(false);
                mot.setCountFall(0);

            }
        }, 10,60000);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setStopButton() {
        stop = binding.stop;
        stop.setOnClickListener(view -> {
            arrival = gps.getAddress(this);
            timeArrival = LocalDateTime.now();
            score = (int)progressBar;
            Trip trip = new Trip(String.valueOf(timeDeparture), String.valueOf(timeArrival), String.valueOf(score), departure, arrival, getApplicationContext());
            trip.insertData();
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        gps.updateMap(this, mMap, foregroundActivity);
        mMap.addMarker(new MarkerOptions().position(gps.current).title("Departure") .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION
                    }, REQUEST_CODE);
            return;
        }
        Task<Location> task = gps.fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {

            if (location != null) {
                gps.currentLocation = location;
                if (departure == null){
                    departure = gps.getAddress(this);
                }
                gps.currentLocation = location;
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                assert supportMapFragment != null;
                supportMapFragment.getMapAsync(StartTrip.this);
            }

        });

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(MapsActivity.getInterval() * 1000);
        locationRequest.setFastestInterval(MapsActivity.getInterval() * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null)
                    return;

                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //TODO: UI updates.

                        Log.d("Attention level: ", String.valueOf(attentionLevel));
                        Log.d("Prev disatttention: ", String.valueOf(prevDisattentionLevel));
                        Log.d("Disattention level: ", String.valueOf(disattentionLevel));
                        Log.d("Speed: ", String.valueOf(speed));
                        Log.d("Noise: ", String.valueOf(noise));
                        Log.d("Head: ", String.valueOf(head));
                        Log.d("Drow: ", String.valueOf(drow));
                        Log.d("Curve: ", String.valueOf(curv));
                        Log.d("Usage: ", String.valueOf(usage));
                        Log.d("Fall: ", String.valueOf(fall));

                        Toast.makeText(getApplicationContext(),
                                "att: " + attentionLevel
                                + ", disatt: " + disattentionLevel
                                + ", prevDis: " + prevDisattentionLevel
                                        + ", progressBar: " + progressBar
                                + ", speed: " + speed
                                + ", noise: " + noise
                                + ", head: " + head
                                + ", drow: " + drow
                                + ", curve: " + curv
                                + ", usage: " + usage
                                + ", fall: " + fall,
                                Toast.LENGTH_SHORT).show();

                        gps.currentLocation = locationResult.getLastLocation();
                        gps.updateMap(StartTrip.this, mMap, foregroundActivity);
                        gps.calculateAvgSpeed();
                    }
                }
            }
        };
        gps.fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (REQUEST_CODE) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mot.sensorManager.unregisterListener(mot.accelerometerEventListener);
        mot.sensorManager.unregisterListener(mot.gyroscopeEventListener);
        mot.sensorManager.unregisterListener(mot.magnetometerEventListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeTimes++;
        if(resumeTimes > 0) {
            Log.d("Resume times: ", String.valueOf(resumeTimes));
            Toast.makeText(StartTrip.this, "Resume detected", Toast.LENGTH_SHORT).show();
        }
        mot.sensorManager.registerListener(mot.accelerometerEventListener, mot.accelerometerSensor, MotionManager.sensorManager.SENSOR_DELAY_NORMAL);
        mot.sensorManager.registerListener(mot.gyroscopeEventListener, mot.gyroscopeSensor, MotionManager.sensorManager.SENSOR_DELAY_NORMAL);
        mot.sensorManager.registerListener(mot.magnetometerEventListener, mot.magnetometerSensor, MotionManager.sensorManager.SENSOR_DELAY_NORMAL);
        foregroundActivity = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        t.cancel();
        foregroundActivity = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        t.cancel();
        foregroundActivity = false;
        mMap = null;
    }



}
