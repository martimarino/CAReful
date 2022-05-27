package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import it.unipi.dii.inattentivedrivers.databinding.ActivityMotionBinding;
import it.unipi.dii.inattentivedrivers.sensors.MotionManager;

public class MotionActivity extends AppCompatActivity {

    MotionManager mot;
    ActivityMotionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = ActivityMotionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mot = new MotionManager(this, binding, getApplicationContext());
    }


    @Override
    protected void onResume(){
        super.onResume();
        mot.registerListeners(true, false);

    }

    @Override
    protected void onStop(){
        super.onStop();
        mot.unregisterListeners(true, false);
    }
}