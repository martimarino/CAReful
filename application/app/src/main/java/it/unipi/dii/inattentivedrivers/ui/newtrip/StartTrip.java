package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.ArrayList;
import java.util.List;

import it.unipi.dii.inattentivedrivers.R;
import it.unipi.dii.inattentivedrivers.databinding.StartTripBinding;

public class StartTrip extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    /* Map variables */
    private GoogleMap mMap;
    private StartTripBinding binding;
    public static Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    /* General variables */
    private boolean foregroundActivity;
    public int resumeTimes = -1;        //avoid first activity start call

    /* Camera variables */
    FaceDetectorOptions realTimeOpts;
    FaceDetector faceDetector;
    float rotX;
    float rotY;
    float rotZ;
    int id;
    float rightEyeOpenProb;
    float leftEyeOpenProb;
    int drowsinessCounter;

    /* Accelerometer variables */
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private SensorEventListener accelerometerEventListener;
    private ArrayList<Float> array;
    private int fallDetected;
    private int countFall;

    public StartTrip() {
        array = new ArrayList<>();
        fallDetected = 0;
        countFall = 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = StartTripBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();

        initializeAccelerometer();
        initializeCamera();

    }

    private void initializeCamera() {
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
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        setupCamera();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        Toast.makeText(StartTrip.this, "Permissions required!", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("******************ON MAP READY*******************");
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        //mMap.addMarker(new MarkerOptions().position(current).title("Current location"));

        // zoomLevel is set at 10 for a City-level view
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(current)
                .zoom(18)
                .bearing(currentLocation.getBearing())
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, zoomLevel));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        mMap.setMyLocationEnabled(true);

    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION
                    }, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {

            if (location != null) {
                currentLocation = location;

                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                assert supportMapFragment != null;
                supportMapFragment.getMapAsync(StartTrip.this);
            }
        });
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                System.out.println("*********** onLocationResult **********");
                //Toast.makeText(getApplicationContext()," location result is  " + locationResult, Toast.LENGTH_LONG).show();

                if (locationResult == null) {
                    //Toast.makeText(getApplicationContext(),"current location is null ", Toast.LENGTH_LONG).show();
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //Toast.makeText(getApplicationContext(),"current location is " + location.getLongitude(), Toast.LENGTH_LONG).show();
                        Log.d("currentLocation: ", String.valueOf(location.getLatitude()));

                        //TODO: UI updates.
                        currentLocation = locationResult.getLastLocation();

                        LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(current)
                                .zoom(18)
                                .build();

                        if (foregroundActivity && mMap != null) {
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            mMap.setMyLocationEnabled(true);
                        }
                    }
                }
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

    }

    private void setupCamera() {
        binding.cameraView.setLifecycleOwner(this);
        binding.cameraView.setFacing(Facing.FRONT);
        binding.cameraView.setVisibility(View.INVISIBLE);
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
                                        Toast.makeText(StartTrip.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
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

            Log.d("euler x", String.valueOf(rotX));
            Log.d("euler y", String.valueOf(rotY));
            Log.d("euler z", String.valueOf(rotZ));
            Log.d("--------------", "----------------------------------------------");
            Log.d("--------------", "----------------------------------------------");

            if(rightEyeOpenProb<0.5){
                drowsinessCounter = drowsinessCounter + 1;
                if(drowsinessCounter == 20){
                    Log.d("drowsiness detected", "");
                    Toast.makeText(StartTrip.this, "Drowsiness detected", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                drowsinessCounter = 0;
            }
        }

    }

    public void initializeAccelerometer() {

        array = new ArrayList<>();
        fallDetected = 0;
        countFall = 0;
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (accelerometerSensor == null){
            Toast.makeText(this, "The device has no Gyroscope", Toast.LENGTH_SHORT).show();
            finish();
        }
        accelerometerEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                int index = 0;
                //Y-Axis
                if (array.size()<70){
                    array.add(sensorEvent.values[1]);
                }
                else{
                    array.remove(0);
                    array.add(sensorEvent.values[1]);
                }
                for (int i=0; i<array.size()-1; i++){
                    if(array.get(i)>0 && array.get(i+1)<0){
                        fallDetected = 1;                       //negative acceleration
                        break;
                    }
                    if(fallDetected==1){
                        if(array.get(i)<0 && array.get(i+1)>0){
                            fallDetected = 2;                   //positive acceleration
                            index = i;
                            break;
                        }
                    }
                    if(fallDetected==2){
                        for (int j = index; j< array.size()-1; j++){
                            if(array.get(j)>3 && array.get(j+1)<3){
                                fallDetected = 3;                //final acceleration
                                break;
                            }
                        }

                    }
                }
                if (fallDetected==3){
                    //Log.d("array", String.valueOf(array));
                    array.clear();
                    fallDetected=0;
                    countFall = countFall + 1;
                    Log.d("count fall: ", String.valueOf(countFall));
                    Toast.makeText(StartTrip.this, "Fall detected", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
    }

    private InputImage getInputImageFromFrame(Frame frame) {
        byte[] data = frame.getData();
        return InputImage.fromByteArray(data, frame.getSize().getWidth(), frame.getSize().getHeight(), frame.getRotation(), InputImage.IMAGE_FORMAT_NV21);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (REQUEST_CODE) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                }
                break;
            case REQUEST_RECORD_AUDIO_PERMISSION:
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                    finish();
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        foregroundActivity = false;
        mMap = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        foregroundActivity = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(accelerometerEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeTimes++;
        if(resumeTimes > 0) {
            Log.d("Resume times: ", String.valueOf(resumeTimes));
            Toast.makeText(StartTrip.this, "Resume detected", Toast.LENGTH_SHORT).show();
        }
        sensorManager.registerListener(accelerometerEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
        foregroundActivity = true;
    }
}
