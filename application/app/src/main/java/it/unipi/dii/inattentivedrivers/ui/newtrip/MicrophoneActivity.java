package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import it.unipi.dii.inattentivedrivers.databinding.ActivityMicrophoneBinding;
import it.unipi.dii.inattentivedrivers.sensors.MicrophoneManager;

public class MicrophoneActivity extends AppCompatActivity {

    ActivityMicrophoneBinding binding;
    MicrophoneManager mic;

    // TODO 2.1: defines the model to be used
    private String modelPath = "lite-model_yamnet_classification_tflite_1.tflite";

    // TODO 2.2: defining the minimum threshold
    private float probabilityThreshold = 0.3f;

    private int REQUEST_RECORD_AUDIO = 200;

    private static final String recPermission = Manifest.permission.RECORD_AUDIO;
    private String[] sendRecPermission = {recPermission};


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMicrophoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mic = new MicrophoneManager(this, binding);
        ActivityCompat.requestPermissions(this, sendRecPermission, REQUEST_RECORD_AUDIO);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                mic.initializeMicrophone(this);
            else
                Toast.makeText(this , "Permission to use the microphone denied...", Toast.LENGTH_SHORT).show();
        }
    }
}