package it.unipi.dii.inattentivedrivers.sensors;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;

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

import it.unipi.dii.inattentivedrivers.R;
import it.unipi.dii.inattentivedrivers.ui.newtrip.StartTrip;

public class GpsManager {

    public static Location currentLocation;
    public static FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private static LocationCallback locationCallback;
    private static LocationRequest locationRequest;

    public GpsManager(Activity activity, boolean foregroundActivity, GoogleMap mMap) {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        getCurrentLocation(activity, foregroundActivity, mMap);

    }

    public void setMap(Activity activity, GoogleMap mMap) {

        // Add a marker in current location and move the camera
        LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(current).title("Departure") .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        // zoomLevel is set at 18 for a City-level view
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(current)
                .zoom(18)
                .bearing(currentLocation.getBearing())
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        }

        public void getCurrentLocation(Activity activity, boolean foregroundActivity, GoogleMap mMap) {
            if (ActivityCompat.checkSelfPermission(
                    activity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION
                        }, REQUEST_CODE);
                return;
            }

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
                            currentLocation = locationResult.getLastLocation();

                            LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(current)
                                    .zoom(18)
                                    .build();

                            System.out.println("FOREGROUD: " + foregroundActivity);

                            if (foregroundActivity && mMap != null) {
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    }
