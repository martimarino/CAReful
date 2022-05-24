package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import it.unipi.dii.inattentivedrivers.databinding.FragmentNewtripBinding;

public class NewTripFragment extends Fragment {

    private FragmentNewtripBinding binding;

    ImageView camera, gps, motion, microphone;
    ImageView camera_off, gps_off, motion_off, microphone_off;
    Button start;
    Switch developMode;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNewtripBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        start = binding.start;
        developMode = binding.developMode;

        camera = binding.camera;
        gps = binding.localization;
        motion = binding.motion;
        microphone = binding.microphone;

        camera_off = binding.cameraOff;
        gps_off = binding.localizationOff;
        motion_off = binding.motionOff;
        microphone_off = binding.microphoneOff;

        // Listeners to change image when sensor is selected
        setVisibility();

        developMode.setOnClickListener(v ->{
            if (developMode.isChecked()) {
                camera.setOnClickListener(view -> tryCamera());
                gps.setOnClickListener(view -> tryGps());
                motion.setOnClickListener(view -> tryMotion());
                microphone.setOnClickListener(view -> tryMicrophone());
            }
            else{
                setVisibility();
            }
        });
        return root;
    }

    private void setVisibility(){

        start.setOnClickListener(view -> startTrip());

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

        motion.setOnClickListener(v -> {
            motion.setVisibility(View.INVISIBLE);
            motion_off.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Gyroscope sensor off", Toast.LENGTH_SHORT).show();
        });

        motion_off.setOnClickListener(v -> {
            motion_off.setVisibility(View.INVISIBLE);
            motion.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Gyroscope sensor on", Toast.LENGTH_SHORT).show();
        });

        microphone.setOnClickListener(v -> {
            microphone.setVisibility(View.INVISIBLE);
            microphone_off.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Microphone sensor off", Toast.LENGTH_SHORT).show();
        });

        microphone_off.setOnClickListener(v -> {
            microphone_off.setVisibility(View.INVISIBLE);
            microphone.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Microphone sensor on", Toast.LENGTH_SHORT).show();
        });
    }

    private void tryMicrophone() {
        Intent intent = new Intent(getActivity(), MicrophoneActivity.class);
        startActivity(intent);
    }

    public void tryMotion(){
        Intent intent = new Intent(getActivity(), MotionActivity.class);
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