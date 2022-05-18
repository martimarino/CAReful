package it.unipi.dii.inattentivedrivers.ui.newtrip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import it.unipi.dii.inattentivedrivers.databinding.ActivityCameraBinding;
import it.unipi.dii.inattentivedrivers.ui.newtrip.Draw;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;


import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CameraActivity extends AppCompatActivity{
    ActivityCameraBinding binding;
    FaceDetectorOptions realTimeOpts;
    FaceDetector faceDetector;
    float rotX;
    float rotY;
    float rotZ;
    int id;
    float rightEyeOpenProb;
    float leftEyeOpenProb;
    int drowsinessCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        drowsinessCounter = 0;

        realTimeOpts =
                new FaceDetectorOptions.Builder()
                        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .enableTracking()
                        .build();

        faceDetector = FaceDetection.getClient(realTimeOpts);

        Dexter.withContext(this)
                .withPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        setupCamera();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        makeToast("Permissions Required!");
                    }
                }).check();

    }

    private void makeToast(String text) {
        Toast.makeText(CameraActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private void setupCamera() {
        binding.cameraView.setLifecycleOwner(this);
        binding.cameraView.setFacing(Facing.FRONT);
        Log.d("fps", String.valueOf(binding.cameraView.getPreviewFrameRate()));
        binding.cameraView.setPreviewFrameRate(10f);
        Log.d("fps", String.valueOf(binding.cameraView.getPreviewFrameRate()));
        binding.cameraView.addFrameProcessor(new FrameProcessor() {
            @Override
            public void process(@NonNull Frame frame) {
                processImage(getInputImageFromFrame(frame));
            }

        });

    }

    private void processImage(InputImage inputImage) {
        Task<List<Face>> result =
                faceDetector.process(inputImage)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {
                                        processResults(faces);
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        makeToast("Some Error Occurred");
                                    }
                                });
    }

    private void processResults(List<Face> faces) {
        for (Face face : faces) {
            rotX = face.getHeadEulerAngleX();
            rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
            rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

            if (face.getTrackingId() != null) {
                id = face.getTrackingId();
                Log.d("id", String.valueOf(id));
            }

            if (face.getRightEyeOpenProbability() != null) {
                rightEyeOpenProb = face.getRightEyeOpenProbability();
                Log.d("righteyeopen", String.valueOf(rightEyeOpenProb));
            }

            if (face.getLeftEyeOpenProbability() != null) {
                leftEyeOpenProb = face.getLeftEyeOpenProbability();
                Log.d("lefteyeopen", String.valueOf(leftEyeOpenProb));
            }

            if(binding.parentLayout.getChildCount() > 1) {
                binding.parentLayout.removeViewAt(1);
            }
            Rect boundingBox = face.getBoundingBox();
            boundingBox.left = binding.parentLayout.getWidth() - boundingBox.left;
            boundingBox.right = binding.parentLayout.getWidth() - boundingBox.right;
            //Log.d("MainActivity", "processResults: " + i.getLabels().toString());
            String text = "Undefined";
            /*if(i.getLabels().size() != 0) {
                text = i.getLabels().get(0).getText();
            }*/

            text = String.valueOf(rotX) + " | " + String.valueOf(rotY) + " | " + String.valueOf(rotZ) + " | " + String.valueOf(rightEyeOpenProb);

            Draw element = new Draw(this, boundingBox, String.valueOf(rotX), String.valueOf(rotY), String.valueOf(rotZ), String.valueOf(rightEyeOpenProb), String.valueOf(leftEyeOpenProb));
            binding.parentLayout.addView(element);

            Log.d("euler x", String.valueOf(rotX));
            Log.d("euler y", String.valueOf(rotY));
            Log.d("euler z", String.valueOf(rotZ));
            Log.d("--------------", "----------------------------------------------");
            Log.d("--------------", "----------------------------------------------");

            if(rightEyeOpenProb<0.5){
                drowsinessCounter = drowsinessCounter + 1;
                if(drowsinessCounter == 20){
                    makeToast("Sveglia rincoglionito");
                }
            }
            else{
                drowsinessCounter = 0;
            }
        }

    }


    private void printValues () {
        Log.d("id", String.valueOf(id));
        Log.d("euler x", String.valueOf(rotX));
        Log.d("euler y", String.valueOf(rotY));
        Log.d("euler z", String.valueOf(rotZ));
        Log.d("righteyeopen", String.valueOf(rightEyeOpenProb));
        Log.d("lefteyeopen", String.valueOf(leftEyeOpenProb));
        Log.d("--------------", "----------------------------------------------");
    }

    private InputImage getInputImageFromFrame(Frame frame) {
        byte[] data = frame.getData();
        return InputImage.fromByteArray(data, frame.getSize().getWidth(), frame.getSize().getHeight(), frame.getRotation(), InputImage.IMAGE_FORMAT_NV21);
    }
}
