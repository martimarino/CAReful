package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.Task;

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

    /* General variables */
    private boolean foregroundActivity;
    public int resumeTimes = -1;        //avoid first activity start call

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = StartTripBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gps = new GpsManager(this, foregroundActivity, mMap);
        mot = new MotionManager();
        cam = new CameraManager(this, binding);
        mic = new MicrophoneManager(this, null);

        createTask();

    }

    public void createTask() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        Task<Location> task = GpsManager.fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {

            if (location != null) {
                GpsManager.currentLocation = location;
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                assert supportMapFragment != null;
                supportMapFragment.getMapAsync(StartTrip.this);
            }

        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        gps.setMap(this, mMap);

    }

    @Override
    protected void onPause() {
        super.onPause();
//        mot.sensorManager.unregisterListener(acc.accelerometerEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeTimes++;
        if(resumeTimes > 0) {
            Log.d("Resume times: ", String.valueOf(resumeTimes));
            Toast.makeText(StartTrip.this, "Resume detected", Toast.LENGTH_SHORT).show();
        }
//        mot.sensorManager.registerListener(acc.accelerometerEventListener, acc.accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
        foregroundActivity = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        foregroundActivity = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        foregroundActivity = false;
        mMap = null;
    }
}
