package it.unipi.dii.inattentivedrivers.sensors;

import android.Manifest;
import android.app.Activity;
import android.media.AudioRecord;
import android.util.Log;

import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import it.unipi.dii.inattentivedrivers.databinding.ActivityMicrophoneBinding;
import it.unipi.dii.inattentivedrivers.ui.newtrip.MicrophoneActivity;
import it.unipi.dii.inattentivedrivers.ui.newtrip.StartTrip;

public class MicrophoneManager {

    MicrophoneActivity microphoneActivity;
    ActivityMicrophoneBinding activityMicrophoneBinding;

    StartTrip startTrip;

    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    int noiseDetections = 0;
    private String modelPath = "lite-model_yamnet_classification_tflite_1.tflite";
    private float probabilityThreshold = 0.3f;

    public static final int REQUEST_RECORD_AUDIO = 200;
    private AudioClassifier classifier;
    private TensorAudio tensor;
    private AudioRecord record;

    private static final String recPermission = Manifest.permission.RECORD_AUDIO;
    public String[] sendRecPermission = {recPermission};

    private Timer t;
    private TimerTask task;

    public MicrophoneManager(MicrophoneActivity microphoneActivity, ActivityMicrophoneBinding activityMicrophoneBinding){
        this.microphoneActivity = microphoneActivity;
        this.activityMicrophoneBinding = activityMicrophoneBinding;
    }

    public MicrophoneManager(StartTrip startTrip){
        this.startTrip = startTrip;
    }

    public void initializeMicrophone(Activity activity) {
        try {
            classifier = AudioClassifier.createFromFile(activity, modelPath);
            tensor = classifier.createInputTensorAudio();
            record = classifier.createAudioRecord();
            record.startRecording();

            task = new TimerTask() {
                public void run() {
                    tensor.load(record);
                    List<Classifications> output = classifier.classify(tensor);
                    Category category = output.get(0).getCategories().get(0);
                    String outputStr;
                    outputStr = "Class: " + category.getLabel() + ", Score: " + category.getScore() +"\n";
                    if(activity instanceof MicrophoneActivity) {
                        activityMicrophoneBinding.textActivityMicrophone.setText(outputStr);
                    }
                    else{
                        Log.d("Microphone detected: ", category.getLabel());
                    }
                }
            };
            t = new Timer();
            t.schedule(task, 1, 500);
        } catch (Exception e) {
            e.printStackTrace();
//            activityMicrophoneBinding.textActivityMicrophone.setText(e.getMessage());
        }
    }

    public int getNoiseDetections() {
        return noiseDetections;
    }

    public void setNoiseDetections(int noiseDetections) {
        this.noiseDetections = noiseDetections;
    }
}