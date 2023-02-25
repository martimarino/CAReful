package it.unipi.dii.careful.sensors;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.unipi.dii.careful.ui.newtrip.MapsActivity;
import it.unipi.dii.careful.ui.newtrip.StartTrip;

public class GpsManager {

    public static Location currentLocation;
    public static FusedLocationProviderClient fusedLocationProviderClient;
    public LatLng current;

    ArrayList arr;
    int size;
    float avgSpeed;

    public GpsManager(Activity activity) {
        size = StartTrip.samplingPeriod / MapsActivity.getInterval();
        arr = new ArrayList(size);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    public void updateMap(Activity activity, GoogleMap mMap, boolean foregroundActivity) {

        current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        /* specifies parameters to update map */
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(current)
                .zoom(18)
                .build();

        /* move map to current location */
        if (foregroundActivity && mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
    }

    public void calculateAvgSpeed() {
        if(arr.size() == size) {
            float s = 0;
            for(int i = 0; i < arr.size(); i++)
                s = s + (Float) arr.get(i);
            arr.clear();
            avgSpeed = s/size;
        } else {
            arr.add(currentLocation.getSpeed());
        }
    }

    public int getAvgSpeed() {
        /* Different level of roads */
        if (avgSpeed<=30){
            return 1;
        }
        else if (avgSpeed>30 && avgSpeed<=70){
            return 2;
        }
        else if (avgSpeed>70 && avgSpeed<=90){
            return 3;
        }
        else{
            return 4;
        }
    }

    public String getAddress(Activity activity) {
        try {
            Geocoder geo = new Geocoder(activity.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
            if (addresses.isEmpty()) {
                return "Waiting for Location";
            }
            else {
                if (addresses.size() > 0) {
                    String addr = addresses.get(0).getAddressLine(0);
                    return addr;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
            return e.getMessage();
        }
        return null;
    }
}