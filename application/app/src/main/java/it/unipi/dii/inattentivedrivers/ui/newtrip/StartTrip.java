package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.Timer;
import java.util.TimerTask;

import it.unipi.dii.inattentivedrivers.R;
import it.unipi.dii.inattentivedrivers.databinding.StartTripBinding;
import it.unipi.dii.inattentivedrivers.sensors.CameraManager;
import it.unipi.dii.inattentivedrivers.sensors.GpsManager;
import it.unipi.dii.inattentivedrivers.sensors.MicrophoneManager;
import it.unipi.dii.inattentivedrivers.sensors.MotionManager;

public class StartTrip extends AppCompatActivity implements OnMapReadyCallback {

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
    public int attentionLevel = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = StartTripBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gps = new GpsManager(this);
        mot = new MotionManager(this, getApplicationContext());
        cam = new CameraManager(this, binding);
        //mic = new MicrophoneManager(this);

        getCurrentLocation();
        startAttentionDetection();

    }

    public void startAttentionDetection(){
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                attentionLevel--;
                binding.determinateBar.setProgress(attentionLevel);

            }
        }, 5000,5000);
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
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                assert supportMapFragment != null;
                supportMapFragment.getMapAsync(StartTrip.this);
            }

        });

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null)
                    return;

                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //TODO: UI updates.
                        gps.currentLocation = locationResult.getLastLocation();
                        gps.updateMap(StartTrip.this, mMap, foregroundActivity);
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
