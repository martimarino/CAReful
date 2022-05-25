package it.unipi.dii.inattentivedrivers.sensors;

import android.Manifest;
import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import it.unipi.dii.inattentivedrivers.databinding.ActivityMicrophoneBinding;
import it.unipi.dii.inattentivedrivers.ui.newtrip.MicrophoneActivity;
import it.unipi.dii.inattentivedrivers.ui.newtrip.StartTrip;

public class MicrophoneManager {

    MicrophoneActivity microphoneActivity;
    ActivityMicrophoneBinding activityMicrophoneBinding;

    StartTrip startTrip;

    boolean decibelVisible;
    public MediaRecorder mRecorder;

    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private final double referenceAmplitude = 2700.0;
    private final int AUDIO_RECORDING_DELAY = 500;
    double mThreshold = 40;
    int decibelCounter = 0;
    int noiseDetections = 0;

    public MicrophoneManager(MicrophoneActivity microphoneActivity, ActivityMicrophoneBinding activityMicrophoneBinding){
        this.microphoneActivity = microphoneActivity;
        this.activityMicrophoneBinding = activityMicrophoneBinding;
        mRecorder = null;
        decibelVisible = true;
        initializeMicrophone(microphoneActivity);
    }

    public MicrophoneManager(StartTrip startTrip){
        this.startTrip = startTrip;
        decibelVisible = false;
        mRecorder = null;
        initializeMicrophone(startTrip);
    }

    private void makeToast(String text, Activity activity) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

    public void initializeMicrophone(Activity activity) {
        Dexter.withContext(activity)
                .withPermissions(new String[]{Manifest.permission.RECORD_AUDIO})
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        startRecording(activity);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        makeToast("Permissions Required!", activity);
                    }
                }).check();
    }

    private void startRecording(Activity activity) {

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
                double decibel = (20*amp / referenceAmplitude);
                if (decibel > mThreshold) {
                    Log.d("amplitude", "too much noise");
                    decibelCounter = decibelCounter + 1;
                    if (decibelCounter == 5) {
                        noiseDetections += 1;
                        Log.d("Noise detection", String.valueOf(noiseDetections));
                        if (decibelVisible) {
                            makeToast("Noise detected", activity);
                        }
                    }
                } else decibelCounter = 0;
                if (decibelVisible) {
                    activityMicrophoneBinding.textActivityMicrophone.setText("Decibel: " + String.format(Locale.US, "%.1f", decibel));
                }
                mHandler.postDelayed(this, AUDIO_RECORDING_DELAY);
            }
        }, AUDIO_RECORDING_DELAY);
    }
}