package it.unipi.dii.inattentivedrivers.sensors;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.lang.reflect.Array;
import java.util.ArrayList;

import it.unipi.dii.inattentivedrivers.R;
import it.unipi.dii.inattentivedrivers.ui.newtrip.MapsActivity;
import it.unipi.dii.inattentivedrivers.ui.newtrip.StartTrip;

public class GpsManager {

    public static Location currentLocation;
    public static FusedLocationProviderClient fusedLocationProviderClient;
    public LatLng current;


    public GpsManager(Activity activity) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    public void updateMap(Activity activity, GoogleMap mMap, boolean foregroundActivity) {

        current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(current)
                .zoom(18)
                .build();

        if (foregroundActivity && mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
    }

}
