package it.unipi.dii.careful.ui.newtrip;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import it.unipi.dii.careful.databinding.ActivityCameraBinding;
import it.unipi.dii.careful.sensors.CameraManager;

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