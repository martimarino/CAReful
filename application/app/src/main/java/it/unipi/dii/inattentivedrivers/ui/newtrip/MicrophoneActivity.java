package it.unipi.dii.inattentivedrivers.ui.newtrip;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



import android.Manifest;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;


import android.content.pm.PackageManager;
import android.util.Log;

import it.unipi.dii.inattentivedrivers.R;


public class MicrophoneActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private MediaRecorder mRecorder = null;
    private Button button;
    private Button button2;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    double mThreshold = 80;
    int decibelCounter = 0;
    double dec[];
    int POLL_INTERVAL = 500;
    private final double referenceAmplitude = 2700.0;  //0.0001
    private final int AUDIO_RECORDING_DELAY = 500;

    private void makeToast(String text) {
        Toast.makeText(MicrophoneActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();

    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_microphone);

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile("/dev/null");
        try {
            mRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
                        makeToast("Noise detected");
                        Log.d("amplitude", "Noise detected");
                    }
                } else decibelCounter = 0;

                Log.d("amplitude", String.valueOf(decibel));
                TextView tv = findViewById(R.id.textActivityMicrophone);
                tv.setText("Decibel: " + String.format(Locale.US, "%.1f", decibel));
                mHandler.postDelayed(this, AUDIO_RECORDING_DELAY);
            }
        }, AUDIO_RECORDING_DELAY);
    }
}