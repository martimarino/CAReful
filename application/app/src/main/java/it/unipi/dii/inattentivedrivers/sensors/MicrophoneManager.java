package it.unipi.dii.inattentivedrivers.sensors;

import android.Manifest;
import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

import it.unipi.dii.inattentivedrivers.databinding.ActivityMicrophoneBinding;

public class MicrophoneManager {

    private MediaRecorder mRecorder = null;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    double mThreshold = 80;
    int decibelCounter = 0;
    double dec[];
    private final int AUDIO_RECORDING_DELAY = 500;

    public MicrophoneManager(Activity activity, ActivityMicrophoneBinding binding){

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile("/dev/null");
        try {
            mRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mRecorder.start();


        final Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                double amp = mRecorder.getMaxAmplitude();
                double decibel = (20*amp / 2700.0);
                if (decibel > mThreshold) {
                    Log.d("amplitude", "too much noise");
                    decibelCounter = decibelCounter + 1;
                    if (decibelCounter == 5) {
                        if(binding != null)
                            makeToast(activity, "Noise detected");
                        Log.d("amplitude", "Noise detected");
                    }
                } else decibelCounter = 0;

                Log.d("amplitude", String.valueOf(decibel));
                if(binding != null)
                    binding.textActivityMicrophone.setText("Decibel: " + String.format(Locale.US, "%.1f", decibel));
                mHandler.postDelayed(this, AUDIO_RECORDING_DELAY);
            }
        }, AUDIO_RECORDING_DELAY);

    }

    private void makeToast(Activity activity, String text) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

}
