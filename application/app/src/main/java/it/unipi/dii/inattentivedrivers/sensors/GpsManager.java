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
import it.unipi.dii.inattentivedrivers.ui.newtrip.StartTrip;

public class GpsManager {

    public static Location currentLocation;
    public static FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private static LocationCallback locationCallback;
    private static LocationRequest locationRequest;

    private Location prev = null;
    double diff_vector_lat_1, diff_vector_lon_1;
    double diff_vector_lat_2 = 1000, diff_vector_lon_2;

    ArrayList<Double> array;


    public GpsManager(Activity activity, boolean foregroundActivity, GoogleMap mMap) {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        array = new ArrayList<>(10);
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
                            double rt = calculateRoadTortuosity();
                            String twisty = "";
                            Log.d("tortuosity", String.valueOf(rt));
                            if(rt == 999){
                                twisty = "Start";
                            }
                            else{
                                twisty = String.valueOf(rt);
                            }
                            Toast.makeText(activity.getApplicationContext(), twisty, Toast.LENGTH_SHORT).show();

                            LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

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
                }
            };
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

        }

        public boolean calculateDifference(){

            if(prev == null) {       //first vector missing
                prev = currentLocation;
                return false;
            }

            if(diff_vector_lat_2 == 1000) {     //first difference vector null
                diff_vector_lat_2 = currentLocation.getLatitude() - prev.getLatitude();
                diff_vector_lon_2 = currentLocation.getLongitude() - prev.getLongitude();
                prev = currentLocation;
                return false;
            }

            diff_vector_lat_1 = diff_vector_lat_2;
            diff_vector_lon_1 = diff_vector_lon_2;

            diff_vector_lat_2 = currentLocation.getLatitude() - prev.getLatitude();
            diff_vector_lon_2 = currentLocation.getLongitude() - prev.getLongitude();
            prev = currentLocation;
            return true;

        }

        public double calculateCosineSimilarity() {

            boolean difference = calculateDifference();
            if (difference == false) {
                return 999;
            }

            double prev_module = Math.sqrt((diff_vector_lat_1*diff_vector_lat_1) + (diff_vector_lon_1*diff_vector_lon_1));
            double actual_module = Math.sqrt((diff_vector_lat_2*diff_vector_lat_2) + (diff_vector_lon_2*diff_vector_lon_2));

            double scalar_product = (diff_vector_lat_1 * diff_vector_lat_2) + (diff_vector_lon_1 * diff_vector_lon_2);
            double similarity = scalar_product / (prev_module * actual_module);

            return similarity;

        }

        private double calculateRoadTortuosity() {

            if(array.size() == 10)      //circular array
                array.remove(0);

            double val = calculateCosineSimilarity();
            if (val == 999){
                return val;
            }
            array.add(val);

            //similar -> 1   dissimilar -> 0

            double avg, sum = 0;
            for(int i = 0; i < array.size(); i++) {
                sum += array.get(i);
            }
            avg = sum/array.size();

            return avg;

        }


    }
