package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import it.unipi.dii.inattentivedrivers.R;
import it.unipi.dii.inattentivedrivers.databinding.ActivityCameraBinding;
import it.unipi.dii.inattentivedrivers.databinding.ActivityMicrophoneBinding;
import it.unipi.dii.inattentivedrivers.sensors.MicrophoneManager;

public class MicrophoneActivity extends AppCompatActivity {

    MicrophoneManager mic;
    public ActivityMicrophoneBinding binding;

    @Override
    public void onCreate(Bundle icicle) {

        super.onCreate(icicle);
        binding = ActivityMicrophoneBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_microphone);

        mic = new MicrophoneManager(this, binding);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission Granted
                //Do your work here
//Perform operations here only which requires permission
            }
        }
    }

}