package it.unipi.dii.inattentivedrivers.sensors;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class DecibelDetector {

    private static final String TAG = "AudioRecord";
    static final int SAMPLE_RATE_IN_HZ = 8000;
    static final int BUFFER_SIZE = android.media.AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
            AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
    android.media.AudioRecord mAudioRecord;
    boolean isGetVoiceRun;
    Object mLock;
    public double decibelMeasure;

    public DecibelDetector() {
        mLock = new Object();
    }

    public void getNoiseLevel(Context context) {
        if (isGetVoiceRun) {
            Log.e(TAG, "Still recording it");
            return;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mAudioRecord = new android.media.AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
        if (mAudioRecord == null) {
            Log.e("sound", "mAudioRecord initialization failed");
        }
        isGetVoiceRun = true;

        new Thread(() -> {
            mAudioRecord.startRecording();
            short[] buffer = new short[BUFFER_SIZE];
            while (isGetVoiceRun) {
                //r is the actual length of data read, generally speaking, r will be less than buffersize
                int r = mAudioRecord.read(buffer, 0, BUFFER_SIZE);
                long v = 0;
                // Take out the contents of buffer. Do the sum of squares
                for (int i = 0; i <buffer.length; i++) {
                    v += buffer[i] * buffer[i];
                }
                // Divide the sum of squares by the total length of the data to get the volume.
                double mean = v / (double) r;
                double volume = 10 * Math.log10(mean);
                Log.d(TAG, "Decibel value:" + volume);
                decibelMeasure = volume;
                // About ten times a second
                synchronized (mLock) {
                    try {
                        mLock.wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }).start();
    }
}