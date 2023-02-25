package it.unipi.dii.careful.ui.newtrip;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import it.unipi.dii.careful.databinding.ActivityMicrophoneBinding;
import it.unipi.dii.careful.sensors.MicrophoneManager;

public class MicrophoneActivity extends AppCompatActivity {

    ActivityMicrophoneBinding binding;
    MicrophoneManager mic;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMicrophoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mic = new MicrophoneManager(this, binding);
        ActivityCompat.requestPermissions(this, mic.sendRecPermission, MicrophoneManager.REQUEST_RECORD_AUDIO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MicrophoneManager.REQUEST_RECORD_AUDIO) {
            if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mic.initializeMicrophone(this);
            }
            else
                Toast.makeText(this , "Permission to use the microphone denied...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}