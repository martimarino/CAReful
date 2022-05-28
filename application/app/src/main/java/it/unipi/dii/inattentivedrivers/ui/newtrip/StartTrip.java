package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import it.unipi.dii.inattentivedrivers.R;
import it.unipi.dii.inattentivedrivers.databinding.StartTripBinding;
import it.unipi.dii.inattentivedrivers.sensors.CameraManager;
import it.unipi.dii.inattentivedrivers.sensors.GpsManager;
import it.unipi.dii.inattentivedrivers.sensors.MicrophoneManager;
import it.unipi.dii.inattentivedrivers.sensors.MotionManager;
import it.unipi.dii.inattentivedrivers.ui.history.Trip;


public class StartTrip extends AppCompatActivity implements OnMapReadyCallback {

    boolean camSelected, magSelected, motSelected, micSelected;

    MotionManager mot;
    CameraManager cam;
    GpsManager gps;
    MicrophoneManager mic;

    /* Map variables */
    public static GoogleMap mMap;
    public static StartTripBinding binding;

    private static final int REQUEST_CODE = 101;

    /* General variables */
    private boolean foregroundActivity;
    public int resumeTimes = -1;        //avoid first activity start call

    /* Attention index */
    Timer t;
    public float disattentionLevel;
    public float prevDisattentionLevel = 0;
    public float attentionLevel = 100f;
    public static int samplingPeriod = 60;     //in seconds
    float alpha = 0.7f;
    float noise, drow, head, fall, usage, curv, speed;

    public static final int maxDisattention = 10;
    public static final float speedNorm = 4;
    public static final float curvNorm = 3;
    public static final float headNorm = 15;
    public static final float drowNorm = 30;
    public static final float noiseNorm = 12;
    public static final float fallNorm = 5;
    float progress;
    ProgressBarAnimation anim;
    ProgressBar pB;

    /* End of trip */
    Button stop;
    public LocalDateTime timeDeparture;
    public LocalDateTime timeArrival;
    public int score;
    public String departure = null;
    public String arrival;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        /* build page */
        super.onCreate(savedInstanceState);
        binding = StartTripBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        timeDeparture = LocalDateTime.now();
        score = 0;
        pB = binding.determinateBar;

        /* get parameters from new trip */
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            micSelected = extras.getBoolean("mic");
            magSelected = extras.getBoolean("mag");
            camSelected = extras.getBoolean("cam");
            motSelected = extras.getBoolean("mot");
        }

        Dexter.withContext(getApplicationContext())
                .withPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        /* constructors called only if sensors are selected in NewTrip */
                        if (micSelected) {
                            mic = new MicrophoneManager(StartTrip.this);
                            ActivityCompat.requestPermissions(StartTrip.this, MicrophoneManager.sendRecPermission, MicrophoneManager.REQUEST_RECORD_AUDIO);
                        }

                        if (motSelected || magSelected)
                            mot = new MotionManager(StartTrip.this, getApplicationContext(), motSelected, magSelected);

                        if(camSelected)
                            cam = new CameraManager(StartTrip.this, binding);

                        /* gps always available */
                        gps = new GpsManager(StartTrip.this);
                        getCurrentLocation();

                        setStopButton();
                        startAttentionDetection();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        Toast.makeText(StartTrip.this, "Permissions Required!", Toast.LENGTH_SHORT).show();
                    }
                }).check();

    }

    public void startAttentionDetection(){

        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                /* get counters value */
                if(micSelected)
                    noise = mic.getNoiseDetections();       //12
                if(camSelected) {
                    drow = cam.getDrowsinessCounter();      //30
                    head = cam.getTurnedHeadCounter();      //12
                }
                if(motSelected) {
                    usage = (mot.getUsageDetected()) ? 1 : 0;        //1
                    fall = mot.getCountFall();             //5
                }
                if(magSelected)
                    curv = mot.getCurvatureIndex();         // 1 - 3
                speed = gps.getAvgSpeed();          //1 - 4

                /* calculate index */
                disattentionLevel = (speed/speedNorm + curv/ curvNorm)* (head/ headNorm + drow/ drowNorm + usage + noise/ noiseNorm + fall/ fallNorm);
                disattentionLevel = alpha * disattentionLevel + (1-alpha) * prevDisattentionLevel;
                prevDisattentionLevel = disattentionLevel;
                attentionLevel = maxDisattention - (disattentionLevel);

                /* update prgress bar */
                progress = attentionLevel * 100 / 10;
                anim = new ProgressBarAnimation(pB, pB.getProgress(), progress);
                anim.setDuration(3000);
                pB.startAnimation(anim);

                /* reset counters */
                if(micSelected)
                    mic.setNoiseDetections(0);
                if(camSelected) {
                    cam.setDrowsinessCounter(0);
                    cam.setTurnedHeadCounter(0);
                }
                if(motSelected) {
                    mot.setUsageDetected(false);
                    mot.setCountFall(0);
                }
            }
        }, 10,60000);     /* the disattention level is recompute every minute */
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setStopButton() {               /* function related to Stop Trip button */
        stop = binding.stop;
        stop.setOnClickListener(view -> {
            arrival = gps.getAddress(this);
            timeArrival = LocalDateTime.now();
            score = (int) progress;
            Trip trip = new Trip(String.valueOf(timeDeparture), String.valueOf(timeArrival), String.valueOf(score), departure, arrival, getApplicationContext());
            trip.insertData();
            if(motSelected || magSelected)
                mot.unregisterListeners(motSelected, magSelected);
            this.onBackPressed();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        gps.updateMap(this, mMap, foregroundActivity);
        mMap.addMarker(new MarkerOptions().position(gps.current).title("Departure") .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

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
        Task<Location> task = GpsManager.fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {

            if (location != null) {
                GpsManager.currentLocation = location;
                if (departure == null){
                    departure = gps.getAddress(this);
                }
                GpsManager.currentLocation = location;
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                assert supportMapFragment != null;
                supportMapFragment.getMapAsync(StartTrip.this);
            }

        });

        /* location callback set */
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(MapsActivity.getInterval() * 1000L);
        locationRequest.setFastestInterval(MapsActivity.getInterval() * 1000L);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //                        print();
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null)
                    return;

                for (Location location : locationResult.getLocations()) {
                    if (location != null) {

//                        print();

                        GpsManager.currentLocation = locationResult.getLastLocation();
                        gps.updateMap(StartTrip.this, mMap, foregroundActivity);
                        gps.calculateAvgSpeed();
                    }
                }
            }
        };
        gps.fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mot != null)
            mot.unregisterListeners(motSelected, magSelected);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeTimes++;
        if(resumeTimes > 0) {
            Log.d("Resume times: ", String.valueOf(resumeTimes));
            if(motSelected || magSelected)
                mot.registerListeners(motSelected, magSelected);
        }
        foregroundActivity = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        t.cancel();
        if(motSelected || magSelected)
            mot.unregisterListeners(motSelected, magSelected);
        foregroundActivity = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        t.cancel();
        foregroundActivity = false;
        mMap = null;
    }

    private void print() {

        Log.d("Attention level: ", String.valueOf(attentionLevel));
        Log.d("Prev disatttention: ", String.valueOf(prevDisattentionLevel));
        Log.d("Disattention level: ", String.valueOf(disattentionLevel));
        Log.d("Speed: ", String.valueOf(speed));
        Log.d("Noise: ", String.valueOf(noise));
        Log.d("Head: ", String.valueOf(head));
        Log.d("Drow: ", String.valueOf(drow));
        Log.d("Curve: ", String.valueOf(curv));
        Log.d("Usage: ", String.valueOf(usage));
        Log.d("Fall: ", String.valueOf(fall));

    }
}
