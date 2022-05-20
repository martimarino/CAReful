package it.unipi.dii.inattentivedrivers.sensors;

import android.media.MediaRecorder;
import android.os.Handler;

import java.io.IOException;

public class MicrophoneManager {

    public MediaRecorder recorder;

    private final double referenceAmplitude = 2700.0;  //0.0001
    private final int AUDIO_RECORDING_DELAY = 1000;

    public MicrophoneManager(){
        recorder = null;
        startRecording();
    }

    private double getAmplitude(){
        if(recorder != null){
            final double maxAmplitude = recorder.getMaxAmplitude();
            final double amplitude = 20 * Math.log10(maxAmplitude / referenceAmplitude);

            return amplitude;
        }
        else{
            return 0.0;
        }
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
        } catch (IOException e){
            System.out.println("Error");
        }
        recorder.start();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getAmplitude();
                handler.postDelayed(this, AUDIO_RECORDING_DELAY);
            }
        }, AUDIO_RECORDING_DELAY);

    }

    private void stopRecording(){
        recorder.stop();
        double amplitude = recorder.getMaxAmplitude();
        double decibel = 20 * Math.log10(amplitude / 2700.0);
        recorder.release();
        recorder = null;
    }

}
