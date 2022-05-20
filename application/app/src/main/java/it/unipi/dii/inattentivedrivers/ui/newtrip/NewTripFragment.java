package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import it.unipi.dii.inattentivedrivers.databinding.FragmentNewtripBinding;

public class NewTripFragment extends Fragment {

    private FragmentNewtripBinding binding;

    ImageView phone, camera, gps, gyroscope, microphone, accelerometer;
    ImageView phone_off, camera_off, gps_off, gyroscope_off, microphone_off, accelerometer_off;
    Button start;
    Switch developMode;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNewtripBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        start = binding.start;
        developMode = binding.developMode;

        phone = binding.phone;
        camera = binding.camera;
        gps = binding.localization;
        gyroscope = binding.gyroscope;
        microphone = binding.microphone;
        accelerometer = binding.accelerometer;

        phone_off = binding.phoneOff;
        camera_off = binding.cameraOff;
        gps_off = binding.localizationOff;
        gyroscope_off = binding.gyroscopeOff;
        microphone_off = binding.microphoneOff;
        accelerometer_off = binding.accelerometerOff;

        setListeners();
        start.setOnClickListener(view -> startTrip());

        developMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {  // Try every sensor
                phone.setOnClickListener(view -> trySmartphoneRestrictions());
                camera.setOnClickListener(view -> tryCamera());
                gps.setOnClickListener(view -> tryGps());
                gyroscope.setOnClickListener(view -> tryGyroscope());
                microphone.setOnClickListener(view -> tryMicrophone());
                accelerometer.setOnClickListener(view -> tryAccelerometer());
            } else {    // Listeners to change image when sensor is selected
                setListeners();
            }   //end else
        });   // end listener

        return root;
    }

    private void setListeners(){
        phone.setOnClickListener(v -> {
            phone.setVisibility(View.INVISIBLE);
            phone_off.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Control on exiting app off", Toast.LENGTH_SHORT).show();

        });

        phone_off.setOnClickListener(v -> {
            phone_off.setVisibility(View.INVISIBLE);
            phone.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Control on exiting app on", Toast.LENGTH_SHORT).show();
        });

        camera.setOnClickListener(v -> {
            camera.setVisibility(View.INVISIBLE);
            camera_off.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Camera sensor off", Toast.LENGTH_SHORT).show();
        });

        camera_off.setOnClickListener(v -> {
            camera_off.setVisibility(View.INVISIBLE);
            camera.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Camera sensor on", Toast.LENGTH_SHORT).show();
        });

        gps.setOnClickListener(v -> {
            gps.setVisibility(View.INVISIBLE);
            gps_off.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Gps sensor off", Toast.LENGTH_SHORT).show();
        });

        gps_off.setOnClickListener(v -> {
            gps_off.setVisibility(View.INVISIBLE);
            gps.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Gps sensor on", Toast.LENGTH_SHORT).show();
        });

        gyroscope.setOnClickListener(v -> {
            gyroscope.setVisibility(View.INVISIBLE);
            gyroscope_off.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Gyroscope sensor off", Toast.LENGTH_SHORT).show();
        });

        gyroscope_off.setOnClickListener(v -> {
            gyroscope_off.setVisibility(View.INVISIBLE);
            gyroscope.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Gyroscope sensor on", Toast.LENGTH_SHORT).show();
        });

        microphone.setOnClickListener(v -> {
            microphone.setVisibility(View.INVISIBLE);
            microphone_off.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Microphone sensor off", Toast.LENGTH_SHORT).show();
        });

        microphone_off.setOnClickListener(v -> {
            microphone.setVisibility(View.INVISIBLE);
            microphone.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Microphone sensor on", Toast.LENGTH_SHORT).show();
        });

        accelerometer.setOnClickListener(v -> {
            accelerometer.setVisibility(View.INVISIBLE);
            accelerometer_off.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Accelerometer sensor off", Toast.LENGTH_SHORT).show();
        });

        accelerometer_off.setOnClickListener(v -> {
            accelerometer_off.setVisibility(View.INVISIBLE);
            accelerometer.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Accelerometer sensor on", Toast.LENGTH_SHORT).show();
        });
    }

    private void tryMicrophone() {
        Intent intent = new Intent(getActivity(), MicrophoneActivity.class);
        startActivity(intent);
    }

    private void tryGyroscope() {
        Intent intent = new Intent(getActivity(), AccelerometerActivity.class);
        startActivity(intent);
    }

    public void tryAccelerometer(){
        Intent intent = new Intent(getActivity(), AccelerometerActivity.class);
        startActivity(intent);
    }

    private void tryGps() {
        Intent intent = new Intent(getActivity(), MapsActivity.class);
        startActivity(intent);
    }

    private void tryCamera() {
        Intent intent = new Intent(getActivity(), CameraActivity.class);
        startActivity(intent);
    }

    private void trySmartphoneRestrictions() {
        Intent intent = new Intent(getActivity(), SmartphoneActivity.class);
        startActivity(intent);
    }

    private void startTrip() {
        Intent intent = new Intent(getActivity(), StartTrip.class);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}