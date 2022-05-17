package it.unipi.dii.inattentivedrivers.ui.newtrip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



import android.Manifest;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import it.unipi.dii.inattentivedrivers.MainActivity;
import it.unipi.dii.inattentivedrivers.R;


public class Microphone extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private MediaRecorder recorder = null;
    private MediaPlayer reproducer = null;
    private static String filename = null;
    private boolean listening = false;
    private boolean recording = false;
    private Button button;
    private Button button2;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private Thread recordingThread = null;

    private AudioRecord rec = null;

    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format
    private final double referenceAmplitude = 2700.0;  //0.0001
    private final int AUDIO_RECORDING_DELAY = 1000;

    private void makeToast(String text) {
        Toast.makeText(Microphone.this, text, Toast.LENGTH_SHORT).show();
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



/*    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microphone);
        button = findViewById(R.id.record);

        Dexter.withContext(this)
                .withPermissions(new String[]{Manifest.permission.RECORD_AUDIO})
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        record();
                        startRecording();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        makeToast("Permissions Required!");
                    }
                }).check();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                record();
            }
        });
        AudioManager manager = (AudioManager) getSystemService(AUDIO_SERVICE);

        filename = Environment.getExternalStorageDirectory().getAbsolutePath();
        filename += "/registrazione.3gp";
    }*/


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_microphone);
        // Record to the external cache directory for visibility
        filename = getExternalCacheDir().getAbsolutePath();
        filename += "/audiorecordtest.3gp";

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);

        button = findViewById(R.id.record);
        button.setOnClickListener(view -> record());
        button2 = findViewById(R.id.listen);
        button2.setOnClickListener(view -> listen());

/*
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final double amplitude = getAmplitude();
                TextView tv = findViewById(R.id.textActivityMicrophone);
                tv.setText(String.format(Locale.US, "%.1f", amplitude));
                handler.postDelayed(this, AUDIO_RECORDING_DELAY);
            }
        }, AUDIO_RECORDING_DELAY);
*/

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (recorder != null){
            recorder.release();
            recorder = null;
        }

        //reproducer can be removed
        if (reproducer != null) {
            reproducer.release();
            reproducer = null;
        }
/*        if (rec != null) {
            rec.release();
            rec = null;
        }

        if (reproducer != null) {
            reproducer.release();
            reproducer = null;
        }*/
    }

    public void record() {

        button = findViewById(R.id.record);
        if (recording) {
            stopRecording();
            button.setText("Record");
        } else {
            startRecording();
            button.setText("Stop Recording");
        }
        recording = !recording;
    }

    //listen can be removed
    public void listen() {
        button2 = findViewById(R.id.listen);
        if (listening) {
            stopListening();
            button2.setText("Listen");
        } else {
            reproduce();
            button2.setText("Stop");
        }
        listening = !listening;
    }

    private void reproduce() {
        reproducer = new MediaPlayer();
        try {
            reproducer.setDataSource(filename);
            reproducer.prepare();
            reproducer.start();
        } catch (IOException e) {
            System.out.println("Error");
        }
    }

    private void stopListening() {
        reproducer.release();
        reproducer = null;
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(filename);
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
                final double amplitude = getAmplitude();
/*                if (amplitude > 60.0) {
                    fai qualcosa;
                }*/
                TextView tv = findViewById(R.id.textActivityMicrophone);
                tv.setText(String.format(Locale.US, "%.1f", amplitude));
                handler.postDelayed(this, AUDIO_RECORDING_DELAY);
            }
        }, AUDIO_RECORDING_DELAY);

/*
        }
        rec = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);
        rec.startRecording();
        recording = true;
        // prova buffer
        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
        //*/
    }

        //double amplitude = recorder.getMaxAmplitude();
/*        double decibel = 20 * Math.log10(amplitude / 2700.0);
        TextView tv = findViewById(R.id.textActivityMicrophone);
        tv.setText("Decibel: " + amplitude);*/


    private void stopRecording(){
        //rec.stop();
        recorder.stop();
        //
        //recording = false;
        //
        double amplitude = recorder.getMaxAmplitude();
        double decibel = 20 * Math.log10(amplitude / 2700.0);
        TextView tv = findViewById(R.id.textActivityMicrophone);
        tv.setText("Decibel: " + amplitude);
        recorder.release();
        recorder = null;
    }


/*    private void writeAudioDataToFile() {
        // write the output audio in byte
        String filepath = filename;
        short sData[] = new short[BufferElements2Rec];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filepath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (recording) {
            //gets the voice output from microphone to byte format
            rec.read(sData, 0, BufferElements2Rec);
            System.out.println("Short writing to file" + sData.toString());
            try {
                // writes the data to file from buffer
                //stores the voice buffer
                byte bData[] = short2byte(sData);
                os.write(bData, 0, BufferElements2Rec * BytesPerElement);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i=0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
        }
        return bytes;
    }*/

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

}