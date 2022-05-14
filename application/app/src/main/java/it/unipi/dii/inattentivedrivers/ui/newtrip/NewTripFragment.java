package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import it.unipi.dii.inattentivedrivers.databinding.FragmentNewtripBinding;

public class NewTripFragment extends Fragment {

    private FragmentNewtripBinding binding;

    ImageView phone, camera, gps, gyroscope, microphone, accelerometer;
    Button start;

    public NewTripFragment() {

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNewtripBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        start = binding.start;
        phone = binding.phone;
        camera = binding.camera;
        gps = binding.gps;
        gyroscope = binding.gyroscope;
        microphone = binding.microphone;
        accelerometer = binding.accelerometer;

        start.setOnClickListener(view -> startTrip());
        phone.setOnClickListener(view -> tryPhoneRestrictions());
        camera.setOnClickListener(view -> tryCamera());
        gps.setOnClickListener(view -> tryGps());
        gyroscope.setOnClickListener(view -> tryGyroscope());
        microphone.setOnClickListener(view -> tryMicrophone());
        accelerometer.setOnClickListener(view -> tryAccelerometer());

        return root;
    }

    private void tryMicrophone() {

    }

    private void tryGyroscope() {

    }

    private void tryGps() {
        Intent intent = new Intent(getActivity(), MapsActivity.class);
        startActivity(intent);
    }

    private void tryCamera() {

    }

    private void tryPhoneRestrictions() {

    }

    private void startTrip() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void tryAccelerometer(){
        Intent intent = new Intent(getActivity(), StartTrip.class);
        startActivity(intent);
    }

}