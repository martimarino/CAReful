package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.content.Intent;
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
import it.unipi.dii.inattentivedrivers.ui.profile.Session;

public class NewTripFragment extends Fragment {

    private FragmentNewtripBinding binding;

    ImageView camera, compass, motion, microphone;
    ImageView camera_off, compass_off, motion_off, microphone_off;
    Button start;
    Switch developMode;

    public static Session session;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNewtripBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (session == null) {      /* not logged user */
            session = new Session();
        }

        start = binding.start;
        developMode = binding.developMode;

        camera = binding.camera;
        compass = binding.road;
        motion = binding.motion;
        microphone = binding.microphone;

        camera_off = binding.cameraOff;
        compass_off = binding.roadOff;
        motion_off = binding.motionOff;
        microphone_off = binding.microphoneOff;

        // Listeners to change image when sensor is selected
        setVisibility();

        developMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                camera.setOnClickListener(view -> tryCamera());
                compass.setOnClickListener(view -> tryMag());
                motion.setOnClickListener(view -> tryMotion());
                microphone.setOnClickListener(view -> tryMicrophone());
                start.setClickable(false);
            } else {
                setVisibility();
            }
        });

        return root;
    }

    private void setVisibility(){

        start.setClickable(true);
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

        compass.setOnClickListener(v -> {
            compass.setVisibility(View.INVISIBLE);
            compass_off.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Magnetometer sensor off", Toast.LENGTH_SHORT).show();
        });

        compass_off.setOnClickListener(v -> {
            compass_off.setVisibility(View.INVISIBLE);
            compass.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Magnetometer sensor on", Toast.LENGTH_SHORT).show();
        });

        motion.setOnClickListener(v -> {
            motion.setVisibility(View.INVISIBLE);
            motion_off.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Motion sensors off", Toast.LENGTH_SHORT).show();
        });

        motion_off.setOnClickListener(v -> {
            motion_off.setVisibility(View.INVISIBLE);
            motion.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Motion sensors on", Toast.LENGTH_SHORT).show();
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

    private void tryMag() {
        Intent intent = new Intent(getActivity(), MapsActivity.class);
        startActivity(intent);
    }

    private void tryCamera() {
        Intent intent = new Intent(getActivity(), CameraActivity.class);
        startActivity(intent);
    }

    private void startTrip() {
        Intent intent = new Intent(getActivity(), StartTrip.class);
        intent.putExtra("mic", binding.microphone.getVisibility() == View.VISIBLE);
        intent.putExtra("mag", binding.road.getVisibility() == View.VISIBLE);
        intent.putExtra("cam", binding.camera.getVisibility() == View.VISIBLE);
        intent.putExtra("mot", binding.motion.getVisibility() == View.VISIBLE);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}