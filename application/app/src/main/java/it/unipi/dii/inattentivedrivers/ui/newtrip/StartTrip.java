package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import it.unipi.dii.inattentivedrivers.R;
import it.unipi.dii.inattentivedrivers.databinding.StartTripBinding;

public class StartTrip extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private StartTripBinding binding;
    public static Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private boolean foregroundActivity;

    public StartTrip() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = StartTripBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("******************ON MAP READY*******************");
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        //mMap.addMarker(new MarkerOptions().position(current).title("Current location"));

        // zoomLevel is set at 10 for a City-level view
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(current)
                .zoom(18)
                .bearing(currentLocation.getBearing())
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, zoomLevel));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        mMap.setMyLocationEnabled(true);

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
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {

            if (location != null) {
                currentLocation = location;
                System.out.println("************* getCurrentLocation *************");

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
                System.out.println("*********** onLocationResult **********");
                //Toast.makeText(getApplicationContext()," location result is  " + locationResult, Toast.LENGTH_LONG).show();

                if (locationResult == null) {
                    //Toast.makeText(getApplicationContext(),"current location is null ", Toast.LENGTH_LONG).show();
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //Toast.makeText(getApplicationContext(),"current location is " + location.getLongitude(), Toast.LENGTH_LONG).show();

                        //TODO: UI updates.
                        currentLocation = locationResult.getLastLocation();

                        LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(current)
                                .zoom(18)
                                .build();

                        System.out.println("FOREGROUD: " + foregroundActivity);

                        if (foregroundActivity && mMap != null) {
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            mMap.setMyLocationEnabled(true);
                        }
                    }
                }
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

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
}
