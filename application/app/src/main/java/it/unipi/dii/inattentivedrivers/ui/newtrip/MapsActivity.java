package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

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

import it.unipi.dii.inattentivedrivers.R;
import it.unipi.dii.inattentivedrivers.databinding.ActivityMapsBinding;
import it.unipi.dii.inattentivedrivers.sensors.GpsManager;
import it.unipi.dii.inattentivedrivers.sensors.MotionManager;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static ActivityMapsBinding binding;
    private static final int REQUEST_CODE = 101;
    private boolean foregroundActivity;         /* move map to current only if the app is visible */
    public static int interval = 5;     /* interval of map updates */

    GpsManager gps;
    MotionManager mot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gps = new GpsManager(this);
        getCurrentLocation();
        mot = new MotionManager(this, binding, getApplicationContext());
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

        Task<Location> task = GpsManager.fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {

            if (location != null) {
                GpsManager.currentLocation = location;
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                assert supportMapFragment != null;
                supportMapFragment.getMapAsync(MapsActivity.this);
            }

        });

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(interval * 1000);
        locationRequest.setFastestInterval(interval * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null)
                    return;

                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //TODO: UI updates.
                        GpsManager.currentLocation = locationResult.getLastLocation();
                        gps.updateMap(MapsActivity.this, mMap, foregroundActivity);
                    }
                }
            }
        };
        GpsManager.fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        foregroundActivity = false;
        mMap = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        foregroundActivity = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        foregroundActivity = true;
    }

    public static int getInterval() {
        return interval;
    }
}
