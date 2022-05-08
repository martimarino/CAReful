package it.unipi.dii.inattentivedrivers.ui.settings;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import it.unipi.dii.inattentivedrivers.R;
import it.unipi.dii.inattentivedrivers.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address;
    Switch sw_locationupdates, sw_gps;
    public static Location current;

    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSIONS_FINE_LOCATION = 99;

    // Google's API for location services.
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        super.onCreate(savedInstanceState);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.swGps;

        settingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);


        /* ********************************************************** */

        tv_lat = binding.tvLat;
        tv_lon = binding.tvLon;
        tv_altitude = binding.tvAltitude;
        tv_accuracy = binding.tvAccuracy;
        tv_speed = binding.tvSpeed;
        tv_sensor = binding.tvSensor;
        tv_updates = binding.tvUpdates;
        tv_address = binding.tvAddress;
        sw_gps = binding.swGps;
        sw_locationupdates = binding.swLocationsupdates;

        StrictMode.ThreadPolicy tp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(tp);

        sw_gps.setOnClickListener(view -> {
            if (sw_gps.isChecked()) {
                // most accurate - use GPS
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                tv_sensor.setText("Using GPS sensors");
            } else {
                locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                tv_sensor.setText("Using Towers + WiFi");
            }
        });

        sw_locationupdates.setOnClickListener(view -> {
            if (sw_locationupdates.isChecked()) {
                // turn on location tracking
                startLocationUpdates();
            } else {
                // turn off location tracking
                stopLocationUpdates();
            }
        });

        /* ******************************** */

        //set properties of LocationRequest
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);   //how often location is checked
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //event triggered whenever the update interval is met
        locationCallBack = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //save the location
                current = locationResult.getLastLocation();
                updateUIValues(locationResult.getLastLocation());
            }
        };

        if (sw_gps.isChecked())
            tv_sensor.setText("Using GPS sensors");
        else
            tv_sensor.setText("Using Towers + WiFi");

        if (sw_locationupdates.isChecked())
            tv_updates.setText("Localization On");
        else
            tv_updates.setText("Localization Off");

        updateGPS();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sw_gps.isChecked())
            tv_sensor.setText("Using GPS sensors");
        else
            tv_sensor.setText("Using Towers + WiFi");

        if (sw_locationupdates.isChecked())
            tv_updates.setText("Localization On");
        else
            tv_updates.setText("Localization Off");
    }

    private void updateGPS() {
        // get permissions from the user to track GPS
        // get the current location from the fused client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // user provided permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                if (location == null)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                        Criteria criteria = new Criteria();
                        String bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true));
                        locationManager.requestLocationUpdates(bestProvider, 1000, 0, (LocationListener) this);
                    }
                // put the values of location into the UI
                updateUIValues(location);
            });
        } else {
            // permission not granted yet
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }

    }

    private void updateUIValues(Location location) {
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));

        if (location.hasAltitude())
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        else
            tv_altitude.setText("Not available");

        if (location.hasSpeed())
            tv_speed.setText(String.valueOf(location.getSpeed()));
        else
            tv_speed.setText("Not available");

        Geocoder geocoder = new Geocoder(getActivity());

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tv_address.setText(addresses.get(0).getAddressLine(0));
        } catch (Exception e) {
            tv_address.setText("Unable to get street address");
        }
    }



    private void stopLocationUpdates() {

        tv_updates.setText("Localization Off");
        tv_lat.setText(R.string.not_tracking);
        tv_lon.setText(R.string.not_tracking);
        tv_speed.setText(R.string.not_tracking);
        tv_speed.setText(R.string.not_tracking);
        tv_accuracy.setText(R.string.not_tracking);
        tv_altitude.setText(R.string.not_tracking);
        tv_sensor.setText(R.string.not_tracking);
        tv_address.setText(R.string.not_tracking);

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateGPS();
            } else {
                Toast.makeText(getActivity(), "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }

    private void startLocationUpdates() {

        tv_updates.setText("Localization On");
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}