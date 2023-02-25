package it.unipi.dii.careful.sensors;

import android.Manifest;
import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;

import java.util.List;

import it.unipi.dii.careful.databinding.ActivityCameraBinding;
import it.unipi.dii.careful.databinding.StartTripBinding;
import it.unipi.dii.careful.ui.newtrip.CameraActivity;
import it.unipi.dii.careful.ui.newtrip.StartTrip;

public class CameraManager {

    public static final double DROWSINESS_PERCENTAGE_THRESHOLD = 0.5;
    public static final double DROWSINESS_SECONDS = 2;
    public static final double TURNED_HEAD_ANGLE_THRESHOLD = 40.0;
    public static final double TURNED_HEAD_SECONDS= 4;

    boolean cameraVisible;

    CameraActivity cameraActivity;
    ActivityCameraBinding activityCameraBinding;

    StartTrip startTrip;
    StartTripBinding startTripBinding;

    /* ************************************* */

    FaceDetectorOptions realTimeOpts;
    FaceDetector faceDetector;
    float rotX;
    float rotY;
    float rotZ;
    int id;
    float rightEyeOpenProb;
    float leftEyeOpenProb;
    int drowsinessCounterDetection;
    int turnedHeadCounterDetection;
    int drowsinessCounter;
    int turnedHeadCounter;

    public CameraManager(CameraActivity cameraActivity, ActivityCameraBinding activityCameraBinding){
        drowsinessCounterDetection = 0;
        turnedHeadCounterDetection = 0;
        drowsinessCounter = 0;
        turnedHeadCounter = 0;
        id = -1;
        cameraVisible = true;
        this.cameraActivity = cameraActivity;
        this.activityCameraBinding = activityCameraBinding;
        realTimeOpts =
                new FaceDetectorOptions.Builder()
                        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .enableTracking()
                        .build();

        faceDetector = FaceDetection.getClient(realTimeOpts);
        initializeCamera(cameraActivity);
    }

    public CameraManager(StartTrip startTrip, StartTripBinding startTripBinding){
        drowsinessCounterDetection = 0;
        turnedHeadCounterDetection = 0;
        drowsinessCounter = 0;
        turnedHeadCounter = 0;
        id = -1;
        cameraVisible = false;
        this.startTrip = startTrip;
        this.startTripBinding = startTripBinding;
        realTimeOpts =
                new FaceDetectorOptions.Builder()
                        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .enableTracking()
                        .build();

        faceDetector = FaceDetection.getClient(realTimeOpts);
        initializeCamera(startTrip);
    }

    public void initializeCamera(Activity activity){
        Dexter.withContext(activity)
                .withPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if(cameraVisible)
                            setupTryCamera(activity);
                        else
                            setupTripCamera(activity);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        makeToast("Permissions Required!!", activity);
                    }
                }).check();
    }

    public static void makeToast(String text, Activity activity) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

    private void setupTryCamera(Activity activity) {
        CameraActivity.binding.cameraView.setLifecycleOwner(cameraActivity);
        CameraActivity.binding.cameraView.setFacing(Facing.FRONT);
        Log.d("fps", String.valueOf(CameraActivity.binding.cameraView.getPreviewFrameRate()));
        CameraActivity.binding.cameraView.setPreviewFrameRate(10f);
        CameraActivity.binding.cameraView.setPreviewFrameRateExact(true);
        Log.d("fps", String.valueOf(CameraActivity.binding.cameraView.getPreviewFrameRate()));
        CameraActivity.binding.cameraView.addFrameProcessor(new FrameProcessor() {
            @Override
            public void process(@NonNull Frame frame) {
                processImage(getInputImageFromFrame(frame), activity);
            }
        });
    }

    private void setupTripCamera(Activity activity) {
        StartTrip.binding.cameraView.setLifecycleOwner(startTrip);
        StartTrip.binding.cameraView.setFacing(Facing.FRONT);
        StartTrip.binding.cameraView.setVisibility(View.INVISIBLE);
        Log.d("fps", String.valueOf(StartTrip.binding.cameraView.getPreviewFrameRate()));
        StartTrip.binding.cameraView.setPreviewFrameRate(10f);
        StartTrip.binding.cameraView.setPreviewFrameRateExact(true);
        Log.d("fps", String.valueOf(StartTrip.binding.cameraView.getPreviewFrameRate()));
        StartTrip.binding.cameraView.addFrameProcessor(new FrameProcessor() {
            @Override
            public void process(@NonNull Frame frame) {
                processImage(getInputImageFromFrame(frame), activity);
            }
        });
    }

    private void processImage(InputImage inputImage, Activity activity) {
        Task<List<Face>> result =
                faceDetector.process(inputImage)
                        .addOnSuccessListener(
                                faces -> processResults(faces, activity))
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        makeToast("Some Error Occurred", activity);
                                    }
                                });
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

    private void processResults(List<Face> faces, Activity activity) {
        for (Face face : faces) {
            if (id == -1){
                id = face.getTrackingId();
                Log.d("id", String.valueOf(id));

            }

            rotX = face.getHeadEulerAngleX();
            rotY = face.getHeadEulerAngleY();
            rotZ = face.getHeadEulerAngleZ();


            if (face.getRightEyeOpenProbability() != null) {
                rightEyeOpenProb = face.getRightEyeOpenProbability();
                //Log.d("righteyeopen", String.valueOf(rightEyeOpenProb));
            }

            if (face.getLeftEyeOpenProbability() != null) {
                leftEyeOpenProb = face.getLeftEyeOpenProbability();
                //Log.d("lefteyeopen", String.valueOf(leftEyeOpenProb));
            }

            if(cameraVisible) {
                if (CameraActivity.binding.parentLayout.getChildCount() > 1) {
                    CameraActivity.binding.parentLayout.removeViewAt(1);
                }
            } else {
                if (StartTrip.binding.parentLayout.getChildCount() > 1) {
                    StartTrip.binding.parentLayout.removeViewAt(1);
                }
            }

            Rect boundingBox = face.getBoundingBox();
            if(cameraVisible) {
                boundingBox.left = CameraActivity.binding.parentLayout.getWidth() - boundingBox.left;
                boundingBox.right = CameraActivity.binding.parentLayout.getWidth() - boundingBox.right;
            } else {
                boundingBox.left = StartTrip.binding.parentLayout.getWidth() - boundingBox.left;
                boundingBox.right = StartTrip.binding.parentLayout.getWidth() - boundingBox.right;
            }
            if(cameraVisible) {
                Draw element = new Draw(activity, boundingBox, String.valueOf(rotX), String.valueOf(rotY), String.valueOf(rotZ), String.valueOf(rightEyeOpenProb), String.valueOf(leftEyeOpenProb));
                CameraActivity.binding.parentLayout.addView(element);
            }
            if(rightEyeOpenProb< DROWSINESS_PERCENTAGE_THRESHOLD && leftEyeOpenProb< DROWSINESS_PERCENTAGE_THRESHOLD){
                drowsinessCounterDetection = drowsinessCounterDetection + 1;
                if(drowsinessCounterDetection == DROWSINESS_SECONDS*10){
                    drowsinessCounter = drowsinessCounter + 1;
                    if(activity instanceof CameraActivity) {
                        makeToast("Drowsiness detected", activity);
                    }
                    else{
                        Log.d("drowsiness", "detected");
                    }
                }
            }
            else{
                drowsinessCounterDetection = 0;
            }

            if(rotY<-TURNED_HEAD_ANGLE_THRESHOLD || rotY> TURNED_HEAD_ANGLE_THRESHOLD){
                turnedHeadCounterDetection = turnedHeadCounterDetection + 1;
                if(turnedHeadCounterDetection == TURNED_HEAD_SECONDS*10){
                    turnedHeadCounter = turnedHeadCounter + 1;
                    if(activity instanceof CameraActivity) {
                        makeToast("Head turned", activity);
                    }
                    else{
                        Log.d("head turned", "si");
                    }
                }
            }
            else{
                turnedHeadCounterDetection = 0;
            }
            break;
        }
    }

    public int getDrowsinessCounter() {
        return drowsinessCounter;
    }

    public int getTurnedHeadCounter() {
        return turnedHeadCounter;
    }

    public void setDrowsinessCounter(int drowsinessCounter) {
        this.drowsinessCounter = drowsinessCounter;
    }

    public void setTurnedHeadCounter(int turnedHeadCounter) {
        this.turnedHeadCounter = turnedHeadCounter;
    }
}