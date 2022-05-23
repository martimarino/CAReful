package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import it.unipi.dii.inattentivedrivers.databinding.ActivityCameraBinding;
import it.unipi.dii.inattentivedrivers.sensors.CameraManager;

public class CameraActivity extends AppCompatActivity {

    public static ActivityCameraBinding binding;
    CameraManager cam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cam = new CameraManager(this, binding);

    }
}